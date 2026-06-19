package com.project.servlet;

import com.project.dao.AttendanceDAO;
import com.project.model.AttendanceSummary;
import com.project.model.User;
import com.project.util.Roles;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/AttendanceStudentServlet")
public class AttendanceStudentServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(AttendanceStudentServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;
        if (user == null || !Roles.STUDENT.equalsIgnoreCase(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        AttendanceDAO attendanceDAO = new AttendanceDAO();

        try {
            List<AttendanceSummary> attendanceSummaries = attendanceDAO.getAttendanceSummaryByStudent(user.getUserId());
            request.setAttribute("attendanceSummaries", attendanceSummaries);
            request.getRequestDispatcher("/student/StudentAttendance.jsp").forward(request, response);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "AttendanceStudentServlet GET error", e);
            request.setAttribute("errorMessage", "Error loading attendance records: " + e.getMessage());
            request.getRequestDispatcher("/student/StudentAttendance.jsp").forward(request, response);
        }
    }
}
