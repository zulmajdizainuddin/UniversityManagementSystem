/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.project.model;

/**
 *
 * @author nabil
 */
public class LecturerSubject {

    private int lecturerId;
    private int subjectId;

    public LecturerSubject() {
    }

    public LecturerSubject(int lecturerId, int subjectId) {
        this.lecturerId = lecturerId;
        this.subjectId = subjectId;
    }

    public int getLecturerId() {
        return lecturerId;
    }

    public void setLecturerId(int lecturerId) {
        this.lecturerId = lecturerId;
    }

    public int getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
    }
}
