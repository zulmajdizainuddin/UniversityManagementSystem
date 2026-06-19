/*
 * Model class for Course Evaluation Answer
 * Represents a single answer to a question in an evaluation
 * 
 * @author ZULMAJDI
 */
package com.project.model;

public class CourseEvaluationAnswer {
    
    private int answerId;
    private int evaluationId;
    private int questionId;
    private Integer ratingValue; // For RATING questions (1-5), null for TEXT
    private String textValue;    // For TEXT questions, null for RATING
    
    public CourseEvaluationAnswer() {
    }
    
    public CourseEvaluationAnswer(int evaluationId, int questionId, Integer ratingValue, String textValue) {
        this.evaluationId = evaluationId;
        this.questionId = questionId;
        this.ratingValue = ratingValue;
        this.textValue = textValue;
    }
    
    // Getters and Setters
    public int getAnswerId() {
        return answerId;
    }
    
    public void setAnswerId(int answerId) {
        this.answerId = answerId;
    }
    
    public int getEvaluationId() {
        return evaluationId;
    }
    
    public void setEvaluationId(int evaluationId) {
        this.evaluationId = evaluationId;
    }
    
    public int getQuestionId() {
        return questionId;
    }
    
    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }
    
    public Integer getRatingValue() {
        return ratingValue;
    }
    
    public void setRatingValue(Integer ratingValue) {
        this.ratingValue = ratingValue;
    }
    
    public String getTextValue() {
        return textValue;
    }
    
    public void setTextValue(String textValue) {
        this.textValue = textValue;
    }
}

