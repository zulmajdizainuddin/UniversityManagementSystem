<%-- 
    Document   : AttendanceDetail
    Created on : 18 Jun 2025, 6:35:06 pm
    Author     : NuNa
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="com.project.model.Attendance"%>
<%@page import="com.project.util.HtmlUtil"%>

<%
    List<Attendance> attendanceList = (List<Attendance>) request.getAttribute("attendanceList");

    request.setAttribute("headerTitle", "University Management System - My Attendance");
%>

<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8" />
        <title>Student Attendance - Details</title>
        <link rel="icon" href="<%=request.getContextPath()%>/images/graduation.png" sizes="32x32" type="image/png" />
        <link rel="stylesheet" href="<%=request.getContextPath()%>/css/layout.css" />
        <link rel="stylesheet" href="<%=request.getContextPath()%>/css/common.css" />
    </head>
    <body>
        <%@ include file="../WEB-INF/jspf/header.jspf" %>

        <main class="container">
            <h2>Attendance Details</h2>

            <table class="data-table" aria-label="Attendance Details">
                <thead>
                    <tr>
                        <th>Date</th>
                        <th>Class</th>
                        <th>Status</th>
                    </tr>
                </thead>
                <tbody>
                    <% if (attendanceList != null && !attendanceList.isEmpty()) {
                            for (Attendance att : attendanceList) {%>
                    <tr>
                        <td><%= HtmlUtil.escape(att.getDate())%></td>
                        <td><%= HtmlUtil.escape(att.getClassName())%></td>
                        <td style="color: <%= "present".equalsIgnoreCase(att.getStatus() != null ? att.getStatus().trim() : "") ? "green" : "red"%>; font-weight: bold;">
                            <%= HtmlUtil.escape(att.getStatus())%>
                        </td>
                    </tr>
                    <%  }
                    } else { %>
                    <tr><td colspan="3">No attendance records found.</td></tr>
                    <% }%>
                </tbody>
            </table>

            <a href="<%=request.getContextPath()%>/AttendanceStudentServlet">Back to Subjects</a>
        </main>

        <%@ include file="../WEB-INF/jspf/footer.jspf" %>
    </body>
</html>
