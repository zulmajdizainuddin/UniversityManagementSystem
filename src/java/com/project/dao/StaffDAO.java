/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.project.dao;

import com.project.model.Staff;
import java.sql.*;
import java.util.ArrayList;

import java.util.List;

/**
 *
 * @author nabil
 */
public class StaffDAO extends BaseDAO {

    public List<Staff> getAllLecturers() throws Exception {
        List<Staff> list = new ArrayList<>();
        String sql = "SELECT s.user_id, u.name, s.staff_number, s.department "
                + "FROM staff s JOIN users u ON s.user_id = u.user_id "
                + "WHERE u.role = 'Lecturer' ORDER BY u.name";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Staff staff = new Staff();
                staff.setUserId(rs.getInt("user_id"));
                staff.setName(rs.getString("name"));
                staff.setStaffNumber(rs.getString("staff_number"));
                staff.setDepartment(rs.getString("department"));
                list.add(staff);
            }
        }
        return list;
    }
}
