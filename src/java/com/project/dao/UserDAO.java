/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.project.dao;

import com.project.model.User;
import com.project.model.Staff;
import com.project.model.Student;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ZULMAJDI
 */
public class UserDAO extends BaseDAO {

    public static String hashPassword(String plainTextPassword) {
        return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt(12));
    }

    public static boolean checkPassword(String plainPassword, String hashed) {
        if (hashed == null) {
            return false;
        }
        return BCrypt.checkpw(plainPassword, hashed);
    }

    public User getUserByEmail(String email) throws Exception {
        String sql = "SELECT * FROM users WHERE email = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String role = rs.getString("role");
                    User user = mapUser(rs);
                    if ("Student".equalsIgnoreCase(role)) {
                        return loadStudentSubtype(user);
                    } else if ("Lecturer".equalsIgnoreCase(role) || "Admin".equalsIgnoreCase(role)) {
                        return loadStaffSubtype(user);
                    } else {
                        return user;
                    }
                }
            }
        }
        return null;
    }

    public User getUserById(int userId) throws Exception {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String role = rs.getString("role");
                    User user = mapUser(rs);  // declare once
                    if ("Student".equalsIgnoreCase(role)) {
                        return loadStudentSubtype(user);
                    } else if ("Lecturer".equalsIgnoreCase(role) || "Admin".equalsIgnoreCase(role)) {
                        return loadStaffSubtype(user);
                    } else {
                        return user;
                    }
                }
            }
        }
        return null;
    }

    private User mapUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setName(rs.getString("name"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password"));
        user.setRole(rs.getString("role"));
        return user;
    }

    private Student loadStudentSubtype(User user) throws Exception {
        String sql = "SELECT student_number, major FROM students WHERE user_id = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, user.getUserId());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Student student = new Student();
                    // Copy common fields
                    student.setUserId(user.getUserId());
                    student.setName(user.getName());
                    student.setEmail(user.getEmail());
                    student.setPassword(user.getPassword());
                    student.setRole(user.getRole());
                    // Set subtype fields
                    student.setStudentNumber(rs.getString("student_number"));
                    student.setMajor(rs.getString("major"));
                    return student;
                }
            }
        }
        return null;
    }

    private Staff loadStaffSubtype(User user) throws Exception {
        String sql = "SELECT staff_number, department FROM staff WHERE user_id = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, user.getUserId());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Staff staff = new Staff();
                    // Copy common fields
                    staff.setUserId(user.getUserId());
                    staff.setName(user.getName());
                    staff.setEmail(user.getEmail());
                    staff.setPassword(user.getPassword());
                    staff.setRole(user.getRole());
                    // Set subtype fields
                    staff.setStaffNumber(rs.getString("staff_number"));
                    staff.setDepartment(rs.getString("department"));
                    return staff;
                }
            }
        }
        return null;
    }

    public void addUser(User user) throws Exception {
        String sql = "INSERT INTO users (name, email, password, role) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            String hashedPassword = hashPassword(user.getPassword());
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, hashedPassword);
            ps.setString(4, user.getRole());
            ps.executeUpdate();

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int userId = generatedKeys.getInt(1);
                    user.setUserId(userId);
                    insertSubtype(user);
                }
            }
        }
    }

    private void insertSubtype(User user) throws Exception {
        String role = user.getRole();
        try (Connection conn = getConnection()) {
            if ("Student".equalsIgnoreCase(role)) {
                Student student = (Student) user;  // cast to Student
                String sqlStudent = "INSERT INTO students (user_id, student_number, major) VALUES (?, ?, ?)";
                try (PreparedStatement ps = conn.prepareStatement(sqlStudent)) {
                    ps.setInt(1, student.getUserId());
                    ps.setString(2, student.getStudentNumber());
                    ps.setString(3, student.getMajor());
                    ps.executeUpdate();
                }
            } else if ("Lecturer".equalsIgnoreCase(role) || "Admin".equalsIgnoreCase(role)) {
                Staff staff = (Staff) user;  // cast to Staff
                String sqlStaff = "INSERT INTO staff (user_id, staff_number, department) VALUES (?, ?, ?)";
                try (PreparedStatement ps = conn.prepareStatement(sqlStaff)) {
                    ps.setInt(1, staff.getUserId());
                    ps.setString(2, staff.getStaffNumber());
                    ps.setString(3, staff.getDepartment());
                    ps.executeUpdate();
                }
            }
        }
    }

    public void updateUser(User user) throws Exception {
        String sql = "UPDATE users SET name=?, email=?, password=?, role=? WHERE user_id=?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            String password = user.getPassword();
            if (password == null || !(password.startsWith("$2a$") || password.startsWith("$2b$") || password.startsWith("$2y$"))) {
                password = hashPassword(password);
            }
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, password);
            ps.setString(4, user.getRole());
            ps.setInt(5, user.getUserId());
            ps.executeUpdate();
        }
        if ("Student".equalsIgnoreCase(user.getRole())) {
            updateStudent(user);
        } else if ("Lecturer".equalsIgnoreCase(user.getRole()) || "Admin".equalsIgnoreCase(user.getRole())) {
            updateStaff(user);
        }
    }

    public void updateStudent(User user) throws Exception {
        if (!(user instanceof Student)) {
            throw new IllegalArgumentException("User is not a Student");
        }
        Student student = (Student) user;  // cast to Student
        String sql = "UPDATE students SET student_number=?, major=? WHERE user_id=?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, student.getStudentNumber());
            ps.setString(2, student.getMajor());
            ps.setInt(3, student.getUserId());
            ps.executeUpdate();
        }
    }

    public void updateStaff(User user) throws Exception {
        if (!(user instanceof Staff)) {
            throw new IllegalArgumentException("User is not a Staff");
        }
        Staff staff = (Staff) user;  // cast to Staff
        String sql = "UPDATE staff SET staff_number=?, department=? WHERE user_id=?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, staff.getStaffNumber());
            ps.setString(2, staff.getDepartment());
            ps.setInt(3, staff.getUserId());
            ps.executeUpdate();
        }
    }

    public void deleteUser(int userId) throws Exception {
        String sql = "DELETE FROM users WHERE user_id=?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        }
    }

    public List<User> getAllUsers() throws Exception {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                User baseUser = mapUser(rs);
                String role = baseUser.getRole();
                User user;
                if ("Student".equalsIgnoreCase(role)) {
                    user = loadStudentSubtype(baseUser);
                } else if ("Lecturer".equalsIgnoreCase(role) || "Admin".equalsIgnoreCase(role)) {
                    user = loadStaffSubtype(baseUser);
                } else {
                    user = baseUser;
                }
                list.add(user);
            }
        }
        return list;
    }

    public int getTotalUsers() throws Exception {
        String sql = "SELECT COUNT(*) FROM users";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    public List<Student> getStudentsBySubject(int subjectId) throws Exception {
        List<Student> students = new ArrayList<>();

        String sql = "SELECT u.user_id, u.name, u.email, u.password, u.role, s.student_number, s.major "
                + "FROM users u "
                + "JOIN students s ON u.user_id = s.user_id "
                + "JOIN student_subjects ss ON ss.student_id = u.user_id "
                + "WHERE ss.subject_id = ?";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, subjectId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    User baseUser = mapUser(rs);

                    Student student = new Student();
                    student.setUserId(baseUser.getUserId());
                    student.setName(baseUser.getName());
                    student.setEmail(baseUser.getEmail());
                    student.setPassword(baseUser.getPassword());
                    student.setRole(baseUser.getRole());
                    student.setStudentNumber(rs.getString("student_number"));
                    student.setMajor(rs.getString("major"));

                    students.add(student);
                }
            }
        }

        return students;
    }
}
