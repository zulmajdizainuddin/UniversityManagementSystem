/*
 * Data Access Object for Evaluation Question operations
 * Handles CRUD for dynamic evaluation questions
 * 
 * @author ZULMAJDI
 */
package com.project.dao;

import com.project.model.EvaluationQuestion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EvaluationQuestionDAO extends BaseDAO {
    
    /**
     * Ensure the evaluation_question table exists, create it if it doesn't
     * Also seeds default questions if table is empty
     */
    private void ensureTableExists() throws SQLException {
        try (Connection conn = getConnection()) {
            boolean tableExists = false;
            try {
                String testSql = "SELECT COUNT(*) FROM evaluation_question";
                try (PreparedStatement testStmt = conn.prepareStatement(testSql);
                     ResultSet testRs = testStmt.executeQuery()) {
                    tableExists = true;
                }
            } catch (SQLException e) {
                if (e.getSQLState() != null && e.getSQLState().equals("42X05")) {
                    tableExists = false;
                } else {
                    throw e;
                }
            }
            
            if (!tableExists) {
                // Create the table
                String createTableSql = "CREATE TABLE evaluation_question ("
                        + "question_id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1) PRIMARY KEY, "
                        + "question_text VARCHAR(500) NOT NULL, "
                        + "question_type VARCHAR(10) NOT NULL CHECK (question_type IN ('RATING', 'TEXT')), "
                        + "is_required SMALLINT NOT NULL DEFAULT 1, "
                        + "is_active SMALLINT NOT NULL DEFAULT 1, "
                        + "display_order INTEGER NOT NULL, "
                        + "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, "
                        + "updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP"
                        + ")";
                
                try (PreparedStatement createStmt = conn.prepareStatement(createTableSql)) {
                    createStmt.executeUpdate();
                }
                
                // Create index for display order
                String createIndexSql = "CREATE INDEX idx_display_order ON evaluation_question(display_order)";
                try (PreparedStatement indexStmt = conn.prepareStatement(createIndexSql)) {
                    indexStmt.executeUpdate();
                }
                
                // Seed default questions
                seedDefaultQuestions(conn);
            } else {
                // Table exists, check if empty and seed if needed
                String countSql = "SELECT COUNT(*) FROM evaluation_question";
                try (PreparedStatement countStmt = conn.prepareStatement(countSql);
                     ResultSet countRs = countStmt.executeQuery()) {
                    if (countRs.next() && countRs.getInt(1) == 0) {
                        seedDefaultQuestions(conn);
                    }
                }
            }
        }
    }
    
    /**
     * Seed default 14 rating questions + 3 text questions
     */
    private void seedDefaultQuestions(Connection conn) throws SQLException {
        String[] ratingQuestions = {
            "The lecturer explains concepts clearly.",
            "The lecturer is well prepared for each class.",
            "The lecturer encourages student participation and questions.",
            "The lecturer is available and helpful outside class.",
            "The course objectives were clearly stated.",
            "The course content is relevant to my programme.",
            "The workload of this course is appropriate.",
            "Assessments match what was taught.",
            "Grading is fair and transparent.",
            "Feedback on assessments helps me improve.",
            "Lecture materials are well organized.",
            "Online resources are useful and easy to access.",
            "Overall, I am satisfied with this course.",
            "Overall, I am satisfied with this lecturer's teaching."
        };
        
        String[] textQuestions = {
            "What did you like most about this course?",
            "What can be improved?",
            "Any other comments?"
        };
        
        String insertSql = "INSERT INTO evaluation_question (question_text, question_type, is_required, is_active, display_order) VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
            int order = 1;
            
            // Insert rating questions (all required, active)
            for (String question : ratingQuestions) {
                ps.setString(1, question);
                ps.setString(2, "RATING");
                ps.setInt(3, 1); // required
                ps.setInt(4, 1); // active
                ps.setInt(5, order++);
                ps.executeUpdate();
            }
            
            // Insert text questions (optional, active)
            for (String question : textQuestions) {
                ps.setString(1, question);
                ps.setString(2, "TEXT");
                ps.setInt(3, 0); // optional
                ps.setInt(4, 1); // active
                ps.setInt(5, order++);
                ps.executeUpdate();
            }
        }
    }
    
    /**
     * Get all active questions ordered by display_order
     */
    public List<EvaluationQuestion> getActiveQuestions() throws Exception {
        ensureTableExists();
        List<EvaluationQuestion> questions = new ArrayList<>();
        String sql = "SELECT * FROM evaluation_question WHERE is_active = 1 ORDER BY display_order";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                questions.add(mapResultSetToQuestion(rs));
            }
        }
        return questions;
    }
    
    /**
     * Get all questions (including inactive) ordered by display_order
     */
    public List<EvaluationQuestion> getAllQuestions() throws Exception {
        ensureTableExists();
        List<EvaluationQuestion> questions = new ArrayList<>();
        String sql = "SELECT * FROM evaluation_question ORDER BY display_order";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                questions.add(mapResultSetToQuestion(rs));
            }
        }
        return questions;
    }
    
    /**
     * Get question by ID
     */
    public EvaluationQuestion getQuestionById(int questionId) throws Exception {
        ensureTableExists();
        String sql = "SELECT * FROM evaluation_question WHERE question_id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, questionId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToQuestion(rs);
                }
            }
        }
        return null;
    }
    
    /**
     * Add a new question
     */
    public int addQuestion(EvaluationQuestion question) throws Exception {
        ensureTableExists();
        // Get max display_order to append at end
        int maxOrder = getMaxDisplayOrder();
        
        String sql = "INSERT INTO evaluation_question (question_text, question_type, is_required, is_active, display_order, created_at, updated_at) "
                + "VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setString(1, question.getQuestionText());
            ps.setString(2, question.getQuestionType());
            ps.setInt(3, question.isRequired() ? 1 : 0);
            ps.setInt(4, question.isActive() ? 1 : 0);
            ps.setInt(5, question.getDisplayOrder() > 0 ? question.getDisplayOrder() : maxOrder + 1);
            
            ps.executeUpdate();
            
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return -1;
    }
    
    /**
     * Update a question
     */
    public void updateQuestion(EvaluationQuestion question) throws Exception {
        ensureTableExists();
        String sql = "UPDATE evaluation_question SET question_text = ?, question_type = ?, is_required = ?, "
                + "is_active = ?, display_order = ?, updated_at = CURRENT_TIMESTAMP WHERE question_id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, question.getQuestionText());
            ps.setString(2, question.getQuestionType());
            ps.setInt(3, question.isRequired() ? 1 : 0);
            ps.setInt(4, question.isActive() ? 1 : 0);
            ps.setInt(5, question.getDisplayOrder());
            ps.setInt(6, question.getQuestionId());
            
            ps.executeUpdate();
        }
    }
    
    /**
     * Soft delete (deactivate) a question
     */
    public void deactivateQuestion(int questionId) throws Exception {
        ensureTableExists();
        String sql = "UPDATE evaluation_question SET is_active = 0, updated_at = CURRENT_TIMESTAMP WHERE question_id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, questionId);
            ps.executeUpdate();
        }
    }
    
    /**
     * Delete a question (only if no answers exist)
     */
    public void deleteQuestion(int questionId) throws Exception {
        ensureTableExists();
        // Check if question has answers
        String checkSql = "SELECT COUNT(*) FROM course_evaluation_answer WHERE question_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement checkPs = conn.prepareStatement(checkSql)) {
            
            checkPs.setInt(1, questionId);
            try (ResultSet rs = checkPs.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    throw new Exception("Cannot delete question: it has existing answers. Use deactivate instead.");
                }
            }
        }
        
        // Delete the question
        String sql = "DELETE FROM evaluation_question WHERE question_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, questionId);
            ps.executeUpdate();
        }
    }
    
    /**
     * Reorder questions (swap display_order of two questions)
     */
    public void swapDisplayOrder(int questionId1, int questionId2) throws Exception {
        ensureTableExists();
        try (Connection conn = getConnection()) {
            // Get current orders
            int order1 = getDisplayOrder(conn, questionId1);
            int order2 = getDisplayOrder(conn, questionId2);
            
            // Swap them
            String sql1 = "UPDATE evaluation_question SET display_order = ?, updated_at = CURRENT_TIMESTAMP WHERE question_id = ?";
            String sql2 = "UPDATE evaluation_question SET display_order = ?, updated_at = CURRENT_TIMESTAMP WHERE question_id = ?";
            
            try (PreparedStatement ps1 = conn.prepareStatement(sql1);
                 PreparedStatement ps2 = conn.prepareStatement(sql2)) {
                
                ps1.setInt(1, order2);
                ps1.setInt(2, questionId1);
                ps1.executeUpdate();
                
                ps2.setInt(1, order1);
                ps2.setInt(2, questionId2);
                ps2.executeUpdate();
            }
        }
    }
    
    /**
     * Get max display_order
     */
    private int getMaxDisplayOrder() throws SQLException {
        String sql = "SELECT MAX(display_order) FROM evaluation_question";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }
    
    /**
     * Get display_order for a question
     */
    private int getDisplayOrder(Connection conn, int questionId) throws SQLException {
        String sql = "SELECT display_order FROM evaluation_question WHERE question_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, questionId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }
    
    /**
     * Map ResultSet to EvaluationQuestion
     */
    private EvaluationQuestion mapResultSetToQuestion(ResultSet rs) throws SQLException {
        EvaluationQuestion question = new EvaluationQuestion();
        question.setQuestionId(rs.getInt("question_id"));
        question.setQuestionText(rs.getString("question_text"));
        question.setQuestionType(rs.getString("question_type"));
        question.setRequired(rs.getInt("is_required") == 1);
        question.setActive(rs.getInt("is_active") == 1);
        question.setDisplayOrder(rs.getInt("display_order"));
        question.setCreatedAt(rs.getTimestamp("created_at"));
        question.setUpdatedAt(rs.getTimestamp("updated_at"));
        return question;
    }
}

