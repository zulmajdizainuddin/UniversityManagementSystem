package com.project.servlet;

import com.project.dao.*;
import com.project.model.*;
import com.project.util.CsrfUtil;
import com.project.util.Roles;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/GradeServlet")
public class GradeServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(GradeServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null || !Roles.LECTURER.equalsIgnoreCase(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String action = request.getParameter("action");
        if (action == null || action.isEmpty()) action = "viewSubjects";

        try {
            SubjectDAO subjectDAO = new SubjectDAO();
            GradeDAO gradeDAO = new GradeDAO();
            UserDAO userDAO = new UserDAO();

            switch (action) {
                case "viewSubjects":
                    List<Subject> subjects = subjectDAO.getSubjectsByLecturer(user.getUserId());
                    request.setAttribute("subjects", subjects);
                    request.setAttribute("action", "viewSubjects");
                    request.getRequestDispatcher("/lecturer/ManageGrades.jsp").forward(request, response);
                    break;

                case "viewGrades":
                    String subjectIdParam = request.getParameter("subjectId");
                    if (subjectIdParam == null || subjectIdParam.isEmpty()) {
                        response.sendRedirect(request.getContextPath() + "/GradeServlet?action=viewSubjects");
                        return;
                    }
                    int subjectId = Integer.parseInt(subjectIdParam);

                    // Ownership check
                    LecturerSubjectDAO lsDao = new LecturerSubjectDAO();
                    if (!lsDao.lecturerTeachesSubject(user.getUserId(), subjectId)) {
                        response.sendRedirect(request.getContextPath() + "/accessDenied.jsp");
                        return;
                    }

                    List<Grade> grades = gradeDAO.getGradesBySubject(subjectId);
                    List<Student> enrolledStudents = userDAO.getStudentsBySubject(subjectId);
                    request.setAttribute("grades", grades);
                    request.setAttribute("students", enrolledStudents);
                    request.setAttribute("selectedSubjectId", subjectId);
                    request.setAttribute("action", "viewGrades");
                    List<Subject> subjectsForView = subjectDAO.getSubjectsByLecturer(user.getUserId());
                    request.setAttribute("subjects", subjectsForView);
                    request.getRequestDispatcher("/lecturer/ManageGrades.jsp").forward(request, response);
                    break;

                default:
                    response.sendRedirect(request.getContextPath() + "/GradeServlet?action=viewSubjects");
                    break;
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "GradeServlet GET error", e);
            session.setAttribute("errorMessage", "An error occurred. Please try again.");
            response.sendRedirect(request.getContextPath() + "/GradeServlet?action=viewSubjects");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null || !Roles.LECTURER.equalsIgnoreCase(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        // CSRF check
        if (!CsrfUtil.isValidToken(request)) {
            session.setAttribute("errorMessage", "Invalid request. Please try again.");
            response.sendRedirect(request.getContextPath() + "/GradeServlet?action=viewSubjects");
            return;
        }

        String action = request.getParameter("action");
        if (action == null || action.isEmpty()) action = "saveGrades";

        try {
            GradeDAO gradeDAO = new GradeDAO();
            LecturerSubjectDAO lsDao = new LecturerSubjectDAO();
            int lecturerId = user.getUserId();

            switch (action) {
                case "saveGrades":
                    String subjectIdParam = request.getParameter("subjectId");
                    if (subjectIdParam == null || subjectIdParam.isEmpty()) {
                        session.setAttribute("errorMessage", "Subject not selected.");
                        response.sendRedirect(request.getContextPath() + "/GradeServlet?action=viewSubjects");
                        return;
                    }
                    int subjectId = Integer.parseInt(subjectIdParam);

                    // Ownership check
                    if (!lsDao.lecturerTeachesSubject(lecturerId, subjectId)) {
                        response.sendRedirect(request.getContextPath() + "/accessDenied.jsp");
                        return;
                    }

                    String[] studentIds = request.getParameterValues("studentIds");
                    if (studentIds == null || studentIds.length == 0) {
                        session.setAttribute("errorMessage", "No students to grade.");
                        response.sendRedirect(request.getContextPath() + "/GradeServlet?action=viewGrades&subjectId=" + subjectId);
                        return;
                    }

                    for (String sid : studentIds) {
                        int studentId = Integer.parseInt(sid);
                        String scoreStr = request.getParameter("score_" + studentId);
                        double score = 0.0;
                        try {
                            score = Double.parseDouble(scoreStr);
                            if (score < 0) score = 0;
                            if (score > 100) score = 100;
                        } catch (NumberFormatException ex) {
                            // Keep 0.0 for invalid input
                        }
                        Grade grade = new Grade();
                        grade.setStudentId(studentId);
                        grade.setSubjectId(subjectId);
                        grade.setScore(score);
                        grade.setLecturerId(lecturerId);
                        gradeDAO.addOrUpdateGrade(grade);
                    }

                    session.setAttribute("successMessage", "Grades saved successfully.");
                    response.sendRedirect(request.getContextPath() + "/GradeServlet?action=viewGrades&subjectId=" + subjectId);
                    break;

                default:
                    response.sendRedirect(request.getContextPath() + "/GradeServlet?action=viewSubjects");
                    break;
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "GradeServlet POST error", e);
            session.setAttribute("errorMessage", "An error occurred. Please try again.");
            response.sendRedirect(request.getContextPath() + "/GradeServlet?action=viewSubjects");
        }
    }
}
