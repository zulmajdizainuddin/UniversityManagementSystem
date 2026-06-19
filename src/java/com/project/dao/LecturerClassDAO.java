/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.project.dao;

import com.project.model.Class;
import com.project.model.LecturerClass;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author nabil
 */
public class LecturerClassDAO extends BaseDAO {

    public List<LecturerClass> getAll() throws Exception {
        List<LecturerClass> list = new ArrayList<>();
        String sql = "SELECT * FROM lecturer_classes";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                LecturerClass lc = new LecturerClass();
                lc.setLecturerId(rs.getInt("lecturer_id"));
                lc.setClassId(rs.getInt("class_id"));
                list.add(lc);
            }
        }
        return list;
    }

    public List<Integer> getClassIdsByLecturer(int lecturerId) throws Exception {
        List<Integer> classIds = new ArrayList<>();
        String sql = "SELECT class_id FROM lecturer_classes WHERE lecturer_id = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, lecturerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    classIds.add(rs.getInt("class_id"));
                }
            }
        }
        return classIds;
    }

    public void add(LecturerClass lc) throws Exception {
        String sql = "INSERT INTO lecturer_classes (lecturer_id, class_id) VALUES (?, ?)";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, lc.getLecturerId());
            ps.setInt(2, lc.getClassId());
            ps.executeUpdate();
        }
    }

    public void deleteByLecturer(int lecturerId) throws Exception {
        String sql = "DELETE FROM lecturer_classes WHERE lecturer_id = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, lecturerId);
            ps.executeUpdate();
        }
    }

    /**
     * Get list of Class objects assigned to a lecturer.
     *
     * @param lecturerId the lecturer's user ID
     * @return List of Class objects
     * @throws Exception on DB errors
     */
    public boolean lecturerTeachesClass(int lecturerId, int classId) throws Exception {
        String sql = "SELECT 1 FROM lecturer_classes WHERE lecturer_id = ? AND class_id = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, lecturerId);
            ps.setInt(2, classId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public List<Class> getClassesByLecturer(int lecturerId) throws Exception {
        List<Class> classes = new ArrayList<>();
        String sql = "SELECT c.class_id, c.class_name, c.subject_id "
                + "FROM classes c "
                + "JOIN lecturer_classes lc ON c.class_id = lc.class_id "
                + "WHERE lc.lecturer_id = ?";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, lecturerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Class c = new Class();
                    c.setClassId(rs.getInt("class_id"));
                    c.setClassName(rs.getString("class_name"));
                    c.setSubjectId(rs.getInt("subject_id"));
                    classes.add(c);
                }
            }
        }
        return classes;
    }
}
