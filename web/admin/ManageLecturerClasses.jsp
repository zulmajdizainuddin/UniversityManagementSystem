<%-- 
    Document   : AssignLecturerClasses
    Created on : 17 Jun 2025, 14:31:55
    Author     : nabil
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.project.dao.ClassesDAO"%>
<%@page import="com.project.dao.StaffDAO"%>
<%@page import="com.project.dao.LecturerClassDAO"%>
<%@page import="com.project.model.Staff"%>
<%@page import="com.project.model.Class"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.project.util.HtmlUtil"%>

<%
    // Check if the user is logged in by verifying the session attribute "user"
    // If not logged in, redirect to the login page and stop further processing
    if (session.getAttribute("user") == null) {
        response.sendRedirect("../login.jsp");
        return;
    }

    // Set a page header title attribute for use in the JSP layout or header
    request.setAttribute("headerTitle", "University Management System - Assign Lecturers To Classes");

    // Instantiate DAO objects to interact with the database
    StaffDAO staffDAO = new StaffDAO();
    ClassesDAO classDAO = new ClassesDAO();
    LecturerClassDAO lcDAO = new LecturerClassDAO();

    // Retrieve the list of all lecturers from the database
    List<Staff> lecturers = staffDAO.getAllLecturers();

    // Retrieve the list of all classes from the database
    List<Class> classes = classDAO.getAllClasses();

    // Get the lecturerId parameter from the request (from dropdown selection)
    String lecturerIdParam = request.getParameter("lecturerId");
    Integer selectedLecturerId = null; // Will hold the selected lecturer's ID
    List<Integer> assignedClassIds = new ArrayList<>(); // List of class IDs assigned to the selected lecturer

    // If a lecturer is selected (lecturerIdParam is not null or empty)
    if (lecturerIdParam != null && !lecturerIdParam.isEmpty()) {
        // Parse the lecturer ID from String to Integer
        selectedLecturerId = Integer.parseInt(lecturerIdParam);

        // Fetch the list of class IDs assigned to the selected lecturer
        assignedClassIds = lcDAO.getClassIdsByLecturer(selectedLecturerId);
    }

    // Retrieve any error or success messages stored in the session (e.g., from previous actions)
    String errorMessage = (String) session.getAttribute("errorMessage");
    String successMessage = (String) session.getAttribute("successMessage");

    // Remove messages from session after retrieving to avoid repeated display
    session.removeAttribute("errorMessage");
    session.removeAttribute("successMessage");

    // Defensive null checks to avoid NullPointerException in case any list is null
    if (lecturers == null) {
        lecturers = new java.util.ArrayList<>();
    }
    if (classes == null) {
        classes = new java.util.ArrayList<>();
    }
    if (assignedClassIds == null) {
        assignedClassIds = new java.util.ArrayList<>();
    }
%>

<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8" />
        <title>Manage Lecturer - Class Assignments</title>
        <link rel="stylesheet" href="<%=request.getContextPath()%>/css/layout.css" />
        <link rel="stylesheet" href="<%=request.getContextPath()%>/css/common.css" />
    </head>
    <body>
        <%@ include file="../WEB-INF/jspf/header.jspf" %>

        <main class="container">
            <h2>Assign Lecturers to Classes</h2>

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
            <form method="post" action="<%=request.getContextPath()%>/LecturerClassServlet">
                <input type="hidden" name="lecturerId" value="<%= selectedLecturerId%>" />
                <input type="hidden" name="csrfToken" value="<%= session.getAttribute("csrfToken") %>" />

                <h3>Assign Classes:</h3>
                <% if (classes.isEmpty()) { %>
                <p>No classes available.</p>
                <% } else { %>
                <ul style="list-style:none; padding-left:0;">
                    <% for (Class c : classes) {%>
                    <li>
                        <label>
                            <input type="checkbox" name="classIds" value="<%= c.getClassId()%>"
                                   <%= assignedClassIds.contains(c.getClassId()) ? "checked" : ""%> />
                            <%= HtmlUtil.escape(c.getClassName())%>
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
