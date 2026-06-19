<%-- 
    Admin - Manage Evaluation Questions
    List all questions with CRUD operations
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.project.model.User"%>
<%@page import="com.project.model.EvaluationQuestion"%>
<%@page import="java.util.List"%>
<%@page import="com.project.util.HtmlUtil"%>
<%
    User user = (User) session.getAttribute("user");
    if (user == null || !"Admin".equalsIgnoreCase(user.getRole())) {
        response.sendRedirect("../login.jsp");
        return;
    }
    
    request.setAttribute("headerTitle", "University Management System - Manage Evaluation Questions");
    
    List<EvaluationQuestion> questions = (List<EvaluationQuestion>) request.getAttribute("questions");
    String errorMessage = (String) session.getAttribute("errorMessage");
    String successMessage = (String) session.getAttribute("successMessage");
    if (errorMessage != null) {
        session.removeAttribute("errorMessage");
    }
    if (successMessage != null) {
        session.removeAttribute("successMessage");
    }
%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8" />
        <title>Manage Evaluation Questions</title>
        <link rel="icon" href="<%=request.getContextPath()%>/images/graduation.png" sizes="32x32" type="image/png" />
        <link rel="stylesheet" href="<%=request.getContextPath()%>/css/layout.css" />
        <link rel="stylesheet" href="<%=request.getContextPath()%>/css/common.css" />
        <style>
            .questions-table {
                width: 100%;
                margin-top: 20px;
            }
            .questions-table th {
                background: #e67e22;
                color: white;
                padding: 12px;
                text-align: left;
            }
            .questions-table td {
                padding: 10px;
                border-bottom: 1px solid #ddd;
            }
            .questions-table tr:hover {
                background: #f5f5f5;
            }
            .status-active {
                color: green;
                font-weight: bold;
            }
            .status-inactive {
                color: red;
                font-weight: bold;
            }
            .type-rating {
                color: #3498db;
            }
            .type-text {
                color: #9b59b6;
            }
            .action-buttons {
                display: flex;
                gap: 5px;
                flex-wrap: wrap;
            }
            .action-buttons a, .action-buttons form {
                display: inline-block;
            }
            .btn-small {
                padding: 5px 10px;
                font-size: 0.85rem;
                text-decoration: none;
                border-radius: 4px;
                border: none;
                cursor: pointer;
            }
            .btn-edit {
                background: #3498db;
                color: white;
            }
            .btn-delete {
                background: #e74c3c;
                color: white;
            }
            .btn-activate {
                background: #27ae60;
                color: white;
            }
            .btn-deactivate {
                background: #f39c12;
                color: white;
            }
            .btn-move {
                background: #95a5a6;
                color: white;
            }
        </style>
    </head>
    <body>
        <%@ include file="../WEB-INF/jspf/header.jspf" %>
        
        <main class="container">
            <h2>Manage Evaluation Questions</h2>
            
            <nav>
                <a href="<%=request.getContextPath()%>/admin/DashboardAdmin.jsp" class="btn-link">Home</a> &nbsp;
                <a href="<%=request.getContextPath()%>/AdminEvaluationQuestionServlet?action=add" class="btn-link">Add New Question</a>
            </nav>
            
            <% if (errorMessage != null) { %>
            <div style="color: #e74c3c; font-weight: bold; padding: 10px; background: #ffeaea; border-radius: 4px; margin: 15px 0;">
                <%= errorMessage %>
            </div>
            <% } %>
            
            <% if (successMessage != null) { %>
            <div style="color: #27ae60; font-weight: bold; padding: 10px; background: #eafaf1; border-radius: 4px; margin: 15px 0;">
                <%= successMessage %>
            </div>
            <% } %>
            
            <% if (questions != null && !questions.isEmpty()) { %>
            <table class="questions-table" aria-label="Evaluation Questions">
                <thead>
                    <tr>
                        <th>Order</th>
                        <th>Question Text</th>
                        <th>Type</th>
                        <th>Required</th>
                        <th>Status</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <% for (EvaluationQuestion q : questions) { %>
                    <tr>
                        <td><%= q.getDisplayOrder() %></td>
                        <td><%= HtmlUtil.escape(q.getQuestionText()) %></td>
                        <td>
                            <span class="<%= "RATING".equals(q.getQuestionType()) ? "type-rating" : "type-text" %>">
                                <%= q.getQuestionType() %>
                            </span>
                        </td>
                        <td><%= q.isRequired() ? "Yes" : "No" %></td>
                        <td>
                            <% if (q.isActive()) { %>
                            <span class="status-active">Active</span>
                            <% } else { %>
                            <span class="status-inactive">Inactive</span>
                            <% } %>
                        </td>
                        <td>
                            <div class="action-buttons">
                                <a href="<%=request.getContextPath()%>/AdminEvaluationQuestionServlet?action=edit&questionId=<%= q.getQuestionId() %>" 
                                   class="btn-small btn-edit">Edit</a>
                                
                                <% if (q.isActive()) { %>
                                <a href="<%=request.getContextPath()%>/AdminEvaluationQuestionServlet?action=deactivate&questionId=<%= q.getQuestionId() %>" 
                                   class="btn-small btn-deactivate" 
                                   onclick="return confirm('Deactivate this question? It will not appear in future evaluation forms.');">Deactivate</a>
                                <% } else { %>
                                <a href="<%=request.getContextPath()%>/AdminEvaluationQuestionServlet?action=activate&questionId=<%= q.getQuestionId() %>" 
                                   class="btn-small btn-activate">Activate</a>
                                <% } %>
                                
                                <a href="<%=request.getContextPath()%>/AdminEvaluationQuestionServlet?action=delete&questionId=<%= q.getQuestionId() %>" 
                                   class="btn-small btn-delete" 
                                   onclick="return confirm('Delete this question permanently? This action cannot be undone if the question has answers.');">Delete</a>
                                
                                <a href="<%=request.getContextPath()%>/AdminEvaluationQuestionServlet?action=moveUp&questionId=<%= q.getQuestionId() %>" 
                                   class="btn-small btn-move">↑</a>
                                
                                <a href="<%=request.getContextPath()%>/AdminEvaluationQuestionServlet?action=moveDown&questionId=<%= q.getQuestionId() %>" 
                                   class="btn-small btn-move">↓</a>
                            </div>
                        </td>
                    </tr>
                    <% } %>
                </tbody>
            </table>
            <% } else { %>
            <p style="margin-top: 20px;">No questions found. <a href="<%=request.getContextPath()%>/AdminEvaluationQuestionServlet?action=add">Add the first question</a></p>
            <% } %>
        </main>
        
        <%@ include file="../WEB-INF/jspf/footer.jspf" %>
    </body>
</html>

