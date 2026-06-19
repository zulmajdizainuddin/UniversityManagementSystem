<%-- 
    Document   : ManageLecturerSubjects
    Created on : 17 Jun 2025, 14:32:12
    Author     : nabil
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.project.dao.StaffDAO"%>
<%@page import="com.project.dao.SubjectDAO"%>
<%@page import="com.project.dao.LecturerSubjectDAO"%>
<%@page import="com.project.model.Staff"%>
<%@page import="com.project.model.Subject"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.project.util.HtmlUtil"%>

<%
    // Redirect to login if user session is missing
    if (session.getAttribute("user") == null) {
        response.sendRedirect("../login.jsp");
        return;
    }

    request.setAttribute("headerTitle", "University Management System - Assign Lecturers To Subjects");

    // Instantiate DAOs
    StaffDAO staffDAO = new StaffDAO();
    SubjectDAO subjectDAO = new SubjectDAO();
    LecturerSubjectDAO lsDAO = new LecturerSubjectDAO();

    // Fetch all lecturers and subjects
    List<Staff> lecturers = staffDAO.getAllLecturers();
    List<Subject> subjects = subjectDAO.getAllSubjects();

    // Get selected lecturer ID from request parameter
    String lecturerIdParam = request.getParameter("lecturerId");
    Integer selectedLecturerId = null;
    List<Integer> assignedSubjectIds = new ArrayList<>();

    if (lecturerIdParam != null && !lecturerIdParam.isEmpty()) {
        selectedLecturerId = Integer.parseInt(lecturerIdParam);
        assignedSubjectIds = lsDAO.getSubjectIdsByLecturer(selectedLecturerId);
    }

    // Retrieve and clear session messages
    String errorMessage = (String) session.getAttribute("errorMessage");
    String successMessage = (String) session.getAttribute("successMessage");
    session.removeAttribute("errorMessage");
    session.removeAttribute("successMessage");

    // Null safety checks
    if (lecturers == null) {
        lecturers = new ArrayList<>();
    }
    if (subjects == null) {
        subjects = new ArrayList<>();
    }
    if (assignedSubjectIds == null)
        assignedSubjectIds = new ArrayList<>();
%>

<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8" />
        <title>Manage Lecturer - Subject Assignments</title>
        <link rel="stylesheet" href="<%=request.getContextPath()%>/css/layout.css" />
        <link rel="stylesheet" href="<%=request.getContextPath()%>/css/common.css" />
    </head>
    <body>
        <%@ include file="../WEB-INF/jspf/header.jspf" %>

        <main class="container">
            <h2>Assign Lecturers to Subjects</h2>

            <a href="DashboardAdmin.jsp" class="btn-link" style="margin-bottom: 10px;">Home</a>

            <% if (errorMessage != null) {%>
            <p class="error"><%= errorMessage%></p>
            <% } else if (successMessage != null) {%>
            <p class="success"><%= successMessage%></p>
            <% } %>

            <form method="get" action="">
                <label for="lecturerSelect">Select Lecturer:</label>
                <select id="lecturerSelect" name="lecturerId" onchange="this.form.submit()" required>
                    <option value="">-- Select Lecturer --</option>
                    <% for (Staff lecturer : lecturers) {%>
                    <option value="<%= lecturer.getUserId()%>" <%= (selectedLecturerId != null && selectedLecturerId.equals(lecturer.getUserId())) ? "selected" : ""%>>
                        <%= HtmlUtil.escape(lecturer.getName())%> (<%= HtmlUtil.escape(lecturer.getStaffNumber())%>)
                    </option>
                    <% } %>
                </select>
            </form>

            <% if (selectedLecturerId != null) {%>
            <form method="post" action="<%=request.getContextPath()%>/LecturerSubjectServlet">
                <input type="hidden" name="lecturerId" value="<%= selectedLecturerId%>" />
                <input type="hidden" name="csrfToken" value="<%= session.getAttribute("csrfToken") %>" />

                <h3>Assign Subjects:</h3>
                <% if (subjects.isEmpty()) { %>
                <p>No subjects available.</p>
                <% } else { %>
                <ul style="list-style:none; padding-left:0;">
                    <% for (Subject s : subjects) {%>
                    <li>
                        <label>
                            <input type="checkbox" name="subjectIds" value="<%= s.getSubjectId()%>"
                                   <%= assignedSubjectIds.contains(s.getSubjectId()) ? "checked" : ""%> />
                            <%= HtmlUtil.escape(s.getSubjectName())%>
                        </label>
                    </li>
                    <% } %>
                </ul>
                <% } %>

                <input type="submit" value="Save Assignments" />
            </form>
            <% }%>
        </main>

        <%@ include file="../WEB-INF/jspf/footer.jspf" %>
    </body>
</html>
