<%-- 
    Document   : student_course_evaluation
    Created on : Course Evaluation Form for Students
    Author     : ZULMAJDI
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.project.model.Subject"%>
<%@page import="com.project.model.User"%>
<%@page import="com.project.model.EvaluationQuestion"%>
<%@page import="com.project.dao.UserDAO"%>
<%@page import="com.project.dao.CoursesDAO"%>
<%@page import="java.util.List"%>
<%@page import="com.project.util.HtmlUtil"%>
<%
    User user = (User) session.getAttribute("user");
    if (user == null || !"Student".equalsIgnoreCase(user.getRole())) {
        response.sendRedirect("../login.jsp");
        return;
    }
    
    Subject subject = (Subject) request.getAttribute("subject");
    Integer lecturerId = (Integer) request.getAttribute("lecturerId");
    Boolean alreadySubmitted = (Boolean) request.getAttribute("alreadySubmitted");
    List<EvaluationQuestion> questions = (List<EvaluationQuestion>) request.getAttribute("questions");
    
    if (subject == null || lecturerId == null) {
        response.sendRedirect("DashboardStudent.jsp");
        return;
    }
    
    // Get lecturer name
    String lecturerName = "N/A";
    try {
        UserDAO userDAO = new UserDAO();
        User lecturer = userDAO.getUserById(lecturerId);
        if (lecturer != null) {
            lecturerName = lecturer.getName();
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    
    // Get course name
    String courseName = "N/A";
    try {
        CoursesDAO courseDAO = new CoursesDAO();
        com.project.model.Course course = courseDAO.getCourseById(subject.getCourseId());
        if (course != null) {
            courseName = course.getCourseName();
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    
    request.setAttribute("headerTitle", "University Management System - Course Evaluation");
    
    String errorMessage = (String) session.getAttribute("errorMessage");
    session.removeAttribute("errorMessage");
%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8" />
        <title>Course Evaluation</title>
        <link rel="icon" href="<%=request.getContextPath()%>/images/graduation.png" sizes="32x32" type="image/png" />
        <link rel="stylesheet" href="<%=request.getContextPath()%>/css/layout.css" />
        <link rel="stylesheet" href="<%=request.getContextPath()%>/css/common.css" />
        <style>
            .evaluation-form {
                max-width: 800px;
                margin: 20px auto;
                padding: 20px;
                background: white;
                border-radius: 8px;
                box-shadow: 0 2px 8px rgba(0,0,0,0.1);
            }
            .info-section {
                background: #f8f9fa;
                padding: 15px;
                border-radius: 6px;
                margin-bottom: 20px;
            }
            .info-section p {
                margin: 5px 0;
            }
            .question-group {
                margin: 20px 0;
                padding: 15px;
                border: 1px solid #ddd;
                border-radius: 6px;
                background: #fafafa;
            }
            .question-group label {
                display: block;
                font-weight: bold;
                margin-bottom: 10px;
                color: #333;
            }
            .rating-options {
                display: flex;
                gap: 15px;
                flex-wrap: wrap;
            }
            .rating-option {
                display: flex;
                align-items: center;
                gap: 5px;
            }
            .rating-option input[type="radio"] {
                margin: 0;
            }
            .comments-section {
                margin: 20px 0;
            }
            .comments-section textarea {
                width: 100%;
                min-height: 100px;
                padding: 10px;
                border: 1px solid #ddd;
                border-radius: 4px;
                font-family: inherit;
                resize: vertical;
            }
            .evaluation-textarea {
                width: 100%;
                min-height: 120px;
                padding: 12px;
                border: 1px solid #ddd;
                border-radius: 4px;
                font-family: inherit;
                font-size: 1rem;
                line-height: 1.5;
                resize: vertical;
                box-sizing: border-box;
            }
            .evaluation-textarea:focus {
                outline: none;
                border-color: #e67e22;
                box-shadow: 0 0 0 2px rgba(230, 126, 34, 0.1);
            }
            .error {
                color: #e74c3c;
                font-weight: bold;
                margin-bottom: 15px;
                padding: 10px;
                background: #ffeaea;
                border-radius: 4px;
            }
            .already-submitted {
                color: #27ae60;
                font-weight: bold;
                padding: 15px;
                background: #eafaf1;
                border-radius: 6px;
                margin: 20px 0;
            }
        </style>
    </head>
    <body>
        <%@ include file="../WEB-INF/jspf/header.jspf" %>

        <main class="container">
            <h2>Course Evaluation</h2>
            
            <a href="<%=request.getContextPath()%>/student/DashboardStudent.jsp" class="btn-link">Home</a> &nbsp;

            <% if (errorMessage != null) { %>
            <div class="error"><%= errorMessage %></div>
            <% } %>

            <% if (alreadySubmitted != null && alreadySubmitted) { %>
            <div class="already-submitted">
                <p>You have already submitted an evaluation for this subject.</p>
                <p>Thank you for your feedback!</p>
            </div>
            <% } else { %>

            <div class="evaluation-form">
                <div class="info-section">
                    <h3>Course Information</h3>
                    <p><strong>Subject Code:</strong> <%= subject.getSubjectCode() != null ? HtmlUtil.escape(subject.getSubjectCode()) : "N/A" %></p>
                    <p><strong>Subject Name:</strong> <%= HtmlUtil.escape(subject.getSubjectName()) %></p>
                    <p><strong>Course:</strong> <%= HtmlUtil.escape(courseName) %></p>
                    <p><strong>Lecturer:</strong> <%= HtmlUtil.escape(lecturerName) %></p>
                </div>

                <form action="<%= request.getContextPath()%>/StudentCourseEvaluationServlet" method="post" id="evaluationForm">
                    <input type="hidden" name="subjectId" value="<%= subject.getSubjectId() %>" />
                    <input type="hidden" name="lecturerId" value="<%= lecturerId %>" />
                    <input type="hidden" name="csrfToken" value="<%= session.getAttribute("csrfToken") %>" />

                    <% if (questions != null && !questions.isEmpty()) { %>
                        <% 
                            boolean hasRatingQuestions = false;
                            for (EvaluationQuestion q : questions) {
                                if ("RATING".equals(q.getQuestionType())) {
                                    hasRatingQuestions = true;
                                    break;
                                }
                            }
                        %>
                        <% if (hasRatingQuestions) { %>
                        <h3>Please rate the following (1 = Strongly Disagree, 5 = Strongly Agree):</h3>
                        <% } %>
                        
                        <% for (EvaluationQuestion question : questions) { %>
                        <div class="question-group">
                            <label>
                                <%= HtmlUtil.escape(question.getQuestionText()) %>
                                <% if (question.isRequired()) { %>
                                <span style="color: red;">*</span>
                                <% } %>
                            </label>
                            
                            <% if ("RATING".equals(question.getQuestionType())) { %>
                            <div class="rating-options">
                                <div class="rating-option">
                                    <input type="radio" name="question_<%= question.getQuestionId() %>" value="1" 
                                           <%= question.isRequired() ? "required" : "" %> /> 1
                                </div>
                                <div class="rating-option">
                                    <input type="radio" name="question_<%= question.getQuestionId() %>" value="2" /> 2
                                </div>
                                <div class="rating-option">
                                    <input type="radio" name="question_<%= question.getQuestionId() %>" value="3" /> 3
                                </div>
                                <div class="rating-option">
                                    <input type="radio" name="question_<%= question.getQuestionId() %>" value="4" /> 4
                                </div>
                                <div class="rating-option">
                                    <input type="radio" name="question_<%= question.getQuestionId() %>" value="5" /> 5
                                </div>
                            </div>
                            <% } else if ("TEXT".equals(question.getQuestionType())) { %>
                            <textarea name="question_<%= question.getQuestionId() %>" 
                                      class="evaluation-textarea"
                                      rows="6" 
                                      placeholder="Enter your response here..."
                                      <%= question.isRequired() ? "required" : "" %>></textarea>
                            <% } %>
                        </div>
                        <% } %>
                    <% } else { %>
                    <p style="color: #e74c3c; font-weight: bold;">No evaluation questions are currently available. Please contact the administrator.</p>
                    <% } %>

                    <div style="margin-top: 30px; text-align: center;">
                        <input type="submit" value="Submit Evaluation" style="padding: 12px 30px; font-size: 1.1rem;" />
                        <a href="<%=request.getContextPath()%>/student/DashboardStudent.jsp" class="btn-link" style="margin-left: 15px;">Cancel</a>
                        
                    </div>
                </form>
            </div>
            <% } %>
        </main>

        <%@ include file="../WEB-INF/jspf/footer.jspf" %>
    </body>
</html>

