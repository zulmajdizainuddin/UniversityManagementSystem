/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.project.dao;

import com.project.model.LecturerSubject;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author nabil
 */

public class LecturerSubjectDAO extends BaseDAO {

    public List<LecturerSubject> getAll() throws Exception {
        List<LecturerSubject> list = new ArrayList<>();
        String sql = "SELECT * FROM lecturer_subjects";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                LecturerSubject ls = new LecturerSubject();
                ls.setLecturerId(rs.getInt("lecturer_id"));
                ls.setSubjectId(rs.getInt("subject_id"));
                list.add(ls);
            }
        }
        return list;
    }

    public List<Integer> getSubjectIdsByLecturer(int lecturerId) throws Exception {
        List<Integer> subjectIds = new ArrayList<>();
        String sql = "SELECT subject_id FROM lecturer_subjects WHERE lecturer_id = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, lecturerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    subjectIds.add(rs.getInt("subject_id"));
                }
            }
        }
        return subjectIds;
    }

    public void add(LecturerSubject ls) throws Exception {
        String sql = "INSERT INTO lecturer_subjects (lecturer_id, subject_id) VALUES (?, ?)";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ls.getLecturerId());
            ps.setInt(2, ls.getSubjectId());
            ps.executeUpdate();
        }
    }

    public void deleteByLecturer(int lecturerId) throws Exception {
        String sql = "DELETE FROM lecturer_subjects WHERE lecturer_id = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, lecturerId);
            ps.executeUpdate();
        }
    }

    public boolean lecturerTeachesSubject(int lecturerId, int subjectId) throws Exception {
        String sql = "SELECT 1 FROM lecturer_subjects WHERE lecturer_id = ? AND subject_id = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, lecturerId);
            ps.setInt(2, subjectId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }
}
