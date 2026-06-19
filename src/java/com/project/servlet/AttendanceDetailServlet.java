package com.project.servlet;

import com.project.dao.AttendanceDAO;
import com.project.model.Attendance;
import com.project.model.User;
import com.project.util.Roles;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/student/AttendanceDetailServlet")
public class AttendanceDetailServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(AttendanceDetailServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;
        if (user == null || !Roles.STUDENT.equalsIgnoreCase(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        int subjectId;
        try {
            subjectId = Integer.parseInt(request.getParameter("subjectId"));
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/AttendanceStudentServlet");
            return;
        }

        AttendanceDAO attendanceDAO = new AttendanceDAO();

        try {
            List<Attendance> attendanceList = attendanceDAO.getAttendanceByStudentAndSubject(user.getUserId(), subjectId);
            request.setAttribute("attendanceList", attendanceList);
            request.getRequestDispatcher("/student/AttendanceDetail.jsp").forward(request, response);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "AttendanceDetailServlet GET error", e);
            response.sendRedirect(request.getContextPath() + "/error.jsp");
        }
    }
}
