package com.project.servlet;

import com.project.dao.ClassesDAO;
import com.project.model.Class;
import com.project.model.User;
import com.project.util.CsrfUtil;
import com.project.util.Roles;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/ClassServlet")
public class ClassServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(ClassServlet.class.getName());

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
            response.sendRedirect(request.getContextPath() + "/admin/ManageClasses.jsp");
            return;
        }

        String action = request.getParameter("action");
        ClassesDAO classesDAO = new ClassesDAO();

        try {
            if ("add".equals(action)) {
                String className = request.getParameter("className");
                int subjectId = Integer.parseInt(request.getParameter("subjectId"));
                if (className == null || className.trim().isEmpty()) {
                    session.setAttribute("errorMessage", "Class name cannot be empty.");
                } else {
                    Class c = new Class();
                    c.setClassName(className.trim());
                    c.setSubjectId(subjectId);
                    classesDAO.addClass(c);
                    session.setAttribute("successMessage", "Class added successfully.");
                }
            } else if ("update".equals(action)) {
                int classId = Integer.parseInt(request.getParameter("classId"));
                String className = request.getParameter("className");
                int subjectId = Integer.parseInt(request.getParameter("subjectId"));
                if (className == null || className.trim().isEmpty()) {
                    session.setAttribute("errorMessage", "Class name cannot be empty.");
                } else {
                    Class c = new Class();
                    c.setClassId(classId);
                    c.setClassName(className.trim());
                    c.setSubjectId(subjectId);
                    classesDAO.updateClass(c);
                    session.setAttribute("successMessage", "Class updated successfully.");
                }
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "ClassServlet POST error, action=" + action, e);
            session.setAttribute("errorMessage", "Error: " + e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/admin/ManageClasses.jsp");
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User caller = (session != null) ? (User) session.getAttribute("user") : null;
        if (caller == null || !Roles.ADMIN.equalsIgnoreCase(caller.getRole())) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String action = request.getParameter("action");
        ClassesDAO classesDAO = new ClassesDAO();

        try {
            if ("delete".equals(action)) {
                int classId = Integer.parseInt(request.getParameter("id"));
                classesDAO.deleteClass(classId);
                session.setAttribute("successMessage", "Class deleted successfully.");
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "ClassServlet GET error, action=" + action, e);
            session.setAttribute("errorMessage", "Error: " + e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/admin/ManageClasses.jsp");
    }
}
