/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.project.model;

/**
 *
 * @author nabil
 */
public class LecturerClass {

    private int lecturerId;
    private int classId;

    public LecturerClass() {
    }

    public LecturerClass(int lecturerId, int classId) {
        this.lecturerId = lecturerId;
        this.classId = classId;
    }

    public int getLecturerId() {
        return lecturerId;
    }

    public void setLecturerId(int lecturerId) {
        this.lecturerId = lecturerId;
    }

    public int getClassId() {
        return classId;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }
}
