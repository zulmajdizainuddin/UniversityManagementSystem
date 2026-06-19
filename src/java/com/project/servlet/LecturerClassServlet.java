package com.project.servlet;

import com.project.dao.ClassesDAO;
import com.project.dao.LecturerClassDAO;
import com.project.dao.StaffDAO;
import com.project.model.Class;
import com.project.model.Staff;
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

@WebServlet("/LecturerClassServlet")
public class LecturerClassServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(LecturerClassServlet.class.getName());

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User caller = (session != null) ? (User) session.getAttribute("user") : null;
        if (caller == null || !Roles.ADMIN.equalsIgnoreCase(caller.getRole())) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        StaffDAO staffDAO = new StaffDAO();
        ClassesDAO classesDAO = new ClassesDAO();
        LecturerClassDAO lcDAO = new LecturerClassDAO();

        try {
            List<Staff> lecturers = staffDAO.getAllLecturers();
            List<Class> classes = classesDAO.getAllClasses();

            request.setAttribute("lecturers", lecturers);
            request.setAttribute("classes", classes);

            String lecturerIdStr = request.getParameter("lecturerId");
            if (lecturerIdStr != null) {
                int lecturerId = Integer.parseInt(lecturerIdStr);
                List<Integer> assignedClassIds = lcDAO.getClassIdsByLecturer(lecturerId);
                request.setAttribute("selectedLecturerId", lecturerId);
                request.setAttribute("assignedClassIds", assignedClassIds);
            }

            request.getRequestDispatcher("/admin/ManageLecturerClasses.jsp").forward(request, response);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "LecturerClassServlet GET error", e);
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
            response.sendRedirect(request.getContextPath() + "/admin/ManageLecturerClasses.jsp");
            return;
        }

        LecturerClassDAO lcDAO = new LecturerClassDAO();

        try {
            int lecturerId = Integer.parseInt(request.getParameter("lecturerId"));
            String[] classIds = request.getParameterValues("classIds");

            lcDAO.deleteByLecturer(lecturerId);

            if (classIds != null) {
                for (String classIdStr : classIds) {
                    int classId = Integer.parseInt(classIdStr);
                    lcDAO.add(new com.project.model.LecturerClass(lecturerId, classId));
                }
            }

            session.setAttribute("successMessage", "Lecturer-class assignments updated successfully.");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "LecturerClassServlet POST error", e);
            session.setAttribute("errorMessage", "Error updating assignments: " + e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/admin/ManageLecturerClasses.jsp");
    }
}
