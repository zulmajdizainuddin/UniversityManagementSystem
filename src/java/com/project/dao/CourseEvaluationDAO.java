/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.project.dao;

import com.project.model.CourseEvaluation;
import java.sql.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Course Evaluation operations
 * 
 * @author ZULMAJDI
 */
public class CourseEvaluationDAO extends BaseDAO {

    /**
     * Ensure the course_evaluation table exists, create it if it doesn't
     */
    private void ensureTableExists() throws SQLException {
        try (Connection conn = getConnection()) {
            // Try to query the table - if it doesn't exist, we'll get an exception
            boolean tableExists = false;
            try {
                String testSql = "SELECT COUNT(*) FROM course_evaluation";
                try (PreparedStatement testStmt = conn.prepareStatement(testSql);
                     ResultSet testRs = testStmt.executeQuery()) {
                    tableExists = true; // If query succeeds, table exists
                }
            } catch (SQLException e) {
                // Check if error is "table does not exist" (Derby error code: 42X05)
                if (e.getSQLState() != null && e.getSQLState().equals("42X05")) {
                    tableExists = false; // Table doesn't exist
                } else {
                    // Some other error - rethrow it
                    throw e;
                }
            }
            
            if (!tableExists) {
                // Create the table
                String createTableSql = "CREATE TABLE course_evaluation ("
                        + "evaluation_id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1) PRIMARY KEY, "
                        + "student_id INTEGER NOT NULL, "
                        + "subject_id INTEGER NOT NULL, "
                        + "lecturer_id INTEGER NOT NULL, "
                        + "semester VARCHAR(50) NOT NULL, "
                        + "q1 SMALLINT NOT NULL CHECK (q1 >= 1 AND q1 <= 5), "
                        + "q2 SMALLINT NOT NULL CHECK (q2 >= 1 AND q2 <= 5), "
                        + "q3 SMALLINT NOT NULL CHECK (q3 >= 1 AND q3 <= 5), "
                        + "q4 SMALLINT NOT NULL CHECK (q4 >= 1 AND q4 <= 5), "
                        + "q5 SMALLINT NOT NULL CHECK (q5 >= 1 AND q5 <= 5), "
                        + "q6 SMALLINT NOT NULL CHECK (q6 >= 1 AND q6 <= 5), "
                        + "q7 SMALLINT NOT NULL CHECK (q7 >= 1 AND q7 <= 5), "
                        + "q8 SMALLINT NOT NULL CHECK (q8 >= 1 AND q8 <= 5), "
                        + "q9 SMALLINT NOT NULL CHECK (q9 >= 1 AND q9 <= 5), "
                        + "q10 SMALLINT NOT NULL CHECK (q10 >= 1 AND q10 <= 5), "
                        + "q11 SMALLINT NOT NULL CHECK (q11 >= 1 AND q11 <= 5), "
                        + "q12 SMALLINT NOT NULL CHECK (q12 >= 1 AND q12 <= 5), "
                        + "q13 SMALLINT NOT NULL CHECK (q13 >= 1 AND q13 <= 5), "
                        + "q14 SMALLINT NOT NULL CHECK (q14 >= 1 AND q14 <= 5), "
                        + "likes_comment CLOB, "
                        + "improvement_comment CLOB, "
                        + "other_comment CLOB, "
                        + "submitted_at TIMESTAMP NOT NULL, "
                        + "FOREIGN KEY (student_id) REFERENCES users(user_id), "
                        + "FOREIGN KEY (subject_id) REFERENCES subjects(subject_id), "
                        + "FOREIGN KEY (lecturer_id) REFERENCES users(user_id)"
                        + ")";
                
                try (PreparedStatement createStmt = conn.prepareStatement(createTableSql)) {
                    createStmt.executeUpdate();
                }
                
                // Create unique index for duplicate prevention (student + subject only, not semester)
                String createIndexSql = "CREATE UNIQUE INDEX idx_unique_evaluation ON course_evaluation(student_id, subject_id)";
                try (PreparedStatement indexStmt = conn.prepareStatement(createIndexSql)) {
                    indexStmt.executeUpdate();
                }
                
                // Create index for lecturer queries
                String createLecturerIndexSql = "CREATE INDEX idx_lecturer_subject_semester ON course_evaluation(lecturer_id, subject_id, semester)";
                try (PreparedStatement lecturerIndexStmt = conn.prepareStatement(createLecturerIndexSql)) {
                    lecturerIndexStmt.executeUpdate();
                }
            }
        }
    }

