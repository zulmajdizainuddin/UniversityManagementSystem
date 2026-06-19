/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.project.dao;

import com.project.model.Attendance;
import com.project.model.AttendanceSummary;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ZULMAJDI
 */
public class AttendanceDAO extends BaseDAO {

    public void addOrUpdateAttendance(Attendance attendance) throws Exception {
        try (Connection conn = getConnection()) {
            String checkSql = "SELECT attendance_id FROM attendance WHERE student_id = ? AND class_id = ? AND date = ?";
            try (PreparedStatement checkPs = conn.prepareStatement(checkSql)) {
                checkPs.setInt(1, attendance.getStudentId());
                checkPs.setInt(2, attendance.getClassId());
                checkPs.setString(3, attendance.getDate());
                try (ResultSet rs = checkPs.executeQuery()) {
                    if (rs.next()) {
                        int attendanceId = rs.getInt("attendance_id");
                        String updateSql = "UPDATE attendance SET status = ?, lecturer_id = ?, subject_id = ? WHERE attendance_id = ?";
                        try (PreparedStatement updatePs = conn.prepareStatement(updateSql)) {
                            updatePs.setString(1, attendance.getStatus());
                            updatePs.setInt(2, attendance.getLecturerId());
                            updatePs.setInt(3, attendance.getSubjectId());
                            updatePs.setInt(4, attendanceId);
                            updatePs.executeUpdate();
                        }
                    } else {
                        String insertSql = "INSERT INTO attendance (student_id, class_id, date, status, lecturer_id, subject_id) VALUES (?, ?, ?, ?, ?, ?)";
                        try (PreparedStatement insertPs = conn.prepareStatement(insertSql)) {
                            insertPs.setInt(1, attendance.getStudentId());
                            insertPs.setInt(2, attendance.getClassId());
                            insertPs.setString(3, attendance.getDate());
                            insertPs.setString(4, attendance.getStatus());
                            insertPs.setInt(5, attendance.getLecturerId());
                            insertPs.setInt(6, attendance.getSubjectId());
                            insertPs.executeUpdate();
                        }
                    }
                }
            }
        }
    }

