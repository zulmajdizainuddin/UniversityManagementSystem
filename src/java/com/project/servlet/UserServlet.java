package com.project.servlet;

import com.project.dao.UserDAO;
import com.project.model.Staff;
import com.project.model.Student;
import com.project.model.User;
import com.project.util.CsrfUtil;
import com.project.util.Roles;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/UserServlet")
public class UserServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(UserServlet.class.getName());

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
            response.sendRedirect(request.getContextPath() + "/admin/ManageUsers.jsp");
            return;
        }

        String action = request.getParameter("action");

        try {
            UserDAO userDAO = new UserDAO();

            String roleParam = request.getParameter("role");
            if (roleParam != null && !roleParam.isEmpty()) {
                roleParam = roleParam.substring(0, 1).toUpperCase() + roleParam.substring(1).toLowerCase();
            }

            if ("add".equals(action)) {
                User user;
                if (Roles.STUDENT.equalsIgnoreCase(roleParam)) {
                    Student student = new Student();
                    student.setStudentNumber(request.getParameter("studentNumber"));
                    student.setMajor(request.getParameter("major"));
                    user = student;
                } else if (Roles.LECTURER.equalsIgnoreCase(roleParam) || Roles.ADMIN.equalsIgnoreCase(roleParam)) {
                    Staff staff = new Staff();
                    staff.setStaffNumber(request.getParameter("staffNumber"));
                    staff.setDepartment(request.getParameter("department"));
                    user = staff;
                } else {
                    user = new User();
                }

                user.setName(request.getParameter("name"));
                user.setEmail(request.getParameter("email"));
                user.setPassword(request.getParameter("password"));
                user.setRole(roleParam);

                userDAO.addUser(user);
                session.setAttribute("successMessage", "User added successfully.");

            } else if ("update".equals(action)) {
                int userId = Integer.parseInt(request.getParameter("userId"));
                User existingUser = userDAO.getUserById(userId);
                if (existingUser == null) {
                    session.setAttribute("errorMessage", "User not found.");
                    response.sendRedirect(request.getContextPath() + "/admin/ManageUsers.jsp");
                    return;
                }

                User user;
                if (Roles.STUDENT.equalsIgnoreCase(roleParam)) {
                    Student student = new Student();
                    student.setStudentNumber(request.getParameter("studentNumber"));
                    student.setMajor(request.getParameter("major"));
                    user = student;
                } else if (Roles.LECTURER.equalsIgnoreCase(roleParam) || Roles.ADMIN.equalsIgnoreCase(roleParam)) {
                    Staff staff = new Staff();
                    staff.setStaffNumber(request.getParameter("staffNumber"));
                    staff.setDepartment(request.getParameter("department"));
                    user = staff;
                } else {
                    user = new User();
                }

                user.setUserId(userId);
                user.setName(request.getParameter("name"));
                user.setEmail(request.getParameter("email"));

                String newPassword = request.getParameter("password");
                if (newPassword == null || newPassword.trim().isEmpty()) {
                    user.setPassword(existingUser.getPassword());
                } else {
                    user.setPassword(newPassword);
                }

                user.setRole(roleParam);
                userDAO.updateUser(user);
                session.setAttribute("successMessage", "User updated successfully.");
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "UserServlet POST error, action=" + action, e);
            session.setAttribute("errorMessage", "Error: " + e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/admin/ManageUsers.jsp");
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

        try {
            UserDAO userDAO = new UserDAO();

            if ("delete".equals(action)) {
                int id = Integer.parseInt(request.getParameter("id"));
                User userToDelete = userDAO.getUserById(id);

                if (userToDelete != null && !Roles.ADMIN.equalsIgnoreCase(userToDelete.getRole())) {
                    userDAO.deleteUser(id);
                    session.setAttribute("successMessage", "User deleted successfully.");
                } else {
                    session.setAttribute("errorMessage", "Admin users cannot be deleted.");
                }
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "UserServlet GET error, action=" + action, e);
            session.setAttribute("errorMessage", "Error: " + e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/admin/ManageUsers.jsp");
    }
}
