<%-- 
    Document   : evaluation_success
    Created on : Success page after submitting evaluation
    Author     : ZULMAJDI
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.project.model.User"%>
<%
    User user = (User) session.getAttribute("user");
    if (user == null || !"Student".equalsIgnoreCase(user.getRole())) {
        response.sendRedirect("../login.jsp");
        return;
    }
    
    request.setAttribute("headerTitle", "University Management System - Evaluation Submitted");
    
    String successMessage = (String) session.getAttribute("successMessage");
    session.removeAttribute("successMessage");
%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8" />
        <title>Evaluation Submitted</title>
        <link rel="icon" href="<%=request.getContextPath()%>/images/graduation.png" sizes="32x32" type="image/png" />
        <link rel="stylesheet" href="<%=request.getContextPath()%>/css/layout.css" />
        <link rel="stylesheet" href="<%=request.getContextPath()%>/css/common.css" />
        <style>
            .success-container {
                max-width: 600px;
                margin: 50px auto;
                padding: 40px;
                background: white;
                border-radius: 8px;
                box-shadow: 0 2px 8px rgba(0,0,0,0.1);
                text-align: center;
            }
            .success-icon {
                font-size: 64px;
                color: #27ae60;
                margin-bottom: 20px;
            }
            .success-message {
                color: #27ae60;
                font-size: 1.3rem;
                font-weight: bold;
                margin-bottom: 20px;
            }
        </style>
    </head>
    <body>
        <%@ include file="../WEB-INF/jspf/header.jspf" %>

        <main class="container">
            <div class="success-container">
                <div class="success-icon">✓</div>
                <div class="success-message">
                    <%= successMessage != null ? successMessage : "Thank you! Your evaluation has been submitted successfully." %>
                </div>
                <p>Your feedback is valuable and will help improve the course.</p>
                <div style="margin-top: 30px;">
                    <a href="DashboardStudent.jsp" class="btn-link">Return to Dashboard</a>
                </div>
            </div>
        </main>

        <%@ include file="../WEB-INF/jspf/footer.jspf" %>
    </body>
</html>

