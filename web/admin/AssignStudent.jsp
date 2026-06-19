<%-- 
    Document   : AssignStudent
    Created on : 18 Jun 2025, 8:12:42 pm
    Author     : NuNa
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@page import="java.util.List"%>
<%@page import="com.project.model.Student"%>
<%@page import="com.project.model.Subject"%>
<%@page import="com.project.model.Class"%>
<%@page import="com.project.dao.StudentDAO"%>
<%@page import="com.project.dao.SubjectDAO"%>
<%@page import="com.project.dao.ClassesDAO"%>

<%
    com.project.model.User user = (com.project.model.User) session.getAttribute("user");
    if (user == null || !"Admin".equalsIgnoreCase(user.getRole())) {
        response.sendRedirect("../login.jsp");
        return;
    }

    StudentDAO studentDAO = new StudentDAO();
    SubjectDAO subjectDAO = new SubjectDAO();
    ClassesDAO classesDAO = new ClassesDAO();

    List<Student> students = studentDAO.getAllStudents();
    List<Subject> subjects = subjectDAO.getAllSubjects();

    Integer selectedSubjectId = (Integer) request.getAttribute("selectedSubjectId");
    if (selectedSubjectId == null) {
        String param = request.getParameter("subjectId");
        try {
            selectedSubjectId = (param != null && !param.isEmpty()) ? Integer.parseInt(param) : 0;
        } catch (NumberFormatException e) {
            selectedSubjectId = 0;
        }
    }

    List<Class> classes;
    if (selectedSubjectId > 0) {
        classes = classesDAO.getClassesBySubject(selectedSubjectId);
    } else {
        classes = new java.util.ArrayList<>();
    }

    request.setAttribute("students", students);
    request.setAttribute("subjects", subjects);
    request.setAttribute("classes", classes);
    request.setAttribute("selectedSubjectId", selectedSubjectId);
%>

<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8" />
        <title>Assign Students to Subjects and Classes</title>
        <link rel="stylesheet" href="<%=request.getContextPath()%>/css/common.css" />
        <link rel="stylesheet" href="<%=request.getContextPath()%>/css/layout.css" />
        <style>
            .success-message {
                color: green;
                margin-bottom: 1em;
            }
            .error-message {
                color: red;
                margin-bottom: 1em;
            }
        </style>
    </head>
    <body>
        <%@ include file="../WEB-INF/jspf/header.jspf" %>

        <main class="container">
            <h2>Assign Student to Subject and Class</h2>

            <nav>
                <a href="<%=request.getContextPath()%>/admin/DashboardAdmin.jsp" class="btn-link" style="margin-bottom: 20px;">Home</a> &nbsp;
            </nav>

            <c:if test="${not empty message}">
                <div class="success-message">${message}</div>
            </c:if>
            <c:if test="${not empty error}">
                <div class="error-message">${error}</div>
            </c:if>

            <form action="AssignStudent.jsp" method="get">
                <label for="subjectId">Select Subject:</label>
                <select name="subjectId" id="subjectId" onchange="this.form.submit()">
                    <option value="">-- Select Subject --</option>
                    <c:forEach var="sub" items="${subjects}">
                        <option value="${sub.subjectId}" <c:if test="${sub.subjectId == selectedSubjectId}">selected</c:if>>
                            ${sub.subjectCode} - ${sub.subjectName}
                        </option>
                    </c:forEach>
                </select>
                <noscript><input type="submit" value="Load Classes" /></noscript>
            </form>

            <form action="AssignStudentServlet" method="post">
                <input type="hidden" name="csrfToken" value="<%= session.getAttribute("csrfToken") %>" />
                <label for="classId">Select Class:</label>
                <select name="classId" id="classId" required>
                    <option value="">-- Select Class --</option>
                    <c:forEach var="c" items="${classes}">
                        <option value="${c.classId}">${c.className}</option>
                    </c:forEach>
                </select>

                <label for="studentId">Select Student:</label>
                <select name="studentId" id="studentId" required>
                    <option value="">-- Select Student --</option>
                    <c:forEach var="s" items="${students}">
                        <option value="${s.userId}">${s.name} (Student Number: ${s.studentNumber})</option>
                    </c:forEach>
                </select>

                <input type="hidden" name="subjectId" value="${selectedSubjectId}" />

                <button type="submit">Assign</button>
            </form>
        </main>

        <%@ include file="../WEB-INF/jspf/footer.jspf" %>
    </body>
</html>
