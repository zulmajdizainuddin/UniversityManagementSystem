<%-- 
    Document   : StudentGrades
    Created on : 18 Jun 2025, 7:18:19 pm
    Author     : NuNa
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="com.project.model.Grade"%>
<%@page import="com.project.util.HtmlUtil"%>

<%
    List<Grade> grades = (List<Grade>) request.getAttribute("grades");
    
    request.setAttribute("headerTitle", "University Management System - My Grades");
%>

<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8" />
        <title>My Grades</title>
        <link rel="icon" href="<%=request.getContextPath()%>/images/graduation.png" sizes="32x32" type="image/png" />
        <link rel="stylesheet" href="<%=request.getContextPath()%>/css/layout.css" />
        <link rel="stylesheet" href="<%=request.getContextPath()%>/css/common.css" />
        <style>
            .grade-score {
                font-weight: bold;
                font-size: 1.1rem;
            }
            .grade-high {
                color: green;
            }
            .grade-low {
                color: red;
            }
        </style>
    </head>
    <body>
        <%@ include file="../WEB-INF/jspf/header.jspf" %>

        <main class="container">
            <h2>Your Grades</h2>

            <nav>
                <a href="<%=request.getContextPath()%>/student/DashboardStudent.jsp" class="btn-link" style="margin-bottom: 20px;">Home</a> &nbsp;
            </nav>
            
            <% String errorMessage = (String) request.getAttribute("errorMessage");
               if (errorMessage != null) { %>
            <div style="color: #e74c3c; font-weight: bold; padding: 10px; background: #ffeaea; border-radius: 4px; margin-bottom: 15px;">
                <%= errorMessage %>
            </div>
            <% } %>

            <table class="data-table" aria-label="Grades">
                <thead>
                    <tr>
                        <th>Subject Code</th>
                        <th>Subject Name</th>
                        <th>Score</th>
                    </tr>
                </thead>
                <tbody>
                    <% if (grades != null && !grades.isEmpty()) {
                            for (Grade grade : grades) {
                                String gradeClass = grade.getScore() >= 50 ? "grade-high" : "grade-low"; // pass mark 50
%>
                    <tr>
                        <td><%= grade.getSubjectCode() != null ? HtmlUtil.escape(grade.getSubjectCode()) : "N/A"%></td>
                        <td><%= HtmlUtil.escape(grade.getSubjectName())%></td>
                        <td class="grade-score <%= gradeClass%>"><%= String.format("%.2f", grade.getScore())%></td>
                    </tr>
                    <%  }
                } else { %>
                    <tr><td colspan="3">No grades available.</td></tr>
                    <% }%>
                </tbody>
            </table>

            
        </main>

        <%@ include file="../WEB-INF/jspf/footer.jspf" %>
    </body>
</html>
