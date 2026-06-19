<%-- 
    Document   : ManageAttendance
    Created on : 18 Jun 2025, 12:33:18?pm
    Author     : NuNa
--%>

<%@page import="java.util.List"%>
<%@page import="com.project.model.Attendance"%>
<%@page import="com.project.model.Student"%>
<%@page import="com.project.model.Class"%>
<%@page import="com.project.model.Subject"%>
<%@page import="com.project.util.HtmlUtil"%>

<%
    if (session.getAttribute("user") == null) {
        response.sendRedirect("../login.jsp");
        return;
    }
    request.setAttribute("headerTitle", "University Management System - Manage Student Attendance");

    String action = (String) request.getAttribute("action");
    if (action == null) {
        action = "viewAttendance"; // default action
    }

    List<Subject> subjects = (List<Subject>) request.getAttribute("subjects");
    List<Class> classes = (List<Class>) request.getAttribute("classes");
    List<Student> students = (List<Student>) request.getAttribute("students");
    List<Attendance> attendanceList = (List<Attendance>) request.getAttribute("attendanceList");

    Integer selectedSubjectId = (Integer) request.getAttribute("selectedSubjectId");
    Integer selectedClassId = (Integer) request.getAttribute("selectedClassId");
    String selectedDate = (String) request.getAttribute("selectedDate");

    String errorMessage = (String) session.getAttribute("errorMessage");
    String successMessage = (String) session.getAttribute("successMessage");
    session.removeAttribute("errorMessage");
    session.removeAttribute("successMessage");
%>

