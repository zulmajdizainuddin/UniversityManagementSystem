<%-- 
    Document   : lecturer_view_evaluation
    Created on : Lecturer view course evaluation results
    Author     : ZULMAJDI
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.project.model.User"%>
<%@page import="com.project.model.Subject"%>
<%@page import="com.project.model.CourseEvaluationHeader"%>
<%@page import="com.project.dao.CourseEvaluationDynamicDAO"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%@page import="com.project.util.HtmlUtil"%>
<%
    User user = (User) session.getAttribute("user");
    if (user == null || !"Lecturer".equalsIgnoreCase(user.getRole())) {
        response.sendRedirect("../login.jsp");
        return;
    }
    
    request.setAttribute("headerTitle", "University Management System - View Course Evaluations");
    
    List<Subject> lecturerSubjects = (List<Subject>) request.getAttribute("lecturerSubjects");
    Subject selectedSubject = (Subject) request.getAttribute("selectedSubject");
    CourseEvaluationDynamicDAO.EvaluationSummary summary = (CourseEvaluationDynamicDAO.EvaluationSummary) request.getAttribute("summary");
    Map<Integer, List<String>> textAnswers = (Map<Integer, List<String>>) request.getAttribute("textAnswers");
    List<CourseEvaluationHeader> evaluations = (List<CourseEvaluationHeader>) request.getAttribute("evaluations");
    
    String errorMessage = (String) request.getAttribute("errorMessage");
