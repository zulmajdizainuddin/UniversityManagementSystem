<%-- 
    Admin Attendance Report - By Student
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.project.dao.AttendanceReportDAO"%>
<%@page import="com.project.model.Student"%>
<%@page import="java.util.List"%>
<%@page import="com.project.util.HtmlUtil"%>
<%
    com.project.model.User user = (com.project.model.User) session.getAttribute("user");
    if (user == null || !"Admin".equalsIgnoreCase(user.getRole())) {
        response.sendRedirect("../login.jsp");
        return;
    }

    request.setAttribute("headerTitle", "University Management System - Attendance Report By Student");

    List<Student> students = (List<Student>) request.getAttribute("students");
    Integer selectedStudentId = (Integer) request.getAttribute("selectedStudentId");
    Double threshold = (Double) request.getAttribute("threshold");
    if (threshold == null) {
        threshold = 80.0;
    }

    AttendanceReportDAO.StudentReportSummary summary =
            (AttendanceReportDAO.StudentReportSummary) request.getAttribute("summary");
%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8" />
        <title>Attendance Report - By Student</title>
        <link rel="icon" href="<%=request.getContextPath()%>/images/graduation.png" sizes="32x32" type="image/png" />
        <link rel="stylesheet" href="<%=request.getContextPath()%>/css/layout.css" />
        <link rel="stylesheet" href="<%=request.getContextPath()%>/css/common.css" />
    </head>
    <body>
        <%@ include file="../WEB-INF/jspf/header.jspf" %>

        <main class="container">
            <h2>Attendance Analysis Report - By Student</h2>

            <a href="DashboardAdmin.jsp" class="btn-link">Home</a>

            <section style="margin-top: 20px;">
                <form method="get" action="<%=request.getContextPath()%>/AdminAttendanceReportServlet">
                    <input type="hidden" name="action" value="studentView" />

                    <label for="studentId">Student:</label>
                    <select id="studentId" name="studentId" required>
                        <option value="">-- Select Student --</option>
                        <% if (students != null) {
                               for (Student s : students) { %>
                        <option value="<%= s.getUserId() %>"
                            <%= (selectedStudentId != null && selectedStudentId.equals(s.getUserId())) ? "selected" : "" %>>
                            <%= HtmlUtil.escape(s.getStudentNumber()) %> - <%= HtmlUtil.escape(s.getName()) %>
                        </option>
                        <%   }
                           } %>
                    </select>

                    <label for="threshold">Threshold (%):</label>
                    <input type="number" id="threshold" name="threshold" min="0" max="100" step="1"
                           value="<%= String.format(\"%.0f\", threshold) %>" />

                    <input type="submit" value="Generate Report" />
                </form>
            </section>

            <% if (summary != null && summary.studentId != 0) { %>
            <section style="margin-top: 25px;">
                <h3>Summary for <%= HtmlUtil.escape(summary.studentNumber) %> - <%= HtmlUtil.escape(summary.studentName) %></h3>
                <p><strong>Total Present:</strong> <%= summary.totalPresentAll %></p>
                <p><strong>Total Absent:</strong> <%= summary.totalAbsentAll %></p>
                <p><strong>Overall Attendance:</strong> <%= String.format("%.2f", summary.overallPercent) %>%</p>

                <div style="margin-top: 15px;">
                    <a href="<%=request.getContextPath()%>/AdminAttendanceReportServlet?action=exportCsv&type=student&studentId=<%=summary.studentId%>" class="btn-link">Export CSV</a>
                </div>
            </section>

            <section style="margin-top: 25px;">
                <h3>Attendance by Subject (Flagged &lt; <%= String.format(\"%.0f\", threshold) %>%)</h3>
                <table class="data-table" aria-label="Student attendance by subject">
                    <thead>
                        <tr>
                            <th>Subject Code</th>
                            <th>Subject Name</th>
                            <th>Present</th>
                            <th>Absent</th>
                            <th>Attendance %</th>
                            <th>Status</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% if (summary.subjectRows != null && !summary.subjectRows.isEmpty()) {
                               boolean atRisk = false;
                               for (AttendanceReportDAO.StudentSubjectRow row : summary.subjectRows) {
                                   String status = row.attendancePercent < threshold ? "At Risk" : "";
                                   if (row.attendancePercent < threshold) {
                                       atRisk = true;
                                   }
                        %>
                        <tr>
                            <td><%= HtmlUtil.escape(row.subjectCode) %></td>
                            <td><%= HtmlUtil.escape(row.subjectName) %></td>
                            <td><%= row.presentCount %></td>
                            <td><%= row.absentCount %></td>
                            <td><%= String.format("%.2f", row.attendancePercent) %></td>
                            <td style="color: red; font-weight: bold;"><%= status %></td>
                        </tr>
                        <%       }
                           } else if (selectedStudentId != null) { %>
                        <tr>
                            <td colspan="6">No attendance records found for this student.</td>
                        </tr>
                        <% } %>
                    </tbody>
                </table>
            </section>
            <% } else if (selectedStudentId != null) { %>
            <section style="margin-top: 25px;">
                <p>No attendance records found for the selected student.</p>
            </section>
            <% } %>
        </main>

        <%@ include file="../WEB-INF/jspf/footer.jspf" %>
    </body>
</html>