<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8" />
        <title>Attendance Management</title>
        <link rel="icon" href="<%=request.getContextPath()%>/images/graduation.png" sizes="32x32" type="image/png" />
        <link rel="stylesheet" href="<%=request.getContextPath()%>/css/layout.css" />
        <link rel="stylesheet" href="<%=request.getContextPath()%>/css/common.css" />
    </head>
    <body>
        <%@ include file="../WEB-INF/jspf/header.jspf" %>

        <main class="container">
            <h2>Attendance Management</h2>

            <nav>
                <a href="<%=request.getContextPath()%>/lecturer/DashboardLecturer.jsp" class="btn-link">Home</a> &nbsp;
                <a href="<%=request.getContextPath()%>/AttendanceServlet?action=viewAttendance" class="btn-link">View Attendance</a> &nbsp;
                <a href="<%=request.getContextPath()%>/AttendanceServlet?action=takeAttendance" class="btn-link">Take Attendance</a>
            </nav>

            <% if (errorMessage != null) {%>
            <p class="error"><%= errorMessage%></p>
            <% } %>
            <% if (successMessage != null) {%>
            <p class="success"><%= successMessage%></p>
            <% } %>

            <% if ("viewAttendance".equals(action)) {%>
            <h3>View Attendance Records</h3>
            <form method="get" action="<%=request.getContextPath()%>/AttendanceServlet">
                <input type="hidden" name="action" value="viewAttendance" />

                <label for="subjectSelect">Subject:</label>
                <select id="subjectSelect" name="subjectId" onchange="this.form.submit()">
                    <option value="">-- Select Subject --</option>
                    <% for (Subject s : subjects) {%>
                    <option value="<%= s.getSubjectId()%>" <%= (selectedSubjectId != null && selectedSubjectId.equals(s.getSubjectId())) ? "selected" : ""%>>
                        <%= s.getSubjectName()%>
                    </option>
                    <% } %>
                </select>

                <% if (classes != null) { %>
                <label for="classSelect">Class:</label>
                <select id="classSelect" name="classId" onchange="this.form.submit()">
                    <option value="">-- Select Class --</option>
                    <% for (Class c : classes) {%>
                    <option value="<%= c.getClassId()%>" <%= (selectedClassId != null && selectedClassId.equals(c.getClassId())) ? "selected" : ""%>>
                        <%= c.getClassName()%>
                    </option>
                    <% } %>
                </select>
                <% } %>
            </form>

            <% if (attendanceList != null && !attendanceList.isEmpty()) {%>
            <form method="post" action="<%=request.getContextPath()%>/AttendanceServlet?action=updateAttendanceBatch">
                <input type="hidden" name="subjectId" value="<%= selectedSubjectId%>" />
                <input type="hidden" name="classId" value="<%= selectedClassId%>" />
                <input type="hidden" name="csrfToken" value="<%= session.getAttribute("csrfToken") %>" />
                <table class="data-table" border="1" cellpadding="5" cellspacing="0">
                    <thead>
                        <tr>
                            <th>Student Number</th>
                            <th>Student Name</th>
                            <th>Status</th>
                            <th>Date</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% for (Attendance att : attendanceList) {
                                Student student = null;
                                for (Student s : students) {
                                    if (s.getUserId() == att.getStudentId()) {
                                        student = s;
                                        break;
                                    }
                                }
                                if (student != null) {
                        %>
                        <tr>
                            <td><%= HtmlUtil.escape(student.getStudentNumber())%></td>
                            <td><%= HtmlUtil.escape(student.getName())%></td>
                            <td>
                                <select name="status_<%= att.getAttendanceId()%>">
                                    <option value="Present" <%= "Present".equals(att.getStatus()) ? "selected" : ""%>>Present</option>
                                    <option value="Absent" <%= "Absent".equals(att.getStatus()) ? "selected" : ""%>>Absent</option>
                                </select>
                                <input type="hidden" name="attendanceIds" value="<%= att.getAttendanceId()%>" />
                            </td>
                            <td><%= att.getDate()%></td>
                        </tr>
                        <%      }
                                } %>
                    </tbody>
                </table>
                <br/>
                <input type="submit" value="Update Attendance" />
            </form>
            <% } else if (selectedClassId != null) { %>
            <p>No attendance records found for this class.</p>
            <% } %>
            <% } else if ("takeAttendance".equals(action)) {%>
            <h3>Take Attendance</h3>
            <form method="get" action="<%=request.getContextPath()%>/AttendanceServlet">
                <input type="hidden" name="action" value="takeAttendance" />

                <label for="subjectSelect">Subject:</label>
                <select id="subjectSelect" name="subjectId" onchange="this.form.submit()">
                    <option value="">-- Select Subject --</option>
                    <% for (Subject s : subjects) {%>
                    <option value="<%= s.getSubjectId()%>" <%= (selectedSubjectId != null && selectedSubjectId.equals(s.getSubjectId())) ? "selected" : ""%>>
                        <%= s.getSubjectName()%>
                    </option>
                    <% } %>
                </select>

                <% if (classes != null) { %>
                <label for="classSelect">Class:</label>
                <select id="classSelect" name="classId" onchange="this.form.submit()">
                    <option value="">-- Select Class --</option>
                    <% for (Class c : classes) {%>
                    <option value="<%= c.getClassId()%>" <%= (selectedClassId != null && selectedClassId.equals(c.getClassId())) ? "selected" : ""%>>
                        <%= c.getClassName()%>
                    </option>
                    <% } %>
                </select>
                <% } %>

                <% if (selectedClassId != null) {%>
                <label for="date">Date:</label>
                <input type="date" id="date" name="date" value="<%= (selectedDate != null) ? selectedDate : ""%>" onchange="this.form.submit()" />
                <% } %>
            </form>

            <% if (students != null && !students.isEmpty() && selectedDate != null && !selectedDate.isEmpty()) {%>
            <form method="post" action="<%=request.getContextPath()%>/AttendanceServlet?action=saveAttendance">
                <input type="hidden" name="classId" value="<%= selectedClassId%>" />
                <input type="hidden" name="date" value="<%= selectedDate%>" />
                <input type="hidden" name="csrfToken" value="<%= session.getAttribute("csrfToken") %>" />

                <table class="data-table" border="1" cellpadding="5" cellspacing="0">
                    <thead>
                        <tr>
                            <th>Student Number</th>
                            <th>Student Name</th>
                            <th>Status</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% for (Student student : students) {
                                String existingStatus = null;
                                if (attendanceList != null) {
                                    for (Attendance att : attendanceList) {
                                        if (att.getStudentId() == student.getUserId()) {
                                            existingStatus = att.getStatus();
                                            break;
                                        }
                                    }
                                }
                        %>
                        <tr>
                            <td><%= HtmlUtil.escape(student.getStudentNumber())%></td>
                            <td><%= HtmlUtil.escape(student.getName())%></td>
                            <td>
                                <select name="status_<%= student.getUserId()%>" required>
                                    <option value="Present" <%= "Present".equals(existingStatus) ? "selected" : ""%>>Present</option>
                                    <option value="Absent" <%= "Absent".equals(existingStatus) ? "selected" : ""%>>Absent</option>
                                </select>
                                <input type="hidden" name="studentIds" value="<%= student.getUserId()%>" />
                            </td>
                        </tr>
                        <% } %>
                    </tbody>
                </table>
                <br/>
                <input type="submit" value="Save Attendance" />
            </form>
            <% } %>
            <% }%>

        </main>

        <%@ include file="../WEB-INF/jspf/footer.jspf" %>
    </body>
</html>
