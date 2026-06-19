<%-- 
    Admin - Add/Edit Evaluation Question
    Form for creating or editing evaluation questions
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.project.model.User"%>
<%@page import="com.project.model.EvaluationQuestion"%>
<%@page import="com.project.util.HtmlUtil"%>
<%
    User user = (User) session.getAttribute("user");
    if (user == null || !"Admin".equalsIgnoreCase(user.getRole())) {
        response.sendRedirect("../login.jsp");
        return;
    }
    
    request.setAttribute("headerTitle", "University Management System - " + 
        (request.getAttribute("question") != null ? "Edit" : "Add") + " Evaluation Question");
    
    EvaluationQuestion question = (EvaluationQuestion) request.getAttribute("question");
    boolean isEdit = question != null;
    String errorMessage = (String) session.getAttribute("errorMessage");
    if (errorMessage != null) {
        session.removeAttribute("errorMessage");
    }
%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8" />
        <title><%= isEdit ? "Edit" : "Add" %> Evaluation Question</title>
        <link rel="icon" href="<%=request.getContextPath()%>/images/graduation.png" sizes="32x32" type="image/png" />
        <link rel="stylesheet" href="<%=request.getContextPath()%>/css/layout.css" />
        <link rel="stylesheet" href="<%=request.getContextPath()%>/css/common.css" />
        <style>
            .form-container {
                max-width: 600px;
                margin: 20px auto;
                padding: 20px;
                background: white;
                border-radius: 8px;
                box-shadow: 0 2px 8px rgba(0,0,0,0.1);
            }
            .form-group {
                margin-bottom: 20px;
            }
            .form-group label {
                display: block;
                margin-bottom: 5px;
                font-weight: bold;
                color: #333;
            }
            .form-group input[type="text"],
            .form-group textarea,
            .form-group select {
                width: 100%;
                padding: 8px;
                border: 1px solid #ddd;
                border-radius: 4px;
                font-size: 1rem;
                box-sizing: border-box;
            }
            .form-group textarea {
                min-height: 100px;
                resize: vertical;
            }
            .form-group input[type="checkbox"] {
                width: auto;
                margin-right: 5px;
            }
            .form-actions {
                margin-top: 25px;
                text-align: right;
            }
            .form-actions button,
            .form-actions a {
                margin-left: 10px;
            }
            .help-text {
                font-size: 0.9rem;
                color: #666;
                margin-top: 5px;
            }
        </style>
    </head>
    <body>
        <%@ include file="../WEB-INF/jspf/header.jspf" %>
        
        <main class="container">
            <h2><%= isEdit ? "Edit" : "Add" %> Evaluation Question</h2>
            
            <nav>
                <a href="<%=request.getContextPath()%>/AdminEvaluationQuestionServlet" class="btn-link">Back to List</a>
            </nav>
            
            <% if (errorMessage != null) { %>
            <div style="color: #e74c3c; font-weight: bold; padding: 10px; background: #ffeaea; border-radius: 4px; margin: 15px 0;">
                <%= errorMessage %>
            </div>
            <% } %>
            
            <div class="form-container">
                <form method="post" action="<%=request.getContextPath()%>/AdminEvaluationQuestionServlet">
                    <input type="hidden" name="action" value="<%= isEdit ? "update" : "add" %>" />
                    <input type="hidden" name="csrfToken" value="<%= session.getAttribute("csrfToken") %>" />
                    <% if (isEdit) { %>
                    <input type="hidden" name="questionId" value="<%= question.getQuestionId() %>" />
                    <% } %>
                    
                    <div class="form-group">
                        <label for="questionText">Question Text: <span style="color: red;">*</span></label>
                        <textarea id="questionText" name="questionText" required
                                  placeholder="Enter the question text..."><%= isEdit ? HtmlUtil.escape(question.getQuestionText()) : "" %></textarea>
                        <div class="help-text">This text will be displayed to students in the evaluation form.</div>
                    </div>
                    
                    <div class="form-group">
                        <label for="questionType">Question Type: <span style="color: red;">*</span></label>
                        <select id="questionType" name="questionType" required>
                            <option value="">-- Select Type --</option>
                            <option value="RATING" <%= isEdit && "RATING".equals(question.getQuestionType()) ? "selected" : "" %>>RATING (1-5 scale)</option>
                            <option value="TEXT" <%= isEdit && "TEXT".equals(question.getQuestionType()) ? "selected" : "" %>>TEXT (free text comment)</option>
                        </select>
                        <div class="help-text">
                            RATING: Students select 1-5 (Strongly Disagree to Strongly Agree)<br>
                            TEXT: Students provide free-form text comments
                        </div>
                    </div>
                    
                    <div class="form-group">
                        <label>
                            <input type="checkbox" name="isRequired" value="true" 
                                   <%= isEdit && question.isRequired() ? "checked" : "" %> />
                            Required Question
                        </label>
                        <div class="help-text">If checked, students must answer this question before submitting the evaluation.</div>
                    </div>
                    
                    <div class="form-group">
                        <label>
                            <input type="checkbox" name="isActive" value="true" 
                                   <%= isEdit && question.isActive() ? "checked" : (!isEdit ? "checked" : "") %> />
                            Active Question
                        </label>
                        <div class="help-text">If checked, this question will appear in the evaluation form. Uncheck to hide it without deleting.</div>
                    </div>
                    
                    <div class="form-group">
                        <label for="displayOrder">Display Order:</label>
                        <input type="number" id="displayOrder" name="displayOrder" 
                               value="<%= isEdit ? question.getDisplayOrder() : "" %>" 
                               min="1" />
                        <div class="help-text">Leave empty to append at the end. Lower numbers appear first.</div>
                    </div>
                    
                    <div class="form-actions">
                        <button type="submit" class="btn-primary">
                            <%= isEdit ? "Update Question" : "Add Question" %>
                        </button>
                        <a href="<%=request.getContextPath()%>/AdminEvaluationQuestionServlet" class="btn-link">Cancel</a>
                    </div>
                </form>
            </div>
        </main>
        
        <%@ include file="../WEB-INF/jspf/footer.jspf" %>
    </body>
</html>

