package com.project.servlet;

import com.project.dao.StudentDAO;
import com.project.model.User;
import com.project.util.CsrfUtil;
import com.project.util.Roles;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/admin/AssignStudentServlet")
public class AssignStudentServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(AssignStudentServlet.class.getName());

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User caller = (session != null) ? (User) session.getAttribute("user") : null;
        if (caller == null || !Roles.ADMIN.equalsIgnoreCase(caller.getRole())) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        if (!CsrfUtil.isValidToken(request)) {
            request.setAttribute("error", "Invalid request. Please try again.");
            forwardToJSP(request, response, 0);
            return;
        }

        String studentIdStr = request.getParameter("studentId");
        String subjectIdStr = request.getParameter("subjectId");
        String classIdStr = request.getParameter("classId");

        if (studentIdStr == null || subjectIdStr == null || classIdStr == null
                || studentIdStr.isEmpty() || subjectIdStr.isEmpty() || classIdStr.isEmpty()) {
            request.setAttribute("error", "All fields are required.");
            forwardToJSP(request, response, 0);
            return;
        }

        try {
            int studentId = Integer.parseInt(studentIdStr);
            int subjectId = Integer.parseInt(subjectIdStr);
            int classId = Integer.parseInt(classIdStr);

            StudentDAO studentDAO = new StudentDAO();
            studentDAO.assignStudentToSubject(studentId, subjectId);
            studentDAO.assignStudentToClass(studentId, classId);

            request.setAttribute("message", "Student assigned successfully.");
            forwardToJSP(request, response, subjectId);

        } catch (Exception e) {
            logger.log(Level.SEVERE, "AssignStudentServlet POST error", e);
            request.setAttribute("error", "Error assigning student: " + e.getMessage());
            forwardToJSP(request, response, 0);
        }
    }

    private void forwardToJSP(HttpServletRequest request, HttpServletResponse response, int selectedSubjectId)
            throws ServletException, IOException {
        request.setAttribute("selectedSubjectId", selectedSubjectId);
        request.getRequestDispatcher("AssignStudent.jsp").forward(request, response);
    }
}
