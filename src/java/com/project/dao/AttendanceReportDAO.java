/*
 * Attendance reporting DAO for admin analysis
 */
package com.project.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class AttendanceReportDAO extends BaseDAO {

    public static class SubjectReportRow {
        public int studentId;
        public String studentNumber;
        public String studentName;
        public int presentCount;
        public int absentCount;
        public double attendancePercent;
    }

    public static class SubjectReportSummary {
        public int subjectId;
        public String subjectCode;
        public String subjectName;
        public int totalSessions;
        public int totalPresent;
        public int totalAbsent;
        public double overallPercent;
        public List<SubjectReportRow> studentRows = new ArrayList<>();
    }

    public static class ClassReportRow {
        public int studentId;
        public String studentNumber;
        public String studentName;
        public int presentCount;
        public int absentCount;
        public double attendancePercent;
    }

    public static class ClassReportSummary {
        public int classId;
        public String className;
        public String subjectName;
        public int totalSessions;
        public int totalPresent;
        public int totalAbsent;
        public double overallPercent;
        public List<ClassReportRow> studentRows = new ArrayList<>();
    }

    public static class StudentSubjectRow {
        public int subjectId;
        public String subjectCode;
        public String subjectName;
        public int presentCount;
        public int absentCount;
        public double attendancePercent;
    }

    public static class StudentReportSummary {
        public int studentId;
        public String studentNumber;
        public String studentName;
        public int totalPresentAll;
        public int totalAbsentAll;
        public double overallPercent;
        public List<StudentSubjectRow> subjectRows = new ArrayList<>();
    }

    /**
     * Subject-level report for a given subject.
     */
    public SubjectReportSummary getSubjectReport(int subjectId) throws Exception {
        SubjectReportSummary summary = new SubjectReportSummary();

        try (Connection conn = getConnection()) {
            // Summary: total sessions (distinct date), total present, total absent
            String summarySql =
                    "SELECT s.subject_id, s.subject_code, s.subject_name, " +
                    "       COUNT(DISTINCT a.date) AS total_sessions, " +
                    "       COUNT(CASE WHEN a.status = 'Present' THEN 1 END) AS total_present, " +
                    "       COUNT(CASE WHEN a.status = 'Absent' THEN 1 END)  AS total_absent " +
                    "FROM attendance a " +
                    "JOIN subjects s ON a.subject_id = s.subject_id " +
                    "WHERE a.subject_id = ? " +
                    "GROUP BY s.subject_id, s.subject_code, s.subject_name";

            try (PreparedStatement ps = conn.prepareStatement(summarySql)) {
                ps.setInt(1, subjectId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        summary.subjectId = rs.getInt("subject_id");
                        summary.subjectCode = rs.getString("subject_code");
                        summary.subjectName = rs.getString("subject_name");
                        summary.totalSessions = rs.getInt("total_sessions");
                        summary.totalPresent = rs.getInt("total_present");
                        summary.totalAbsent = rs.getInt("total_absent");
                        int denom = summary.totalPresent + summary.totalAbsent;
                        summary.overallPercent = denom > 0 ? (summary.totalPresent * 100.0 / denom) : 0.0;
                    }
                }
            }

            // Per-student breakdown for this subject
            String perStudentSql =
                    "SELECT u.user_id AS student_id, s2.student_number, u.name, " +
                    "       COUNT(CASE WHEN a.status = 'Present' THEN 1 END) AS present_count, " +
                    "       COUNT(CASE WHEN a.status = 'Absent' THEN 1 END)  AS absent_count " +
                    "FROM attendance a " +
                    "JOIN users u ON a.student_id = u.user_id " +
                    "JOIN students s2 ON u.user_id = s2.user_id " +
                    "WHERE a.subject_id = ? " +
                    "GROUP BY u.user_id, s2.student_number, u.name " +
                    "ORDER BY u.name";

            try (PreparedStatement ps = conn.prepareStatement(perStudentSql)) {
                ps.setInt(1, subjectId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        SubjectReportRow row = new SubjectReportRow();
                        row.studentId = rs.getInt("student_id");
                        row.studentNumber = rs.getString("student_number");
                        row.studentName = rs.getString("name");
                        row.presentCount = rs.getInt("present_count");
                        row.absentCount = rs.getInt("absent_count");
                        int denom = row.presentCount + row.absentCount;
                        row.attendancePercent = denom > 0 ? (row.presentCount * 100.0 / denom) : 0.0;
                        summary.studentRows.add(row);
                    }
                }
            }
        }

        return summary;
    }

    /**
     * Class-level report for a given class (aggregated over all dates).
     */
    public ClassReportSummary getClassReport(int classId) throws Exception {
        ClassReportSummary summary = new ClassReportSummary();

        try (Connection conn = getConnection()) {
            // Summary for the class
            String summarySql =
                    "SELECT c.class_id, c.class_name, s.subject_name, " +
                    "       COUNT(DISTINCT a.date) AS total_sessions, " +
                    "       COUNT(CASE WHEN a.status = 'Present' THEN 1 END) AS total_present, " +
                    "       COUNT(CASE WHEN a.status = 'Absent' THEN 1 END)  AS total_absent " +
                    "FROM attendance a " +
                    "JOIN classes c ON a.class_id = c.class_id " +
                    "JOIN subjects s ON a.subject_id = s.subject_id " +
                    "WHERE a.class_id = ? " +
                    "GROUP BY c.class_id, c.class_name, s.subject_name";

            try (PreparedStatement ps = conn.prepareStatement(summarySql)) {
                ps.setInt(1, classId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        summary.classId = rs.getInt("class_id");
                        summary.className = rs.getString("class_name");
                        summary.subjectName = rs.getString("subject_name");
                        summary.totalSessions = rs.getInt("total_sessions");
                        summary.totalPresent = rs.getInt("total_present");
                        summary.totalAbsent = rs.getInt("total_absent");
                        int denom = summary.totalPresent + summary.totalAbsent;
                        summary.overallPercent = denom > 0 ? (summary.totalPresent * 100.0 / denom) : 0.0;
                    }
                }
            }

            // Per-student breakdown for this class
            String perStudentSql =
                    "SELECT u.user_id AS student_id, s2.student_number, u.name, " +
                    "       COUNT(CASE WHEN a.status = 'Present' THEN 1 END) AS present_count, " +
                    "       COUNT(CASE WHEN a.status = 'Absent' THEN 1 END)  AS absent_count " +
                    "FROM attendance a " +
                    "JOIN users u ON a.student_id = u.user_id " +
                    "JOIN students s2 ON u.user_id = s2.user_id " +
                    "WHERE a.class_id = ? " +
                    "GROUP BY u.user_id, s2.student_number, u.name " +
                    "ORDER BY u.name";

            try (PreparedStatement ps = conn.prepareStatement(perStudentSql)) {
                ps.setInt(1, classId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        ClassReportRow row = new ClassReportRow();
                        row.studentId = rs.getInt("student_id");
                        row.studentNumber = rs.getString("student_number");
                        row.studentName = rs.getString("name");
                        row.presentCount = rs.getInt("present_count");
                        row.absentCount = rs.getInt("absent_count");
                        int denom = row.presentCount + row.absentCount;
                        row.attendancePercent = denom > 0 ? (row.presentCount * 100.0 / denom) : 0.0;
                        summary.studentRows.add(row);
                    }
                }
            }
        }

        return summary;
    }

    /**
     * Student-level report across subjects.
     */
    public StudentReportSummary getStudentReport(int studentId) throws Exception {
        StudentReportSummary summary = new StudentReportSummary();
        summary.studentId = studentId;

        try (Connection conn = getConnection()) {
            // Overall info (name, student number, totals)
            String overallSql =
                    "SELECT u.name, s2.student_number, " +
                    "       COUNT(CASE WHEN a.status = 'Present' THEN 1 END) AS total_present, " +
                    "       COUNT(CASE WHEN a.status = 'Absent' THEN 1 END)  AS total_absent " +
                    "FROM users u " +
                    "JOIN students s2 ON u.user_id = s2.user_id " +
                    "LEFT JOIN attendance a ON a.student_id = u.user_id " +
                    "WHERE u.user_id = ? " +
                    "GROUP BY u.name, s2.student_number";

            try (PreparedStatement ps = conn.prepareStatement(overallSql)) {
                ps.setInt(1, studentId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        summary.studentName = rs.getString("name");
                        summary.studentNumber = rs.getString("student_number");
                        summary.totalPresentAll = rs.getInt("total_present");
                        summary.totalAbsentAll = rs.getInt("total_absent");
                        int denom = summary.totalPresentAll + summary.totalAbsentAll;
                        summary.overallPercent = denom > 0 ? (summary.totalPresentAll * 100.0 / denom) : 0.0;
                    }
                }
            }

            // Per-subject breakdown for this student
            String perSubjectSql =
                    "SELECT s.subject_id, s.subject_code, s.subject_name, " +
                    "       COUNT(CASE WHEN a.status = 'Present' THEN 1 END) AS present_count, " +
                    "       COUNT(CASE WHEN a.status = 'Absent' THEN 1 END)  AS absent_count " +
                    "FROM attendance a " +
                    "JOIN subjects s ON a.subject_id = s.subject_id " +
                    "WHERE a.student_id = ? " +
                    "GROUP BY s.subject_id, s.subject_code, s.subject_name " +
                    "ORDER BY s.subject_name";

            try (PreparedStatement ps = conn.prepareStatement(perSubjectSql)) {
                ps.setInt(1, studentId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        StudentSubjectRow row = new StudentSubjectRow();
                        row.subjectId = rs.getInt("subject_id");
                        row.subjectCode = rs.getString("subject_code");
                        row.subjectName = rs.getString("subject_name");
                        row.presentCount = rs.getInt("present_count");
                        row.absentCount = rs.getInt("absent_count");
                        int denom = row.presentCount + row.absentCount;
                        row.attendancePercent = denom > 0 ? (row.presentCount * 100.0 / denom) : 0.0;
                        summary.subjectRows.add(row);
                    }
                }
            }
        }

        return summary;
    }
}


