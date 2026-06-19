<%-- 
    Document   : DashboardLecturer
    Created on : Jun 12, 2025, 5:40:40 AM
    Author     : ZULMAJDI
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.project.model.User"%>
<%@page import="com.project.dao.*"%>
<%@page import="com.project.util.HtmlUtil"%>

<%
    User user = (User) session.getAttribute("user");
    if (user == null) {
        response.sendRedirect("../login.jsp");
        return;
    }
    request.setAttribute("headerTitle", "University Management System - Lecturer Dashboard");

    SubjectDAO subjectDAO = new SubjectDAO();

    int subjectsCount = subjectDAO.countSubjectsByLecturer(user.getUserId());
%>

<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8" />
        <title>Lecturer Dashboard</title>
        <link rel="icon" href="<%=request.getContextPath()%>/images/graduation.png" sizes="32x32" type="image/png" />
        <link rel="stylesheet" href="<%=request.getContextPath()%>/css/layout.css" />
        <link rel="stylesheet" href="<%=request.getContextPath()%>/css/common.css" />
        <link rel="stylesheet" href="<%=request.getContextPath()%>/css/dashboard.css" />
    </head>
    <body>

        <%@ include file="../WEB-INF/jspf/header.jspf" %>

        <main class="container">
            <h2>Welcome, <%= HtmlUtil.escape(user.getName())%>!</h2>

            <div class="dashboard-container" aria-label="Summary statistics">
                <div class="widget" tabindex="0" role="region" aria-labelledby="subjectsCount">
                    <h3 id="subjectsCount"><%= subjectsCount%></h3>
                    <p>Total Teaching Subjects</p>
                </div>
                <!-- You can add more cards here similarly -->
            </div>

            <nav>
                <ul class="dashboard-menu">
                    <li><a href="<%= request.getContextPath()%>/AttendanceServlet?action=viewAttendance">Manage Attendance</a></li>
                    <li><a href="<%= request.getContextPath()%>/GradeServlet?action=manageGrades">Manage Grades</a></li>
                    <li><a href="<%= request.getContextPath()%>/LecturerEvaluationServlet">View Course Evaluations</a></li>
                    <li><a href="<%= request.getContextPath()%>/LogoutServlet">Logout</a></li>
                </ul>
            </nav>
        </main>

        <%@ include file="../WEB-INF/jspf/footer.jspf" %>
    </body>
</html>
