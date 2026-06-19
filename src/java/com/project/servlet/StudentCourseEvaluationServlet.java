package com.project.servlet;

import com.project.dao.CourseEvaluationDAO;
import com.project.dao.CourseEvaluationDynamicDAO;
import com.project.dao.EvaluationQuestionDAO;
import com.project.dao.SubjectDAO;
import com.project.model.CourseEvaluationHeader;
import com.project.model.EvaluationQuestion;
import com.project.model.Subject;
import com.project.model.User;
import com.project.util.CsrfUtil;
import com.project.util.HtmlUtil;
import com.project.util.Roles;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/StudentCourseEvaluationServlet")
public class StudentCourseEvaluationServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(StudentCourseEvaluationServlet.class.getName());
    private static final String EVALUATION_FORM_PAGE = "/student/student_course_evaluation.jsp";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;
        if (user == null || !Roles.STUDENT.equalsIgnoreCase(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String subjectIdParam = request.getParameter("subjectId");
        if (subjectIdParam == null || subjectIdParam.trim().isEmpty()) {
            session.setAttribute("errorMessage", "Please select a subject.");
            response.sendRedirect(request.getContextPath() + "/student/DashboardStudent.jsp");
            return;
        }

        try {
            int subjectId = Integer.parseInt(subjectIdParam);
            int studentId = user.getUserId();

            SubjectDAO subjectDAO = new SubjectDAO();
            CourseEvaluationDAO evalDAO = new CourseEvaluationDAO();

            Subject subject = subjectDAO.getSubjectById(subjectId);
            if (subject == null) {
                session.setAttribute("errorMessage", "Subject not found.");
                response.sendRedirect(request.getContextPath() + "/student/DashboardStudent.jsp");
                return;
            }

            List<Subject> enrolledSubjects = subjectDAO.getEnrolledSubjectsByStudent(studentId);
            boolean isEnrolled = enrolledSubjects.stream().anyMatch(s -> s.getSubjectId() == subjectId);
            if (!isEnrolled) {
                session.setAttribute("errorMessage", "You are not enrolled in this subject.");
                response.sendRedirect(request.getContextPath() + "/student/DashboardStudent.jsp");
                return;
            }

            Integer lecturerId = evalDAO.getLecturerIdForSubject(subjectId);
            if (lecturerId == null) {
                session.setAttribute("errorMessage", "No lecturer assigned to this subject.");
                response.sendRedirect(request.getContextPath() + "/student/DashboardStudent.jsp");
                return;
            }

            CourseEvaluationDynamicDAO dynamicDAO = new CourseEvaluationDynamicDAO();
            boolean alreadySubmitted = dynamicDAO.hasStudentSubmitted(studentId, subjectId);

            EvaluationQuestionDAO questionDAO = new EvaluationQuestionDAO();
            List<EvaluationQuestion> questions = questionDAO.getActiveQuestions();

            request.setAttribute("subject", subject);
            request.setAttribute("lecturerId", lecturerId);
            request.setAttribute("alreadySubmitted", alreadySubmitted);
            request.setAttribute("questions", questions);
            request.getRequestDispatcher(EVALUATION_FORM_PAGE).forward(request, response);

        } catch (NumberFormatException e) {
            session.setAttribute("errorMessage", "Invalid subject ID.");
            response.sendRedirect(request.getContextPath() + "/student/DashboardStudent.jsp");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "StudentCourseEvaluationServlet GET error", e);
            session.setAttribute("errorMessage", "Error: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/student/DashboardStudent.jsp");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;
        if (user == null || !Roles.STUDENT.equalsIgnoreCase(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        if (!CsrfUtil.isValidToken(request)) {
            session.setAttribute("errorMessage", "Invalid request. Please try again.");
            response.sendRedirect(request.getContextPath() + "/student/DashboardStudent.jsp");
            return;
        }

        try {
            int studentId = user.getUserId();
            int subjectId = Integer.parseInt(request.getParameter("subjectId"));
            int lecturerId = Integer.parseInt(request.getParameter("lecturerId"));

            EvaluationQuestionDAO questionDAO = new EvaluationQuestionDAO();
            List<EvaluationQuestion> questions = questionDAO.getActiveQuestions();

            String validationError = validateAnswers(request, questions);
            if (validationError != null) {
                session.setAttribute("errorMessage", validationError);
                response.sendRedirect(request.getContextPath() + "/StudentCourseEvaluationServlet?subjectId=" + subjectId);
                return;
            }

            Map<Integer, Object> answers = new HashMap<>();
            for (EvaluationQuestion question : questions) {
                String paramName = "question_" + question.getQuestionId();
                String answerValue = request.getParameter(paramName);

                if (answerValue != null && !answerValue.trim().isEmpty()) {
                    if ("RATING".equals(question.getQuestionType())) {
                        try {
                            int rating = Integer.parseInt(answerValue);
                            if (rating >= 1 && rating <= 5) {
                                answers.put(question.getQuestionId(), rating);
                            }
                        } catch (NumberFormatException e) {
                            // Skip invalid rating
                        }
                    } else if ("TEXT".equals(question.getQuestionType())) {
                        String escaped = HtmlUtil.escape(answerValue.trim());
                        if (!escaped.isEmpty()) {
                            answers.put(question.getQuestionId(), escaped);
                        }
                    }
                }
            }

            CourseEvaluationHeader header = new CourseEvaluationHeader(studentId, subjectId, lecturerId);
            CourseEvaluationDynamicDAO dynamicDAO = new CourseEvaluationDynamicDAO();
            dynamicDAO.submitEvaluation(header, answers);

            session.setAttribute("successMessage", "Thank you! Your evaluation has been submitted successfully.");
            response.sendRedirect(request.getContextPath() + "/student/evaluation_success.jsp");

        } catch (NumberFormatException e) {
            session.setAttribute("errorMessage", "Invalid input. Please check your answers.");
            response.sendRedirect(request.getContextPath() + "/student/DashboardStudent.jsp");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "StudentCourseEvaluationServlet POST error", e);
            session.setAttribute("errorMessage", "Error: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/student/DashboardStudent.jsp");
        }
    }

    private String validateAnswers(HttpServletRequest request, List<EvaluationQuestion> questions) {
        for (EvaluationQuestion question : questions) {
            String paramName = "question_" + question.getQuestionId();
            String value = request.getParameter(paramName);

            if (question.isRequired() && (value == null || value.trim().isEmpty())) {
                return "Please answer all required questions. Question: " + question.getQuestionText();
            }

            if (value != null && !value.trim().isEmpty() && "RATING".equals(question.getQuestionType())) {
                try {
                    int rating = Integer.parseInt(value);
                    if (rating < 1 || rating > 5) {
                        return "Rating must be between 1 and 5. Question: " + question.getQuestionText();
                    }
                } catch (NumberFormatException e) {
                    return "Invalid rating value for question: " + question.getQuestionText();
                }
            }
        }
        return null;
    }
}
