/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.project.model;

/**
 *
 * @author ZULMAJDI
 */
public class Grade {

    private int gradeId;
    private int studentId;
    private int subjectId;
    private double score;
    private int lecturerId;       // Added lecturerId
    private String subjectCode;   // For display
    private String subjectName;   // For display

    public Grade() {
    }

    public Grade(int gradeId, int studentId, int subjectId, double score, int lecturerId, String subjectName) {
        this.gradeId = gradeId;
        this.studentId = studentId;
        this.subjectId = subjectId;
        this.score = score;
        this.lecturerId = lecturerId;
        this.subjectName = subjectName;
    }

    // Getters and setters
    public int getGradeId() {
        return gradeId;
    }

    public void setGradeId(int gradeId) {
        this.gradeId = gradeId;
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

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public int getLecturerId() {
        return lecturerId;
    }

    public void setLecturerId(int lecturerId) {
        this.lecturerId = lecturerId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getSubjectCode() {
        return subjectCode;
    }

    public void setSubjectCode(String subjectCode) {
        this.subjectCode = subjectCode;
    }
}
