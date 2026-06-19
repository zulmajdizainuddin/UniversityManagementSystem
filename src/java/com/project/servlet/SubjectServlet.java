package com.project.servlet;

import com.project.dao.SubjectDAO;
import com.project.model.Subject;
import com.project.model.User;
import com.project.util.CsrfUtil;
import com.project.util.Roles;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/SubjectServlet")
public class SubjectServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(SubjectServlet.class.getName());
    private static final String REDIRECT_URL = "/admin/ManageSubjects.jsp";

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
            response.sendRedirect(request.getContextPath() + REDIRECT_URL);
            return;
        }

        String action = request.getParameter("action");
        SubjectDAO subjectDAO = new SubjectDAO();

        try {
            if ("add".equals(action)) {
                createSubject(request, response, session, subjectDAO);
            } else if ("update".equals(action)) {
                updateSubject(request, response, session, subjectDAO);
            } else {
                response.sendRedirect(request.getContextPath() + REDIRECT_URL);
            }
        } catch (NumberFormatException e) {
            session.setAttribute("errorMessage", "Invalid course ID format.");
            response.sendRedirect(request.getContextPath() + REDIRECT_URL);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "SubjectServlet POST error, action=" + action, e);
            session.setAttribute("errorMessage", "Error: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + REDIRECT_URL);
        }
    }

    private void createSubject(HttpServletRequest request, HttpServletResponse response,
            HttpSession session, SubjectDAO subjectDAO) throws Exception, IOException {
        String validationError = validateSubjectForm(request);
        if (validationError != null) {
            session.setAttribute("errorMessage", validationError);
            response.sendRedirect(request.getContextPath() + REDIRECT_URL);
            return;
        }
        Subject subject = buildSubjectFromRequest(request);
        subjectDAO.addSubject(subject);
        session.setAttribute("successMessage", "Subject added successfully.");
        response.sendRedirect(request.getContextPath() + REDIRECT_URL);
    }

    private void updateSubject(HttpServletRequest request, HttpServletResponse response,
            HttpSession session, SubjectDAO subjectDAO) throws Exception, IOException {
        String validationError = validateSubjectForm(request);
        if (validationError != null) {
            session.setAttribute("errorMessage", validationError);
            response.sendRedirect(request.getContextPath() + REDIRECT_URL);
            return;
        }
        Subject subject = buildSubjectFromRequest(request);
        int subjectId = Integer.parseInt(request.getParameter("subjectId"));
        subject.setSubjectId(subjectId);
        subjectDAO.updateSubject(subject);
        session.setAttribute("successMessage", "Subject updated successfully.");
        response.sendRedirect(request.getContextPath() + REDIRECT_URL);
    }

    private String validateSubjectForm(HttpServletRequest request) {
        String subjectCode = request.getParameter("subjectCode");
        String subjectName = request.getParameter("subjectName");
        String courseIdParam = request.getParameter("courseId");
        if (subjectCode == null || subjectCode.trim().isEmpty()) return "Subject code is required.";
        if (subjectName == null || subjectName.trim().isEmpty()) return "Subject name is required.";
        if (courseIdParam == null || courseIdParam.trim().isEmpty()) return "Course selection is required.";
        return null;
    }

    private Subject buildSubjectFromRequest(HttpServletRequest request) {
        Subject subject = new Subject();
        subject.setSubjectCode(request.getParameter("subjectCode").trim());
        subject.setSubjectName(request.getParameter("subjectName").trim());
        subject.setCourseId(Integer.parseInt(request.getParameter("courseId")));
        return subject;
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
        SubjectDAO subjectDAO = new SubjectDAO();

        try {
            if ("delete".equals(action)) {
                int id = Integer.parseInt(request.getParameter("id"));
                subjectDAO.deleteSubject(id);
                session.setAttribute("successMessage", "Subject deleted successfully.");
            }
        } catch (NumberFormatException e) {
            session.setAttribute("errorMessage", "Invalid subject ID format.");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "SubjectServlet GET error, action=" + action, e);
            session.setAttribute("errorMessage", "Error: " + e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + REDIRECT_URL);
    }
}