%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8" />
        <title>View Course Evaluations</title>
        <link rel="icon" href="<%=request.getContextPath()%>/images/graduation.png" sizes="32x32" type="image/png" />
        <link rel="stylesheet" href="<%=request.getContextPath()%>/css/layout.css" />
        <link rel="stylesheet" href="<%=request.getContextPath()%>/css/common.css" />
        <style>
            .evaluation-container {
                max-width: 1200px;
                margin: 20px auto;
            }
            .selection-form {
                background: #f8f9fa;
                padding: 20px;
                border-radius: 8px;
                margin-bottom: 20px;
            }
            .selection-form select, .selection-form input {
                padding: 8px;
                margin: 0 10px;
                border: 1px solid #ddd;
                border-radius: 4px;
            }
            .summary-section {
                background: white;
                padding: 20px;
                border-radius: 8px;
                margin-bottom: 20px;
                box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            }
            .summary-stats {
                display: grid;
                grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
                gap: 15px;
                margin: 20px 0;
            }
            .stat-card {
                background: #f8f9fa;
                padding: 15px;
                border-radius: 6px;
                text-align: center;
        }
            .stat-card h4 {
                margin: 0 0 10px 0;
                color: #333;
            }
            .stat-value {
                font-size: 2rem;
                font-weight: bold;
                color: #e67e22;
            }
            .question-table {
                width: 100%;
                border-collapse: collapse;
                margin: 20px 0;
            }
            .question-table th, .question-table td {
                padding: 12px;
                text-align: left;
                border-bottom: 1px solid #ddd;
            }
            .question-table th {
                background: #e67e22;
                color: white;
            }
            .question-table tr:hover {
                background: #f8f9fa;
            }
            .comments-section {
                margin-top: 30px;
            }
            .comment-box {
                background: #f8f9fa;
                padding: 15px;
                border-radius: 6px;
                margin: 10px 0;
                border-left: 4px solid #e67e22;
            }
            .error {
                color: #e74c3c;
                font-weight: bold;
                padding: 10px;
                background: #ffeaea;
                border-radius: 4px;
                margin-bottom: 15px;
            }
        </style>
    </head>
    <body>
        <%@ include file="../WEB-INF/jspf/header.jspf" %>

        <main class="container">
            <h2>Course Evaluation Results</h2>
            
            <a href="<%=request.getContextPath()%>/lecturer/DashboardLecturer.jsp" class="btn-link">Home</a> &nbsp;

            <% if (errorMessage != null) { %>
            <div class="error"><%= errorMessage %></div>
            <% } %>

            <div class="evaluation-container">
                <div class="selection-form">
                    <form method="get" action="<%= request.getContextPath()%>/LecturerEvaluationServlet">
                        <label for="subjectId">Select Subject:</label>
                        <select name="subjectId" id="subjectId" required>
                            <option value="">-- Select Subject --</option>
                            <% if (lecturerSubjects != null) {
                                for (Subject subj : lecturerSubjects) { %>
                            <option value="<%= subj.getSubjectId() %>"
                                    <%= selectedSubject != null && selectedSubject.getSubjectId() == subj.getSubjectId() ? "selected" : "" %>>
                                <%= subj.getSubjectCode() != null ? HtmlUtil.escape(subj.getSubjectCode()) + " - " : "" %><%= HtmlUtil.escape(subj.getSubjectName()) %>
                            </option>
                            <% }
                            } %>
                        </select>

                        <input type="submit" value="View Evaluations" />
                    </form>
                </div>

                <% if (selectedSubject != null && summary != null) { %>
                <div class="summary-section">
                    <h3>Evaluation Summary for <%= selectedSubject.getSubjectCode() != null ? HtmlUtil.escape(selectedSubject.getSubjectCode()) + " - " : "" %><%= HtmlUtil.escape(selectedSubject.getSubjectName()) %></h3>
                    <p><strong>Format:</strong> Dynamic Evaluation System</p>

                    <div class="summary-stats">
                        <div class="stat-card">
                            <h4>Total Responses</h4>
                            <div class="stat-value"><%= summary.totalResponses %></div>
                        </div>
                    </div>

                    <% if (summary.questionStats != null && !summary.questionStats.isEmpty()) { %>
                    <h4>Rating Questions (Average)</h4>
                    <table class="question-table">
                        <thead>
                            <tr>
                                <th>Question</th>
                                <th>Average Rating</th>
                                <th>Responses</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% for (CourseEvaluationDynamicDAO.QuestionStat stat : summary.questionStats) { %>
                            <tr>
                                <td><%= HtmlUtil.escape(stat.questionText) %></td>
                                <td><%= String.format("%.2f", stat.avgRating) %></td>
                                <td><%= stat.responseCount %></td>
                            </tr>
                            <% } %>
                        </tbody>
                    </table>
                    <% } else { %>
                    <p>No rating questions have responses yet.</p>
                    <% } %>
                </div>

                <% if (textAnswers != null && !textAnswers.isEmpty()) { %>
                <div class="comments-section">
                    <h3>Text Comments</h3>
                    <% 
                        com.project.dao.EvaluationQuestionDAO questionDAO = new com.project.dao.EvaluationQuestionDAO();
                        for (Map.Entry<Integer, List<String>> entry : textAnswers.entrySet()) {
                            int questionId = entry.getKey();
                            List<String> answers = entry.getValue();
                            com.project.model.EvaluationQuestion question = questionDAO.getQuestionById(questionId);
                            String questionText = question != null ? question.getQuestionText() : "Question #" + questionId;
                    %>
                    <div class="comment-box">
                        <h4><%= HtmlUtil.escape(questionText) %></h4>
                        <% for (String answer : answers) { %>
                        <p style="margin: 10px 0; padding-left: 15px; border-left: 3px solid #e67e22;">
                            <%= HtmlUtil.escape(answer) %>
                        </p>
                        <% } %>
                    </div>
                    <% } %>
                </div>
                <% } else if (selectedSubject != null && summary.totalResponses > 0) { %>
                <div class="summary-section">
                    <p>No text comments available for this subject.</p>
                </div>
                <% } else if (selectedSubject != null) { %>
                <div class="summary-section">
                    <p>No evaluations found for this subject.</p>
                </div>
                <% } %>
                <% } else { %>
                <div class="summary-section">
                    <p>Please select a subject to view evaluation results.</p>
                </div>
                <% } %>
            </div>
        </main>

        <%@ include file="../WEB-INF/jspf/footer.jspf" %>
    </body>
</html>

