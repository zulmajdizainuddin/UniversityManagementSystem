package com.project.servlet;

import com.project.dao.UserDAO;
import com.project.model.User;
import com.project.util.CsrfUtil;
import com.project.util.Roles;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(LoginServlet.class.getName());

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        try {
            UserDAO userDAO = new UserDAO();
            User user = userDAO.getUserByEmail(email);

            if (user != null && UserDAO.checkPassword(password, user.getPassword())) {
                HttpSession session = request.getSession();
                session.setAttribute("user", user);
                session.setMaxInactiveInterval(30 * 60);

                // Generate CSRF token for the new session
                CsrfUtil.generateToken(session);

                switch (user.getRole().toLowerCase()) {
                    case "admin":
                        response.sendRedirect(request.getContextPath() + "/admin/DashboardAdmin.jsp");
                        break;
                    case "lecturer":
                        response.sendRedirect(request.getContextPath() + "/lecturer/DashboardLecturer.jsp");
                        break;
                    case "student":
                        response.sendRedirect(request.getContextPath() + "/student/DashboardStudent.jsp");
                        break;
                    default:
                        response.sendRedirect(request.getContextPath() + "/login.jsp?error=invalidrole");
                        break;
                }
            } else {
                response.sendRedirect(request.getContextPath() + "/login.jsp?error=invalid");
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Login error for email: " + email, e);
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=server");
        }
    }
}