    public List<Attendance> getAttendanceByLecturer(int lecturerId) throws Exception {
        List<Attendance> list = new ArrayList<>();
        String sql = "SELECT * FROM attendance WHERE lecturer_id = ? ORDER BY date DESC, class_id, student_id";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, lecturerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Attendance att = new Attendance();
                    att.setAttendanceId(rs.getInt("attendance_id"));
                    att.setStudentId(rs.getInt("student_id"));
                    att.setClassId(rs.getInt("class_id"));
                    att.setDate(rs.getString("date"));
                    att.setStatus(rs.getString("status"));
                    att.setLecturerId(rs.getInt("lecturer_id"));
                    att.setSubjectId(rs.getInt("subject_id"));
                    list.add(att);
                }
            }
        }
        return list;
    }

    public Attendance getAttendanceById(int attendanceId) throws Exception {
        String sql = "SELECT * FROM attendance WHERE attendance_id = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, attendanceId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Attendance att = new Attendance();
                    att.setAttendanceId(rs.getInt("attendance_id"));
                    att.setStudentId(rs.getInt("student_id"));
                    att.setClassId(rs.getInt("class_id"));
                    att.setDate(rs.getString("date"));
                    att.setStatus(rs.getString("status"));
                    att.setLecturerId(rs.getInt("lecturer_id"));
                    att.setSubjectId(rs.getInt("subject_id"));
                    return att;
                }
            }
        }
        return null;
    }

    public void updateAttendance(Attendance attendance) throws Exception {
        String sql = "UPDATE attendance SET status = ? WHERE attendance_id = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, attendance.getStatus());
            ps.setInt(2, attendance.getAttendanceId());
            ps.executeUpdate();
        }
    }

    public List<Attendance> getAttendanceByClassDateLecturer(Integer classId, String date, int lecturerId) throws Exception {
        List<Attendance> list = new ArrayList<>();
        String sql = "SELECT * FROM attendance WHERE class_id = ? AND date = ? AND lecturer_id = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, classId);
            ps.setString(2, date);
            ps.setInt(3, lecturerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Attendance att = new Attendance();
                    att.setAttendanceId(rs.getInt("attendance_id"));
                    att.setStudentId(rs.getInt("student_id"));
                    att.setClassId(rs.getInt("class_id"));
                    att.setDate(rs.getString("date"));
                    att.setStatus(rs.getString("status"));
                    att.setLecturerId(rs.getInt("lecturer_id"));
                    att.setSubjectId(rs.getInt("subject_id"));
                    list.add(att);
                }
            }
        }
        return list;
    }

    public List<Attendance> getAttendanceByClass(int classId) throws Exception {
        List<Attendance> list = new ArrayList<>();
        String sql = "SELECT * FROM attendance WHERE class_id = ? ORDER BY date DESC, student_id";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, classId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Attendance att = new Attendance();
                    att.setAttendanceId(rs.getInt("attendance_id"));
                    att.setStudentId(rs.getInt("student_id"));
                    att.setClassId(rs.getInt("class_id"));
                    att.setDate(rs.getString("date"));
                    att.setStatus(rs.getString("status"));
                    att.setLecturerId(rs.getInt("lecturer_id"));
                    att.setSubjectId(rs.getInt("subject_id"));
                    list.add(att);
                }
            }
        }
        return list;
    }

    public List<Attendance> getAttendanceByStudentAndSubject(int studentId, int subjectId) throws Exception {
        List<Attendance> attendanceList = new ArrayList<>();
        String sql = "SELECT a.attendance_id, a.student_id, a.class_id, a.subject_id, a.date, a.status, a.lecturer_id, "
                + "s.subject_name, c.class_name "
                + "FROM attendance a "
                + "JOIN subjects s ON a.subject_id = s.subject_id "
                + "JOIN classes c ON a.class_id = c.class_id "
                + "WHERE a.student_id = ? AND a.subject_id = ? "
                + "ORDER BY a.date DESC";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, subjectId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Attendance attendance = new Attendance();
                    attendance.setAttendanceId(rs.getInt("attendance_id"));
                    attendance.setStudentId(rs.getInt("student_id"));
                    attendance.setClassId(rs.getInt("class_id"));
                    attendance.setSubjectId(rs.getInt("subject_id"));
                    attendance.setDate(rs.getDate("date").toString());
                    attendance.setStatus(rs.getString("status"));
                    attendance.setLecturerId(rs.getInt("lecturer_id"));
                    attendance.setSubjectName(rs.getString("subject_name"));
                    attendance.setClassName(rs.getString("class_name"));
                    attendanceList.add(attendance);
                }
            }
        }
        return attendanceList;
    }

    public List<AttendanceSummary> getAttendanceSummaryByStudent(int studentId) throws Exception {
        List<AttendanceSummary> list = new ArrayList<>();
        String sql = "SELECT s.subject_id, s.subject_code, s.subject_name, "
                + "COUNT(CASE WHEN a.status = 'Present' THEN 1 END) AS present_count, "
                + "COUNT(CASE WHEN a.status = 'Absent' THEN 1 END) AS absent_count "
                + "FROM attendance a "
                + "JOIN subjects s ON a.subject_id = s.subject_id "
                + "WHERE a.student_id = ? "
                + "GROUP BY s.subject_id, s.subject_code, s.subject_name "
                + "ORDER BY s.subject_name";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    AttendanceSummary summary = new AttendanceSummary();
                    summary.setSubjectId(rs.getInt("subject_id"));
                    summary.setSubjectCode(rs.getString("subject_code"));
                    summary.setSubjectName(rs.getString("subject_name"));
                    summary.setPresentCount(rs.getInt("present_count"));
                    summary.setAbsentCount(rs.getInt("absent_count"));
                    list.add(summary);
                }
            }
        }
        return list;
    }
}
