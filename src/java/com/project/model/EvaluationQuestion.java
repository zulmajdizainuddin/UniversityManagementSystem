/*
 * Model class for Evaluation Question
 * Represents a dynamic question in the course evaluation form
 * 
 * @author ZULMAJDI
 */
package com.project.model;

import java.sql.Timestamp;

public class EvaluationQuestion {
    
    private int questionId;
    private String questionText;
    private String questionType; // 'RATING' or 'TEXT'
    private boolean isRequired;
    private boolean isActive;
    private int displayOrder;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    
    public EvaluationQuestion() {
    }
    
    public EvaluationQuestion(String questionText, String questionType, boolean isRequired, boolean isActive, int displayOrder) {
        this.questionText = questionText;
        this.questionType = questionType;
        this.isRequired = isRequired;
        this.isActive = isActive;
        this.displayOrder = displayOrder;
    }
    
    // Getters and Setters
    public int getQuestionId() {
        return questionId;
    }
    
    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }
    
    public String getQuestionText() {
        return questionText;
    }
    
    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }
    
    public String getQuestionType() {
        return questionType;
    }
    
    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }
    
    public boolean isRequired() {
        return isRequired;
    }
    
    public void setRequired(boolean isRequired) {
        this.isRequired = isRequired;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }
    
    public int getDisplayOrder() {
        return displayOrder;
    }
    
    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }
    
    public Timestamp getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
    
    public Timestamp getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
}

