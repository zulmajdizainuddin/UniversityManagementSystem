package com.project.servlet;

import com.project.dao.LecturerSubjectDAO;
import com.project.dao.StaffDAO;
import com.project.dao.SubjectDAO;
import com.project.model.Staff;
import com.project.model.Subject;
import com.project.model.User;
import com.project.util.CsrfUtil;
import com.project.util.Roles;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/LecturerSubjectServlet")
public class LecturerSubjectServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(LecturerSubjectServlet.class.getName());

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User caller = (session != null) ? (User) session.getAttribute("user") : null;
        if (caller == null || !Roles.ADMIN.equalsIgnoreCase(caller.getRole())) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        StaffDAO staffDAO = new StaffDAO();
        SubjectDAO subjectDAO = new SubjectDAO();
        LecturerSubjectDAO lsDAO = new LecturerSubjectDAO();

        try {
            List<Staff> lecturers = staffDAO.getAllLecturers();
            List<Subject> subjects = subjectDAO.getAllSubjects();

            request.setAttribute("lecturers", lecturers);
            request.setAttribute("subjects", subjects);

            String lecturerIdStr = request.getParameter("lecturerId");
            if (lecturerIdStr != null) {
                int lecturerId = Integer.parseInt(lecturerIdStr);
                List<Integer> assignedSubjectIds = lsDAO.getSubjectIdsByLecturer(lecturerId);
                request.setAttribute("selectedLecturerId", lecturerId);
                request.setAttribute("assignedSubjectIds", assignedSubjectIds);
            }

            request.getRequestDispatcher("/admin/ManageLecturerSubjects.jsp").forward(request, response);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "LecturerSubjectServlet GET error", e);
            response.sendError(500, "Internal Server Error");
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User caller = (session != null) ? (User) session.getAttribute("user") : null;
        if (caller == null || !Roles.ADMIN.equalsIgnoreCase(caller.getRole())) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        if (!CsrfUtil.isValidToken(request)) {
            session.setAttribute("errorMessage", "Invalid request. Please try again.");
            response.sendRedirect(request.getContextPath() + "/admin/ManageLecturerSubjects.jsp");
            return;
        }

        LecturerSubjectDAO lsDAO = new LecturerSubjectDAO();

        try {
            int lecturerId = Integer.parseInt(request.getParameter("lecturerId"));
            String[] subjectIds = request.getParameterValues("subjectIds");

            lsDAO.deleteByLecturer(lecturerId);

            if (subjectIds != null) {
                for (String subjectIdStr : subjectIds) {
                    int subjectId = Integer.parseInt(subjectIdStr);
                    lsDAO.add(new com.project.model.LecturerSubject(lecturerId, subjectId));
                }
            }

            session.setAttribute("successMessage", "Lecturer-subject assignments updated successfully.");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "LecturerSubjectServlet POST error", e);
            session.setAttribute("errorMessage", "Error updating assignments: " + e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/admin/ManageLecturerSubjects.jsp");
    }
}
