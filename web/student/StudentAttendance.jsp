<%-- 
    Document   : StudentAttendance
    Created on : 18 Jun 2025, 5:50:38 pm
    Author     : NuNa
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="com.project.model.AttendanceSummary"%>
<%@page import="com.project.util.HtmlUtil"%>

<%
    List<AttendanceSummary> attendanceSummaries = (List<AttendanceSummary>) request.getAttribute("attendanceSummaries");

    request.setAttribute("headerTitle", "University Management System - My Attendance");
%>

<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8" />
        <title>Student Attendance</title>
        <link rel="icon" href="<%=request.getContextPath()%>/images/graduation.png" sizes="32x32" type="image/png" />
        <link rel="stylesheet" href="<%=request.getContextPath()%>/css/layout.css" />
        <link rel="stylesheet" href="<%=request.getContextPath()%>/css/common.css" />
        <style>
            .dashboard-container {
                display: flex;
                flex-wrap: wrap;
                gap: 1rem;
            }
            .widget {
                border: 1px solid #ccc;
                border-radius: 8px;
                padding: 1rem;
                width: 220px;
                box-shadow: 2px 2px 8px rgba(0,0,0,0.1);
                cursor: pointer;
                transition: box-shadow 0.3s ease;
            }
            .widget:hover {
                box-shadow: 4px 4px 12px rgba(0,0,0,0.2);
            }
            .widget h3 {
                margin-top: 0;
            }
            .widget p {
                margin: 0.3rem 0;
            }
            .widget a {
                display: inline-block;
                margin-top: 0.5rem;
                color: #007bff;
                text-decoration: none;
            }
            .widget a:hover {
                text-decoration: underline;
            }
        </style>
    </head>
    <body>
        <%@ include file="../WEB-INF/jspf/header.jspf" %>

        <main class="container">
            <h2>Your Subjects Attendance Summary</h2>

            <nav>
                <a href="<%=request.getContextPath()%>/student/DashboardStudent.jsp" class="btn-link" style="margin-bottom: 20px;">Home</a> &nbsp;
            </nav>

            <% String errorMessage = (String) request.getAttribute("errorMessage");
               if (errorMessage != null) { %>
            <div style="color: #e74c3c; font-weight: bold; padding: 10px; background: #ffeaea; border-radius: 4px; margin-bottom: 15px;">
                <%= errorMessage %>
            </div>
            <% } %>

            <div class="dashboard-container" aria-label="Subjects">
                <% if (attendanceSummaries != null && !attendanceSummaries.isEmpty()) {
                        for (AttendanceSummary summary : attendanceSummaries) {%>
                <div class="widget" tabindex="0" role="region" aria-labelledby="subject_<%=summary.getSubjectId()%>"
                     onclick="location.href = '<%=request.getContextPath()%>/AttendanceDetailServlet?subjectId=<%=summary.getSubjectId()%>'">
                    <h3 id="subject_<%=summary.getSubjectId()%>"><%= summary.getSubjectCode() != null ? HtmlUtil.escape(summary.getSubjectCode()) + " - " : "" %><%= HtmlUtil.escape(summary.getSubjectName())%></h3>
                    <p style="color: green; font-weight: bold;">Present: <strong><%= summary.getPresentCount()%></strong></p>
                    <p style="color: red; font-weight: bold;">Absent: <strong><%= summary.getAbsentCount()%></strong></p>
                    <% 
                       int totalSessions = summary.getPresentCount() + summary.getAbsentCount();
                       double attendancePercent = totalSessions > 0 ? (summary.getPresentCount() * 100.0 / totalSessions) : 0.0;
                       String percentColor = attendancePercent >= 80 ? "green" : (attendancePercent >= 60 ? "orange" : "red");
                    %>
                    <p style="color: <%= percentColor %>; font-weight: bold;">Attendance: <strong><%= String.format("%.1f", attendancePercent) %>%</strong></p>
                    <a href="<%=request.getContextPath()%>/AttendanceDetailServlet?subjectId=<%=summary.getSubjectId()%>">View Details</a>
                </div>
                <%  }
                } else { %>
                <p>You do not have any attendance records yet.</p>
                <% }%>
            </div>
        </main>

        <%@ include file="../WEB-INF/jspf/footer.jspf" %>
    </body>
</html>
