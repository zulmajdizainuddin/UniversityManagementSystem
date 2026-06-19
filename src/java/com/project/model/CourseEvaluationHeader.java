/*
 * Model class for Course Evaluation Header
 * Represents a single evaluation submission (one per student per subject)
 * 
 * @author ZULMAJDI
 */
package com.project.model;

import java.sql.Timestamp;

public class CourseEvaluationHeader {
    
    private int evaluationId;
    private int studentId;
    private int subjectId;
    private int lecturerId;
    private Timestamp submittedAt;
    
    public CourseEvaluationHeader() {
    }
    
    public CourseEvaluationHeader(int studentId, int subjectId, int lecturerId) {
        this.studentId = studentId;
        this.subjectId = subjectId;
        this.lecturerId = lecturerId;
    }
    
    // Getters and Setters
    public int getEvaluationId() {
        return evaluationId;
    }
    
    public void setEvaluationId(int evaluationId) {
        this.evaluationId = evaluationId;
    }
    
    public int getStudentId() {
        return studentId;
    }
    
    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }
    
    public int getSubjectId() {
        return subjectId;
    }
    
    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
    }
    
    public int getLecturerId() {
        return lecturerId;
    }
    
    public void setLecturerId(int lecturerId) {
        this.lecturerId = lecturerId;
    }
    
    public Timestamp getSubmittedAt() {
        return submittedAt;
    }
    
    public void setSubmittedAt(Timestamp submittedAt) {
        this.submittedAt = submittedAt;
    }
}

