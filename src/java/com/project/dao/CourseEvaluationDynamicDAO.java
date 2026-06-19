/*
 * Data Access Object for Dynamic Course Evaluation operations
 * Handles the new dynamic evaluation system (header + answers)
 * 
 * @author ZULMAJDI
 */
package com.project.dao;

import com.project.model.CourseEvaluationAnswer;
import com.project.model.CourseEvaluationHeader;
import com.project.model.EvaluationQuestion;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CourseEvaluationDynamicDAO extends BaseDAO {
    
    /**
     * Ensure the course_evaluation_header and course_evaluation_answer tables exist
     */
    private void ensureTablesExist() throws SQLException {
        try (Connection conn = getConnection()) {
            // Check and create course_evaluation_header
            boolean headerTableExists = false;
            try {
                String testSql = "SELECT COUNT(*) FROM course_evaluation_header";
                try (PreparedStatement testStmt = conn.prepareStatement(testSql);
                     ResultSet testRs = testStmt.executeQuery()) {
                    headerTableExists = true;
                }
            } catch (SQLException e) {
                if (e.getSQLState() != null && e.getSQLState().equals("42X05")) {
                    headerTableExists = false;
                } else {
                    throw e;
                }
            }
            
            if (!headerTableExists) {
                String createHeaderSql = "CREATE TABLE course_evaluation_header ("
                        + "evaluation_id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1) PRIMARY KEY, "
                        + "student_id INTEGER NOT NULL, "
                        + "subject_id INTEGER NOT NULL, "
                        + "lecturer_id INTEGER NOT NULL, "
                        + "submitted_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, "
                        + "FOREIGN KEY (student_id) REFERENCES users(user_id), "
                        + "FOREIGN KEY (subject_id) REFERENCES subjects(subject_id), "
                        + "FOREIGN KEY (lecturer_id) REFERENCES users(user_id)"
                        + ")";
                
                try (PreparedStatement createStmt = conn.prepareStatement(createHeaderSql)) {
                    createStmt.executeUpdate();
                }
                
                // Create unique index for duplicate prevention
                String createIndexSql = "CREATE UNIQUE INDEX idx_unique_evaluation_header ON course_evaluation_header(student_id, subject_id)";
                try (PreparedStatement indexStmt = conn.prepareStatement(createIndexSql)) {
                    indexStmt.executeUpdate();
                }
            }
            
            // Check and create course_evaluation_answer
            boolean answerTableExists = false;
            try {
                String testSql = "SELECT COUNT(*) FROM course_evaluation_answer";
                try (PreparedStatement testStmt = conn.prepareStatement(testSql);
                     ResultSet testRs = testStmt.executeQuery()) {
                    answerTableExists = true;
                }
            } catch (SQLException e) {
                if (e.getSQLState() != null && e.getSQLState().equals("42X05")) {
                    answerTableExists = false;
                } else {
                    throw e;
                }
            }
            
            if (!answerTableExists) {
                String createAnswerSql = "CREATE TABLE course_evaluation_answer ("
                        + "answer_id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1) PRIMARY KEY, "
                        + "evaluation_id INTEGER NOT NULL, "
                        + "question_id INTEGER NOT NULL, "
                        + "rating_value INTEGER CHECK (rating_value >= 1 AND rating_value <= 5), "
                        + "text_value CLOB, "
                        + "FOREIGN KEY (evaluation_id) REFERENCES course_evaluation_header(evaluation_id) ON DELETE CASCADE, "
                        + "FOREIGN KEY (question_id) REFERENCES evaluation_question(question_id)"
                        + ")";
                
                try (PreparedStatement createStmt = conn.prepareStatement(createAnswerSql)) {
                    createStmt.executeUpdate();
                }
                
                // Create index for queries
                String createIndexSql = "CREATE INDEX idx_evaluation_id ON course_evaluation_answer(evaluation_id)";
                try (PreparedStatement indexStmt = conn.prepareStatement(createIndexSql)) {
                    indexStmt.executeUpdate();
                }
            }
        }
    }
    
    /**
     * Submit a new dynamic evaluation
     * @param header The evaluation header
     * @param answers Map of questionId -> answer (Integer for rating, String for text)
     * @throws Exception if duplicate submission or validation fails
     */
    public void submitEvaluation(CourseEvaluationHeader header, Map<Integer, Object> answers) throws Exception {
        ensureTablesExist();
        
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            
            try {
                // Check for duplicate submission
                String checkSql = "SELECT COUNT(*) FROM course_evaluation_header WHERE student_id = ? AND subject_id = ?";
                try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                    checkStmt.setInt(1, header.getStudentId());
                    checkStmt.setInt(2, header.getSubjectId());
                    try (ResultSet rs = checkStmt.executeQuery()) {
                        if (rs.next() && rs.getInt(1) > 0) {
                            throw new Exception("You have already submitted an evaluation for this subject.");
                        }
                    }
                }
                
                // Insert header
                String headerSql = "INSERT INTO course_evaluation_header (student_id, subject_id, lecturer_id, submitted_at) "
                        + "VALUES (?, ?, ?, CURRENT_TIMESTAMP)";
                int evaluationId;
                
                try (PreparedStatement headerPs = conn.prepareStatement(headerSql, Statement.RETURN_GENERATED_KEYS)) {
                    headerPs.setInt(1, header.getStudentId());
                    headerPs.setInt(2, header.getSubjectId());
                    headerPs.setInt(3, header.getLecturerId());
                    headerPs.executeUpdate();
                    
                    try (ResultSet rs = headerPs.getGeneratedKeys()) {
                        if (rs.next()) {
                            evaluationId = rs.getInt(1);
                        } else {
                            throw new Exception("Failed to create evaluation header.");
                        }
                    }
                }
                
                // Insert answers
                String answerSql = "INSERT INTO course_evaluation_answer (evaluation_id, question_id, rating_value, text_value) "
                        + "VALUES (?, ?, ?, ?)";
                
                try (PreparedStatement answerPs = conn.prepareStatement(answerSql)) {
                    for (Map.Entry<Integer, Object> entry : answers.entrySet()) {
                        int questionId = entry.getKey();
                        Object answer = entry.getValue();
                        
                        answerPs.setInt(1, evaluationId);
                        answerPs.setInt(2, questionId);
                        
                        if (answer instanceof Integer) {
                            // Rating question
                            answerPs.setInt(3, (Integer) answer);
                            answerPs.setNull(4, Types.CLOB);
                        } else if (answer instanceof String) {
                            // Text question
                            answerPs.setNull(3, Types.INTEGER);
                            String textValue = (String) answer;
                            if (textValue != null && !textValue.trim().isEmpty()) {
                                answerPs.setString(4, textValue.trim());
                            } else {
                                answerPs.setNull(4, Types.CLOB);
                            }
                        } else {
                            answerPs.setNull(3, Types.INTEGER);
                            answerPs.setNull(4, Types.CLOB);
                        }
                        
                        answerPs.executeUpdate();
                    }
                }
                
                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }
    
    /**
     * Check if student has already submitted evaluation for subject
     */
    public boolean hasStudentSubmitted(int studentId, int subjectId) throws Exception {
        ensureTablesExist();
        String sql = "SELECT COUNT(*) FROM course_evaluation_header WHERE student_id = ? AND subject_id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, studentId);
            ps.setInt(2, subjectId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
    
    /**
     * Get evaluation summary for lecturer's subject
     * Returns statistics for each rating question
     */
    public EvaluationSummary getEvaluationSummary(int lecturerId, int subjectId) throws Exception {
        ensureTablesExist();
        EvaluationSummary summary = new EvaluationSummary();
        
        // Get total responses
        String countSql = "SELECT COUNT(*) FROM course_evaluation_header WHERE lecturer_id = ? AND subject_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(countSql)) {
            
            ps.setInt(1, lecturerId);
            ps.setInt(2, subjectId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    summary.totalResponses = rs.getInt(1);
                }
            }
        }
        
        if (summary.totalResponses == 0) {
            return summary;
        }
        
        // Get averages for each rating question
        String avgSql = "SELECT q.question_id, q.question_text, AVG(a.rating_value) as avg_rating, COUNT(a.rating_value) as response_count "
                + "FROM evaluation_question q "
                + "LEFT JOIN course_evaluation_answer a ON q.question_id = a.question_id "
                + "LEFT JOIN course_evaluation_header h ON a.evaluation_id = h.evaluation_id "
                + "WHERE q.question_type = 'RATING' AND q.is_active = 1 "
                + "AND (h.lecturer_id = ? AND h.subject_id = ? OR h.lecturer_id IS NULL) "
                + "GROUP BY q.question_id, q.question_text, q.display_order "
                + "ORDER BY q.display_order";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(avgSql)) {
            
            ps.setInt(1, lecturerId);
            ps.setInt(2, subjectId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    QuestionStat stat = new QuestionStat();
                    stat.questionId = rs.getInt("question_id");
                    stat.questionText = rs.getString("question_text");
                    stat.avgRating = rs.getDouble("avg_rating");
                    stat.responseCount = rs.getInt("response_count");
                    summary.questionStats.add(stat);
                }
            }
        }
        
        return summary;
    }
    
    /**
     * Get all text answers for a lecturer's subject, grouped by question
     */
    public Map<Integer, List<String>> getTextAnswers(int lecturerId, int subjectId) throws Exception {
        ensureTablesExist();
        Map<Integer, List<String>> textAnswersByQuestion = new HashMap<>();
        
        String sql = "SELECT q.question_id, a.text_value "
                + "FROM evaluation_question q "
                + "JOIN course_evaluation_answer a ON q.question_id = a.question_id "
                + "JOIN course_evaluation_header h ON a.evaluation_id = h.evaluation_id "
                + "WHERE q.question_type = 'TEXT' AND q.is_active = 1 "
                + "AND h.lecturer_id = ? AND h.subject_id = ? "
                + "AND a.text_value IS NOT NULL AND CAST(a.text_value AS VARCHAR(32672)) <> '' "
                + "ORDER BY q.display_order, h.submitted_at DESC";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, lecturerId);
            ps.setInt(2, subjectId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int questionId = rs.getInt("question_id");
                    String textValue = rs.getString("text_value");
                    
                    textAnswersByQuestion.computeIfAbsent(questionId, k -> new ArrayList<>()).add(textValue);
                }
            }
        }
        
        return textAnswersByQuestion;
    }
    
    /**
     * Get all evaluations for a lecturer's subject
     */
    public List<CourseEvaluationHeader> getEvaluationsForLecturer(int lecturerId, int subjectId) throws Exception {
        ensureTablesExist();
        List<CourseEvaluationHeader> evaluations = new ArrayList<>();
        
        String sql = "SELECT * FROM course_evaluation_header WHERE lecturer_id = ? AND subject_id = ? ORDER BY submitted_at DESC";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, lecturerId);
            ps.setInt(2, subjectId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CourseEvaluationHeader header = new CourseEvaluationHeader();
                    header.setEvaluationId(rs.getInt("evaluation_id"));
                    header.setStudentId(rs.getInt("student_id"));
                    header.setSubjectId(rs.getInt("subject_id"));
                    header.setLecturerId(rs.getInt("lecturer_id"));
                    header.setSubmittedAt(rs.getTimestamp("submitted_at"));
                    evaluations.add(header);
                }
            }
        }
        
        return evaluations;
    }
    
    /**
     * Get all answers for a specific evaluation
     */
    public Map<Integer, Object> getAnswersByEvaluationId(int evaluationId) throws Exception {
        ensureTablesExist();
        Map<Integer, Object> answers = new HashMap<>();
        
        String sql = "SELECT question_id, rating_value, text_value FROM course_evaluation_answer WHERE evaluation_id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, evaluationId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int questionId = rs.getInt("question_id");
                    Integer ratingValue = rs.getObject("rating_value") != null ? rs.getInt("rating_value") : null;
                    String textValue = rs.getString("text_value");
                    
                    if (ratingValue != null) {
                        answers.put(questionId, ratingValue);
                    } else if (textValue != null) {
                        answers.put(questionId, textValue);
                    }
                }
            }
        }
        
        return answers;
    }
    
    /**
     * Inner class for evaluation summary
     */
    public static class EvaluationSummary {
        public int totalResponses = 0;
        public List<QuestionStat> questionStats = new ArrayList<>();
    }
    
    /**
     * Inner class for question statistics
     */
    public static class QuestionStat {
        public int questionId;
        public String questionText;
        public double avgRating;
        public int responseCount;
    }
}

