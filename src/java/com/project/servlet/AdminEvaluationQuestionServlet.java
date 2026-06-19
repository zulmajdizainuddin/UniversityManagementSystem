package com.project.servlet;

import com.project.dao.EvaluationQuestionDAO;
import com.project.model.EvaluationQuestion;
import com.project.model.User;
import com.project.util.CsrfUtil;
import com.project.util.Roles;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/AdminEvaluationQuestionServlet")
public class AdminEvaluationQuestionServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(AdminEvaluationQuestionServlet.class.getName());
    private static final String LIST_PAGE = "/admin/admin_manage_evaluation_questions.jsp";
    private static final String EDIT_PAGE = "/admin/admin_edit_evaluation_question.jsp";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;
        if (user == null || !Roles.ADMIN.equalsIgnoreCase(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String action = request.getParameter("action");
        if (action == null) action = "list";

        EvaluationQuestionDAO dao = new EvaluationQuestionDAO();

        try {
            switch (action) {
                case "list":     handleList(request, response, dao); break;
                case "add":      handleAddForm(request, response); break;
                case "edit":     handleEditForm(request, response, dao); break;
                case "delete":   handleDelete(request, response, dao); break;
                case "deactivate": handleDeactivate(request, response, dao); break;
                case "activate": handleActivate(request, response, dao); break;
                case "moveUp":   handleMoveUp(request, response, dao); break;
                case "moveDown": handleMoveDown(request, response, dao); break;
                default:         handleList(request, response, dao); break;
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "AdminEvaluationQuestionServlet GET error, action=" + action, e);
            request.setAttribute("errorMessage", "Error: " + e.getMessage());
            request.getRequestDispatcher(LIST_PAGE).forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;
        if (user == null || !Roles.ADMIN.equalsIgnoreCase(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        if (!CsrfUtil.isValidToken(request)) {
            session.setAttribute("errorMessage", "Invalid request. Please try again.");
            response.sendRedirect(request.getContextPath() + "/AdminEvaluationQuestionServlet");
            return;
        }

        String action = request.getParameter("action");
        EvaluationQuestionDAO dao = new EvaluationQuestionDAO();

        try {
            if ("add".equals(action)) {
                handleAdd(request, response, dao);
            } else if ("update".equals(action)) {
                handleUpdate(request, response, dao);
            } else {
                response.sendRedirect(request.getContextPath() + "/AdminEvaluationQuestionServlet");
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "AdminEvaluationQuestionServlet POST error, action=" + action, e);
            session.setAttribute("errorMessage", "Error: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/AdminEvaluationQuestionServlet");
        }
    }

    private void handleList(HttpServletRequest request, HttpServletResponse response, EvaluationQuestionDAO dao)
            throws Exception {
        List<EvaluationQuestion> questions = dao.getAllQuestions();
        request.setAttribute("questions", questions);
        request.getRequestDispatcher(LIST_PAGE).forward(request, response);
    }

    private void handleAddForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher(EDIT_PAGE).forward(request, response);
    }

    private void handleEditForm(HttpServletRequest request, HttpServletResponse response, EvaluationQuestionDAO dao)
            throws Exception {
        String questionIdParam = request.getParameter("questionId");
        if (questionIdParam == null || questionIdParam.trim().isEmpty()) {
            request.getSession().setAttribute("errorMessage", "Question ID is required.");
            response.sendRedirect(request.getContextPath() + "/AdminEvaluationQuestionServlet");
            return;
        }
        int questionId = Integer.parseInt(questionIdParam);
        EvaluationQuestion question = dao.getQuestionById(questionId);
        if (question == null) {
            request.getSession().setAttribute("errorMessage", "Question not found.");
            response.sendRedirect(request.getContextPath() + "/AdminEvaluationQuestionServlet");
            return;
        }
        request.setAttribute("question", question);
        request.getRequestDispatcher(EDIT_PAGE).forward(request, response);
    }

    private void handleAdd(HttpServletRequest request, HttpServletResponse response, EvaluationQuestionDAO dao)
            throws Exception {
        EvaluationQuestion question = buildQuestionFromRequest(request);
        String validationError = validateQuestion(question);
        if (validationError != null) {
            request.getSession().setAttribute("errorMessage", validationError);
            response.sendRedirect(request.getContextPath() + "/AdminEvaluationQuestionServlet?action=add");
            return;
        }
        dao.addQuestion(question);
        request.getSession().setAttribute("successMessage", "Question added successfully.");
        response.sendRedirect(request.getContextPath() + "/AdminEvaluationQuestionServlet");
    }

    private void handleUpdate(HttpServletRequest request, HttpServletResponse response, EvaluationQuestionDAO dao)
            throws Exception {
        String questionIdParam = request.getParameter("questionId");
        if (questionIdParam == null || questionIdParam.trim().isEmpty()) {
            request.getSession().setAttribute("errorMessage", "Question ID is required.");
            response.sendRedirect(request.getContextPath() + "/AdminEvaluationQuestionServlet");
            return;
        }
        int questionId = Integer.parseInt(questionIdParam);
        EvaluationQuestion question = buildQuestionFromRequest(request);
        question.setQuestionId(questionId);
        String validationError = validateQuestion(question);
        if (validationError != null) {
            request.getSession().setAttribute("errorMessage", validationError);
            response.sendRedirect(request.getContextPath() + "/AdminEvaluationQuestionServlet?action=edit&questionId=" + questionId);
            return;
        }
        dao.updateQuestion(question);
        request.getSession().setAttribute("successMessage", "Question updated successfully.");
        response.sendRedirect(request.getContextPath() + "/AdminEvaluationQuestionServlet");
    }

    private void handleDelete(HttpServletRequest request, HttpServletResponse response, EvaluationQuestionDAO dao)
            throws Exception {
        String questionIdParam = request.getParameter("questionId");
        if (questionIdParam == null || questionIdParam.trim().isEmpty()) {
            request.getSession().setAttribute("errorMessage", "Question ID is required.");
            response.sendRedirect(request.getContextPath() + "/AdminEvaluationQuestionServlet");
            return;
        }
        dao.deleteQuestion(Integer.parseInt(questionIdParam));
        request.getSession().setAttribute("successMessage", "Question deleted successfully.");
        response.sendRedirect(request.getContextPath() + "/AdminEvaluationQuestionServlet");
    }

    private void handleDeactivate(HttpServletRequest request, HttpServletResponse response, EvaluationQuestionDAO dao)
            throws Exception {
        String questionIdParam = request.getParameter("questionId");
        if (questionIdParam == null || questionIdParam.trim().isEmpty()) {
            request.getSession().setAttribute("errorMessage", "Question ID is required.");
            response.sendRedirect(request.getContextPath() + "/AdminEvaluationQuestionServlet");
            return;
        }
        dao.deactivateQuestion(Integer.parseInt(questionIdParam));
        request.getSession().setAttribute("successMessage", "Question deactivated successfully.");
        response.sendRedirect(request.getContextPath() + "/AdminEvaluationQuestionServlet");
    }

    private void handleActivate(HttpServletRequest request, HttpServletResponse response, EvaluationQuestionDAO dao)
            throws Exception {
        String questionIdParam = request.getParameter("questionId");
        if (questionIdParam == null || questionIdParam.trim().isEmpty()) {
            request.getSession().setAttribute("errorMessage", "Question ID is required.");
            response.sendRedirect(request.getContextPath() + "/AdminEvaluationQuestionServlet");
            return;
        }
        int questionId = Integer.parseInt(questionIdParam);
        EvaluationQuestion question = dao.getQuestionById(questionId);
        question.setActive(true);
        dao.updateQuestion(question);
        request.getSession().setAttribute("successMessage", "Question activated successfully.");
        response.sendRedirect(request.getContextPath() + "/AdminEvaluationQuestionServlet");
    }

    private void handleMoveUp(HttpServletRequest request, HttpServletResponse response, EvaluationQuestionDAO dao)
            throws Exception {
        String questionIdParam = request.getParameter("questionId");
        if (questionIdParam == null || questionIdParam.trim().isEmpty()) {
            request.getSession().setAttribute("errorMessage", "Question ID is required.");
            response.sendRedirect(request.getContextPath() + "/AdminEvaluationQuestionServlet");
            return;
        }
        int questionId = Integer.parseInt(questionIdParam);
        List<EvaluationQuestion> questions = dao.getAllQuestions();
        EvaluationQuestion current = null, previous = null;
        for (EvaluationQuestion q : questions) {
            if (q.getQuestionId() == questionId) { current = q; break; }
            previous = q;
        }
        if (current != null && previous != null) {
            dao.swapDisplayOrder(current.getQuestionId(), previous.getQuestionId());
            request.getSession().setAttribute("successMessage", "Question order updated.");
        }
        response.sendRedirect(request.getContextPath() + "/AdminEvaluationQuestionServlet");
    }

    private void handleMoveDown(HttpServletRequest request, HttpServletResponse response, EvaluationQuestionDAO dao)
            throws Exception {
        String questionIdParam = request.getParameter("questionId");
        if (questionIdParam == null || questionIdParam.trim().isEmpty()) {
            request.getSession().setAttribute("errorMessage", "Question ID is required.");
            response.sendRedirect(request.getContextPath() + "/AdminEvaluationQuestionServlet");
            return;
        }
        int questionId = Integer.parseInt(questionIdParam);
        List<EvaluationQuestion> questions = dao.getAllQuestions();
        EvaluationQuestion current = null, next = null;
        boolean found = false;
        for (EvaluationQuestion q : questions) {
            if (found && next == null) { next = q; break; }
            if (q.getQuestionId() == questionId) { current = q; found = true; }
        }
        if (current != null && next != null) {
            dao.swapDisplayOrder(current.getQuestionId(), next.getQuestionId());
            request.getSession().setAttribute("successMessage", "Question order updated.");
        }
        response.sendRedirect(request.getContextPath() + "/AdminEvaluationQuestionServlet");
    }

    private EvaluationQuestion buildQuestionFromRequest(HttpServletRequest request) {
        EvaluationQuestion question = new EvaluationQuestion();
        question.setQuestionText(request.getParameter("questionText"));
        question.setQuestionType(request.getParameter("questionType"));
        question.setRequired("true".equals(request.getParameter("isRequired")) || "on".equals(request.getParameter("isRequired")));
        question.setActive("true".equals(request.getParameter("isActive")) || "on".equals(request.getParameter("isActive")));
        String displayOrderParam = request.getParameter("displayOrder");
        if (displayOrderParam != null && !displayOrderParam.trim().isEmpty()) {
            question.setDisplayOrder(Integer.parseInt(displayOrderParam));
        }
        return question;
    }

    private String validateQuestion(EvaluationQuestion question) {
        if (question.getQuestionText() == null || question.getQuestionText().trim().isEmpty()) {
            return "Question text is required.";
        }
        if (question.getQuestionType() == null ||
                (!question.getQuestionType().equals("RATING") && !question.getQuestionType().equals("TEXT"))) {
            return "Question type must be RATING or TEXT.";
        }
        return null;
    }
}
