package com.project.servlet;

import com.project.dao.CourseEvaluationDAO;
import com.project.dao.CourseEvaluationDynamicDAO;
import com.project.dao.SubjectDAO;
import com.project.model.CourseEvaluationHeader;
import com.project.model.Subject;
import com.project.model.User;
import com.project.util.Roles;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/LecturerEvaluationServlet")
public class LecturerEvaluationServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(LecturerEvaluationServlet.class.getName());
    private static final String VIEW_PAGE = "/lecturer/lecturer_view_evaluation.jsp";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;
        if (user == null || !Roles.LECTURER.equalsIgnoreCase(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        int lecturerId = user.getUserId();
        String subjectIdParam = request.getParameter("subjectId");

        try {
            CourseEvaluationDAO evalDAO = new CourseEvaluationDAO();
            SubjectDAO subjectDAO = new SubjectDAO();

            List<Integer> lecturerSubjectIds = evalDAO.getSubjectIdsByLecturer(lecturerId);
            List<Subject> lecturerSubjects = new ArrayList<>();
            for (Integer subjectId : lecturerSubjectIds) {
                Subject subject = subjectDAO.getSubjectById(subjectId);
                if (subject != null) lecturerSubjects.add(subject);
            }
            request.setAttribute("lecturerSubjects", lecturerSubjects);

            if (subjectIdParam != null && !subjectIdParam.trim().isEmpty()) {
                int subjectId = Integer.parseInt(subjectIdParam);

                if (!lecturerSubjectIds.contains(subjectId)) {
                    request.setAttribute("errorMessage", "You are not authorized to view evaluations for this subject.");
                } else {
                    Subject selectedSubject = subjectDAO.getSubjectById(subjectId);

                    CourseEvaluationDynamicDAO dynamicDAO = new CourseEvaluationDynamicDAO();
                    CourseEvaluationDynamicDAO.EvaluationSummary summary = dynamicDAO.getEvaluationSummary(lecturerId, subjectId);
                    Map<Integer, List<String>> textAnswers = dynamicDAO.getTextAnswers(lecturerId, subjectId);
                    List<CourseEvaluationHeader> evaluations = dynamicDAO.getEvaluationsForLecturer(lecturerId, subjectId);

                    request.setAttribute("selectedSubject", selectedSubject);
                    request.setAttribute("summary", summary);
                    request.setAttribute("textAnswers", textAnswers);
                    request.setAttribute("evaluations", evaluations);
                }
            } else {
                request.setAttribute("errorMessage", "Please select a subject before viewing evaluations.");
            }

            request.getRequestDispatcher(VIEW_PAGE).forward(request, response);

        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Invalid subject ID.");
            request.getRequestDispatcher(VIEW_PAGE).forward(request, response);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "LecturerEvaluationServlet GET error", e);
            request.setAttribute("errorMessage", "Error: " + e.getMessage());
            request.getRequestDispatcher(VIEW_PAGE).forward(request, response);
        }
    }
}
