/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.project.model;

import java.sql.Timestamp;

/**
 *
 * @author ZULMAJDI
 */
public class Course {

    private int courseId;
    private String courseName;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public Course() {
    }

    public Course(int courseId, String courseName, Timestamp createdAt, Timestamp updatedAt) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
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
