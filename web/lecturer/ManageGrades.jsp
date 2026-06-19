<%-- 
    Document   : ManageGrades
    Created on : 18 Jun 2025, 3:57:21?pm
    Author     : NuNa
--%>

<%@page import="java.util.List"%>
<%@page import="com.project.model.Grade"%>
<%@page import="com.project.model.Student"%>
<%@page import="com.project.model.Subject"%>
<%@page import="com.project.util.HtmlUtil"%>

<%
    // Check user login
    if (session.getAttribute("user") == null) {
        response.sendRedirect("../login.jsp");
        return;
    }
    request.setAttribute("headerTitle", "University Management System - Manage Student Grades");

    // Defensive retrieval of request attributes
    List<Subject> subjects = (List<Subject>) request.getAttribute("subjects");
    List<Student> students = (List<Student>) request.getAttribute("students");
    List<Grade> grades = (List<Grade>) request.getAttribute("grades");
    Integer selectedSubjectId = (Integer) request.getAttribute("selectedSubjectId");

    if (subjects == null) {
        subjects = new java.util.ArrayList<>();
    }
    if (students == null) {
        students = new java.util.ArrayList<>();
    }
    if (grades == null) {
        grades = new java.util.ArrayList<>();
    }

    // Retrieve and clear session messages
    String errorMessage = (String) session.getAttribute("errorMessage");
    String successMessage = (String) session.getAttribute("successMessage");
    session.removeAttribute("errorMessage");
    session.removeAttribute("successMessage");
%>

<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8" />
        <title>Manage Grades</title>
        <link rel="icon" href="<%=request.getContextPath()%>/images/graduation.png" sizes="32x32" type="image/png" />
        <link rel="stylesheet" href="<%=request.getContextPath()%>/css/layout.css" />
        <link rel="stylesheet" href="<%=request.getContextPath()%>/css/common.css" />
    </head>
    <body>
        <%@ include file="../WEB-INF/jspf/header.jspf" %>

        <main class="container">
            <h2>Manage Grades</h2>

            <nav>
                <a href="<%=request.getContextPath()%>/lecturer/DashboardLecturer.jsp" class="btn-link">Home</a> &nbsp;
                <a href="<%=request.getContextPath()%>/GradeServlet?action=viewSubjects" class="btn-link">Select Subject</a>
            </nav>

            <% if (errorMessage != null) {%>
            <p class="error"><%= errorMessage%></p>
            <% } %>
            <% if (successMessage != null) {%>
            <p class="success"><%= successMessage%></p>
            <% } %>

            <% if ("viewSubjects".equals(request.getAttribute("action"))) {%>
            <h3>Select Subject to Manage Grades</h3>
            <form method="get" action="<%=request.getContextPath()%>/GradeServlet">
                <input type="hidden" name="action" value="viewGrades" />
                <label for="subjectSelect">Subject:</label>
                <select id="subjectSelect" name="subjectId" required>
                    <option value="">-- Select Subject --</option>
                    <% for (Subject s : subjects) {%>
                    <option value="<%= s.getSubjectId()%>"><%= s.getSubjectName()%></option>
                    <% } %>
                </select>
                <input type="submit" value="View Grades" />
            </form>
            <% } else if ("viewGrades".equals(request.getAttribute("action"))) { %>
            <h3>Grades for Subject: 
                <%
                    for (Subject s : subjects) {
                        if (selectedSubjectId != null && selectedSubjectId.equals(s.getSubjectId())) {
                            out.print(s.getSubjectName());
                            break;
                        }
                    }
                %>
            </h3>
            <form method="post" action="<%=request.getContextPath()%>/GradeServlet?action=saveGrades">
                <input type="hidden" name="subjectId" value="<%= selectedSubjectId%>" />
                <input type="hidden" name="csrfToken" value="<%= session.getAttribute("csrfToken") %>" />
                <table class="data-table" border="1" cellpadding="5" cellspacing="0">
                    <thead>
                        <tr>
                            <th>Student Number</th>
                            <th>Student Name</th>
                            <th>Score</th>
                        </tr>
                    </thead>
                    <tbody>
                        <%
                            java.util.Map<Integer, Double> gradeMap = new java.util.HashMap<>();
                            for (Grade g : grades) {
                                gradeMap.put(g.getStudentId(), g.getScore());
                            }

                            for (Student student : students) {
                        %>
                        <tr>
                            <td><%= HtmlUtil.escape(student.getStudentNumber())%></td>
                            <td><%= HtmlUtil.escape(student.getName())%></td>
                            <td>
                                <input type="hidden" name="studentIds" value="<%= student.getUserId()%>" />
                                <input type="number" step="0.01" min="0" max="100" name="score_<%= student.getUserId()%>" value="<%= gradeMap.containsKey(student.getUserId()) ? gradeMap.get(student.getUserId()) : ""%>" required />
                            </td>
                        </tr>
                        <% } %>
                    </tbody>
                </table>
                <br/>
                <input type="submit" value="Save Grades" />
            </form>
            <% }%>
        </main>

        <%@ include file="../WEB-INF/jspf/footer.jspf" %>
    </body>
</html>
