<%-- 
    Admin Attendance Report - By Subject
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.project.dao.AttendanceReportDAO"%>
<%@page import="com.project.model.Subject"%>
<%@page import="java.util.List"%>
<%@page import="com.project.util.HtmlUtil"%>
<%
    com.project.model.User user = (com.project.model.User) session.getAttribute("user");
    if (user == null || !"Admin".equalsIgnoreCase(user.getRole())) {
        response.sendRedirect("../login.jsp");
        return;
    }

    request.setAttribute("headerTitle", "University Management System - Attendance Report By Subject");

    List<Subject> subjects = (List<Subject>) request.getAttribute("subjects");
    Integer selectedSubjectId = (Integer) request.getAttribute("selectedSubjectId");
    Double threshold = (Double) request.getAttribute("threshold");
    if (threshold == null) {
        threshold = 80.0;
    }

    AttendanceReportDAO.SubjectReportSummary summary =
            (AttendanceReportDAO.SubjectReportSummary) request.getAttribute("summary");
%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8" />
        <title>Attendance Report - By Subject</title>
        <link rel="icon" href="<%=request.getContextPath()%>/images/graduation.png" sizes="32x32" type="image/png" />
        <link rel="stylesheet" href="<%=request.getContextPath()%>/css/layout.css" />
        <link rel="stylesheet" href="<%=request.getContextPath()%>/css/common.css" />
    </head>
    <body>
        <%@ include file="../WEB-INF/jspf/header.jspf" %>

        <main class="container">
            <h2>Attendance Analysis Report - By Subject</h2>

            
            <a href="<%=request.getContextPath()%>/admin/DashboardAdmin.jsp" class="btn-link">Home</a> &nbsp;

            <section style="margin-top: 20px;">
                <form method="get" action="<%=request.getContextPath()%>/AdminAttendanceReportServlet">
                    <input type="hidden" name="action" value="subjectView" />

                    <label for="subjectId">Subject:</label>
                    <select id="subjectId" name="subjectId" required>
                        <option value="">-- Select Subject --</option>
                        <% if (subjects != null) {
                               for (Subject s : subjects) { %>
                        <option value="<%= s.getSubjectId() %>"
                            <%= (selectedSubjectId != null && selectedSubjectId.equals(s.getSubjectId())) ? "selected" : "" %>>
                            <%= (s.getSubjectCode() != null ? HtmlUtil.escape(s.getSubjectCode()) + " - " : "") + HtmlUtil.escape(s.getSubjectName()) %>
                        </option>
                        <%   }
                           } %>
                    </select>

                    <label for="threshold">Threshold (%):</label>
                    <input type="number" id="threshold" name="threshold" min="0" max="100" step="1"
                           value="<%= String.format("%.0f", threshold) %>" />

                    <input type="submit" value="Generate Report" />
                </form>
            </section>

            <% if (summary != null && summary.subjectId != 0) { %>
            <section style="margin-top: 25px;">
                <h3>Summary for <%= (summary.subjectCode != null ? HtmlUtil.escape(summary.subjectCode) + " - " : "") + HtmlUtil.escape(summary.subjectName) %></h3>
                <p><strong>Total Sessions:</strong> <%= summary.totalSessions %></p>
                <p><strong>Total Present:</strong> <%= summary.totalPresent %></p>
                <p><strong>Total Absent:</strong> <%= summary.totalAbsent %></p>
                <p><strong>Overall Attendance:</strong> <%= String.format("%.2f", summary.overallPercent) %>%</p>

                <div style="margin-top: 15px;">
                    <a href="<%=request.getContextPath()%>/AdminAttendanceReportServlet?action=exportCsv&type=subject&subjectId=<%=summary.subjectId%>" class="btn-link">Export CSV</a>
                </div>
            </section>

            <section style="margin-top: 25px;">
                <h3>Students (Flagged &lt; <%= String.format("%.0f", threshold) %>%)</h3>
                <table class="data-table" aria-label="Students with attendance">
                    <thead>
                        <tr>
                            <th>Student Number</th>
                            <th>Name</th>
                            <th>Present</th>
                            <th>Absent</th>
                            <th>Attendance %</th>
                            <th>Status</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% if (summary.studentRows != null && !summary.studentRows.isEmpty()) {
                               for (AttendanceReportDAO.SubjectReportRow row : summary.studentRows) {
                                   String status = row.attendancePercent < threshold ? "At Risk" : "";
                        %>
                        <tr>
                            <td><%= HtmlUtil.escape(row.studentNumber) %></td>
                            <td><%= HtmlUtil.escape(row.studentName) %></td>
                            <td><%= row.presentCount %></td>
                            <td><%= row.absentCount %></td>
                            <td><%= String.format("%.2f", row.attendancePercent) %></td>
                            <td style="color: red; font-weight: bold;"><%= status %></td>
                        </tr>
                        <%       }
                           } else { %>
                        <tr>
                            <td colspan="6">No attendance records found for this subject.</td>
                        </tr>
                        <% } %>
                    </tbody>
                </table>
            </section>
            <% } else if (selectedSubjectId != null) { %>
            <section style="margin-top: 25px;">
                <p>No attendance records found for the selected subject.</p>
            </section>
            <% } %>
        </main>

        <%@ include file="../WEB-INF/jspf/footer.jspf" %>
    </body>
</html>


