/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.project.model;

import java.sql.Timestamp;

/**
 * Model class for Course Evaluation
 * 
 * @author ZULMAJDI
 */
public class CourseEvaluation {

    private int evaluationId;
    private int studentId;
    private int subjectId;
    private int lecturerId;
    private String semester;
    private int q1;  // Lecturer explains concepts clearly
    private int q2;  // Lecturer is well prepared
    private int q3;  // Encourages participation
    private int q4;  // Available and helpful
    private int q5;  // Course objectives clearly stated
    private int q6;  // Content relevant to programme
    private int q7;  // Workload appropriate
    private int q8;  // Assessments match teaching
    private int q9;  // Grading fair and transparent
    private int q10; // Feedback helps improve
    private int q11; // Materials well organized
    private int q12; // Online resources useful
    private int q13; // Satisfied with course
    private int q14; // Satisfied with lecturer
    private String likesComment;
    private String improvementComment;
    private String otherComment;
    private Timestamp submittedAt;

    public CourseEvaluation() {
    }

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

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public int getQ1() {
        return q1;
    }

    public void setQ1(int q1) {
        this.q1 = q1;
    }

    public int getQ2() {
        return q2;
    }

    public void setQ2(int q2) {
        this.q2 = q2;
    }

    public int getQ3() {
        return q3;
    }

    public void setQ3(int q3) {
        this.q3 = q3;
    }

    public int getQ4() {
        return q4;
    }

    public void setQ4(int q4) {
        this.q4 = q4;
    }

    public int getQ5() {
        return q5;
    }

    public void setQ5(int q5) {
        this.q5 = q5;
    }

    public int getQ6() {
        return q6;
    }

    public void setQ6(int q6) {
        this.q6 = q6;
    }

    public int getQ7() {
        return q7;
    }

    public void setQ7(int q7) {
        this.q7 = q7;
    }

    public int getQ8() {
        return q8;
    }

    public void setQ8(int q8) {
        this.q8 = q8;
    }

    public int getQ9() {
        return q9;
    }

    public void setQ9(int q9) {
        this.q9 = q9;
    }

    public int getQ10() {
        return q10;
    }

    public void setQ10(int q10) {
        this.q10 = q10;
    }

    public int getQ11() {
        return q11;
    }

    public void setQ11(int q11) {
        this.q11 = q11;
    }

    public int getQ12() {
        return q12;
    }

    public void setQ12(int q12) {
        this.q12 = q12;
    }

    public int getQ13() {
        return q13;
    }

    public void setQ13(int q13) {
        this.q13 = q13;
    }

    public int getQ14() {
        return q14;
    }

    public void setQ14(int q14) {
        this.q14 = q14;
    }

    public String getLikesComment() {
        return likesComment;
    }

    public void setLikesComment(String likesComment) {
        this.likesComment = likesComment;
    }

    public String getImprovementComment() {
        return improvementComment;
    }

    public void setImprovementComment(String improvementComment) {
        this.improvementComment = improvementComment;
    }

    public String getOtherComment() {
        return otherComment;
    }

    public void setOtherComment(String otherComment) {
        this.otherComment = otherComment;
    }

    public Timestamp getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(Timestamp submittedAt) {
        this.submittedAt = submittedAt;
    }
}

