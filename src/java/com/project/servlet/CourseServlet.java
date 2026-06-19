package com.project.servlet;

import com.project.dao.CoursesDAO;
import com.project.model.Course;
import com.project.model.User;
import com.project.util.CsrfUtil;
import com.project.util.Roles;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/CourseServlet")
public class CourseServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(CourseServlet.class.getName());

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
            response.sendRedirect(request.getContextPath() + "/admin/ManageCourses.jsp");
            return;
        }

        String action = request.getParameter("action");
        CoursesDAO courseDAO = new CoursesDAO();

        try {
            if ("add".equals(action)) {
                String courseName = request.getParameter("courseName");
                if (courseName == null || courseName.trim().isEmpty()) {
                    session.setAttribute("errorMessage", "Course name cannot be empty.");
                } else {
                    Course course = new Course();
                    course.setCourseName(courseName.trim());
                    courseDAO.addCourse(course);
                    session.setAttribute("successMessage", "Course added successfully.");
                }
            } else if ("update".equals(action)) {
                int courseId = Integer.parseInt(request.getParameter("courseId"));
                String courseName = request.getParameter("courseName");
                if (courseName == null || courseName.trim().isEmpty()) {
                    session.setAttribute("errorMessage", "Course name cannot be empty.");
                } else {
                    Course course = new Course();
                    course.setCourseId(courseId);
                    course.setCourseName(courseName.trim());
                    courseDAO.updateCourse(course);
                    session.setAttribute("successMessage", "Course updated successfully.");
                }
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "CourseServlet POST error, action=" + action, e);
            session.setAttribute("errorMessage", "Error: " + e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/admin/ManageCourses.jsp");
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
        CoursesDAO courseDAO = new CoursesDAO();

        try {
            if ("delete".equals(action)) {
                int courseId = Integer.parseInt(request.getParameter("id"));
                courseDAO.deleteCourse(courseId);
                session.setAttribute("successMessage", "Course deleted successfully.");
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "CourseServlet GET error, action=" + action, e);
            session.setAttribute("errorMessage", "Error: " + e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/admin/ManageCourses.jsp");
    }
}