    /**
     * Submit a new course evaluation
     * Validates that student hasn't already submitted for this subject (one evaluation per student per subject)
     */
    public void submitEvaluation(CourseEvaluation evaluation) throws Exception {
        ensureTableExists();
        // Check for duplicate submission - only check student_id and subject_id (semester is not used for duplicate check)
        String checkSql = "SELECT COUNT(*) FROM course_evaluation WHERE student_id = ? AND subject_id = ?";
        try (Connection conn = getConnection(); 
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            
            checkStmt.setInt(1, evaluation.getStudentId());
            checkStmt.setInt(2, evaluation.getSubjectId());
            
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    throw new Exception("You have already submitted an evaluation for this subject.");
                }
            }
        }

        // Insert new evaluation
        String sql = "INSERT INTO course_evaluation (student_id, subject_id, lecturer_id, semester, "
                + "q1, q2, q3, q4, q5, q6, q7, q8, q9, q10, q11, q12, q13, q14, "
                + "likes_comment, improvement_comment, other_comment, submitted_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";
        
        try (Connection conn = getConnection(); 
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, evaluation.getStudentId());
            ps.setInt(2, evaluation.getSubjectId());
            ps.setInt(3, evaluation.getLecturerId());
            ps.setString(4, evaluation.getSemester());
            ps.setInt(5, evaluation.getQ1());
            ps.setInt(6, evaluation.getQ2());
            ps.setInt(7, evaluation.getQ3());
            ps.setInt(8, evaluation.getQ4());
            ps.setInt(9, evaluation.getQ5());
            ps.setInt(10, evaluation.getQ6());
            ps.setInt(11, evaluation.getQ7());
            ps.setInt(12, evaluation.getQ8());
            ps.setInt(13, evaluation.getQ9());
            ps.setInt(14, evaluation.getQ10());
            ps.setInt(15, evaluation.getQ11());
            ps.setInt(16, evaluation.getQ12());
            ps.setInt(17, evaluation.getQ13());
            ps.setInt(18, evaluation.getQ14());
            
            // Handle nullable comments
            if (evaluation.getLikesComment() != null && !evaluation.getLikesComment().trim().isEmpty()) {
                ps.setString(19, evaluation.getLikesComment().trim());
            } else {
                ps.setNull(19, Types.CLOB);
            }
            
            if (evaluation.getImprovementComment() != null && !evaluation.getImprovementComment().trim().isEmpty()) {
                ps.setString(20, evaluation.getImprovementComment().trim());
            } else {
                ps.setNull(20, Types.CLOB);
            }
            
            if (evaluation.getOtherComment() != null && !evaluation.getOtherComment().trim().isEmpty()) {
                ps.setString(21, evaluation.getOtherComment().trim());
            } else {
                ps.setNull(21, Types.CLOB);
            }
            
            ps.executeUpdate();
        }
    }

    /**
     * Get all evaluations for a lecturer's subject in a specific semester
     */
    public List<CourseEvaluation> getEvaluationsForLecturer(int lecturerId, int subjectId, String semester) throws Exception {
        ensureTableExists();
        List<CourseEvaluation> evaluations = new ArrayList<>();
        String sql = "SELECT * FROM course_evaluation WHERE lecturer_id = ? AND subject_id = ? ORDER BY submitted_at DESC";
        
        try (Connection conn = getConnection(); 
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, lecturerId);
            ps.setInt(2, subjectId);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    evaluations.add(mapResultSetToEvaluation(rs));
                }
            }
        }
        return evaluations;
    }

    /**
     * Get evaluation summary (averages and count) for a lecturer's subject
     */
    public EvaluationSummary getEvaluationSummary(int lecturerId, int subjectId, String semester) throws Exception {
        ensureTableExists();
        String sql = "SELECT "
                + "COUNT(*) as total_responses, "
                + "AVG(q1) as avg_q1, AVG(q2) as avg_q2, AVG(q3) as avg_q3, AVG(q4) as avg_q4, "
                + "AVG(q5) as avg_q5, AVG(q6) as avg_q6, AVG(q7) as avg_q7, AVG(q8) as avg_q8, "
                + "AVG(q9) as avg_q9, AVG(q10) as avg_q10, AVG(q11) as avg_q11, AVG(q12) as avg_q12, "
                + "AVG(q13) as avg_q13, AVG(q14) as avg_q14 "
                + "FROM course_evaluation "
                + "WHERE lecturer_id = ? AND subject_id = ?";
        
        try (Connection conn = getConnection(); 
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, lecturerId);
            ps.setInt(2, subjectId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    EvaluationSummary summary = new EvaluationSummary();
                    summary.totalResponses = rs.getInt("total_responses");
                    summary.avgQ1 = rs.getDouble("avg_q1");
                    summary.avgQ2 = rs.getDouble("avg_q2");
                    summary.avgQ3 = rs.getDouble("avg_q3");
                    summary.avgQ4 = rs.getDouble("avg_q4");
                    summary.avgQ5 = rs.getDouble("avg_q5");
                    summary.avgQ6 = rs.getDouble("avg_q6");
                    summary.avgQ7 = rs.getDouble("avg_q7");
                    summary.avgQ8 = rs.getDouble("avg_q8");
                    summary.avgQ9 = rs.getDouble("avg_q9");
                    summary.avgQ10 = rs.getDouble("avg_q10");
                    summary.avgQ11 = rs.getDouble("avg_q11");
                    summary.avgQ12 = rs.getDouble("avg_q12");
                    summary.avgQ13 = rs.getDouble("avg_q13");
                    summary.avgQ14 = rs.getDouble("avg_q14");
                    
                    // Calculate overall course rating (average of q5-q13)
                    double courseSum = summary.avgQ5 + summary.avgQ6 + summary.avgQ7 + summary.avgQ8 + 
                                       summary.avgQ9 + summary.avgQ10 + summary.avgQ11 + summary.avgQ12 + summary.avgQ13;
                    summary.overallCourseRating = courseSum / 9.0;
                    
                    // Calculate overall lecturer rating (average of q1-q4, q14)
                    double lecturerSum = summary.avgQ1 + summary.avgQ2 + summary.avgQ3 + summary.avgQ4 + summary.avgQ14;
                    summary.overallLecturerRating = lecturerSum / 5.0;
                    
                    return summary;
                }
            }
        }
        return new EvaluationSummary(); // Return empty summary if no data
    }

    /**
     * Check if student has already submitted evaluation for subject (semester not used for duplicate check)
     */
    public boolean hasStudentSubmitted(int studentId, int subjectId, String semester) throws Exception {
        ensureTableExists();
        // Note: semester parameter kept for compatibility but not used in query
        String sql = "SELECT COUNT(*) FROM course_evaluation WHERE student_id = ? AND subject_id = ?";
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
     * Get lecturer ID for a subject (from lecturer_subjects table)
     */
    public Integer getLecturerIdForSubject(int subjectId) throws Exception {
        String sql = "SELECT lecturer_id FROM lecturer_subjects WHERE subject_id = ? FETCH FIRST 1 ROW ONLY";
        try (Connection conn = getConnection(); 
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, subjectId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("lecturer_id");
                }
            }
        }
        return null;
    }

    /**
     * Get all subjects taught by a lecturer
     */
    public List<Integer> getSubjectIdsByLecturer(int lecturerId) throws Exception {
        List<Integer> subjectIds = new ArrayList<>();
        String sql = "SELECT subject_id FROM lecturer_subjects WHERE lecturer_id = ?";
        try (Connection conn = getConnection(); 
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, lecturerId);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    subjectIds.add(rs.getInt("subject_id"));
                }
            }
        }
        return subjectIds;
    }

    private CourseEvaluation mapResultSetToEvaluation(ResultSet rs) throws SQLException {
        CourseEvaluation eval = new CourseEvaluation();
        eval.setEvaluationId(rs.getInt("evaluation_id"));
        eval.setStudentId(rs.getInt("student_id"));
        eval.setSubjectId(rs.getInt("subject_id"));
        eval.setLecturerId(rs.getInt("lecturer_id"));
        eval.setSemester(rs.getString("semester"));
        eval.setQ1(rs.getInt("q1"));
        eval.setQ2(rs.getInt("q2"));
        eval.setQ3(rs.getInt("q3"));
        eval.setQ4(rs.getInt("q4"));
        eval.setQ5(rs.getInt("q5"));
        eval.setQ6(rs.getInt("q6"));
        eval.setQ7(rs.getInt("q7"));
        eval.setQ8(rs.getInt("q8"));
        eval.setQ9(rs.getInt("q9"));
        eval.setQ10(rs.getInt("q10"));
        eval.setQ11(rs.getInt("q11"));
        eval.setQ12(rs.getInt("q12"));
        eval.setQ13(rs.getInt("q13"));
        eval.setQ14(rs.getInt("q14"));
        eval.setLikesComment(rs.getString("likes_comment"));
        eval.setImprovementComment(rs.getString("improvement_comment"));
        eval.setOtherComment(rs.getString("other_comment"));
        eval.setSubmittedAt(rs.getTimestamp("submitted_at"));
        return eval;
    }

    /**
     * Inner class to hold evaluation summary statistics
     */
    public static class EvaluationSummary {
        public int totalResponses = 0;
        public double avgQ1, avgQ2, avgQ3, avgQ4, avgQ5, avgQ6, avgQ7, avgQ8, 
                     avgQ9, avgQ10, avgQ11, avgQ12, avgQ13, avgQ14;
        public double overallCourseRating = 0.0;
        public double overallLecturerRating = 0.0;
    }
}

