package com.project.servlet;

import com.project.dao.GradeDAO;
import com.project.model.Grade;
import com.project.model.User;
import com.project.util.Roles;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/GradeStudentServlet")
public class GradeStudentServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(GradeStudentServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;
        if (user == null || !Roles.STUDENT.equalsIgnoreCase(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        GradeDAO gradeDAO = new GradeDAO();

        try {
            List<Grade> grades = gradeDAO.getGradesByStudent(user.getUserId());
            request.setAttribute("grades", grades);
            request.getRequestDispatcher("/student/StudentGrades.jsp").forward(request, response);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "GradeStudentServlet GET error", e);
            request.setAttribute("errorMessage", "Error loading grades: " + e.getMessage());
            request.getRequestDispatcher("/student/StudentGrades.jsp").forward(request, response);
        }
    }
}
