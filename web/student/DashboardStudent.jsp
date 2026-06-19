<%-- 
    Document   : DashboardStudent
    Created on : Jun 12, 2025, 5:41:23 AM
    Author     : ZULMAJDI
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.project.model.User"%>
<%@page import="com.project.dao.*"%>
<%@page import="com.project.model.Subject"%>
<%@page import="com.project.model.Class"%>
<%@page import="com.project.model.Course"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.project.util.HtmlUtil"%>

<%
    User user = (User) session.getAttribute("user");
    if (user == null) {
        response.sendRedirect("../login.jsp");
        return;
    }
    request.setAttribute("headerTitle", "University Management System - Student Dashboard");

    int studentId = user.getUserId();

    CoursesDAO courseDAO = new CoursesDAO();
    SubjectDAO subjectDAO = new SubjectDAO();
    ClassesDAO classesDAO = new ClassesDAO();

    String courseName = "N/A";
    List<Subject> enrolledSubjects = null;

    try {
        courseName = courseDAO.getCourseNameByStudentId(studentId);
        enrolledSubjects = subjectDAO.getEnrolledSubjectsByStudent(studentId);
    } catch (Exception e) {
        e.printStackTrace();
        // Set to empty list to avoid NullPointerException in JSP if error occurs
        enrolledSubjects = new ArrayList<>();
    }
%>

<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8" />
        <title>Student Dashboard</title>
        <link rel="icon" href="<%=request.getContextPath()%>/images/graduation.png" sizes="32x32" type="image/png" />
        <link rel="stylesheet" href="<%=request.getContextPath()%>/css/layout.css" />
        <link rel="stylesheet" href="<%=request.getContextPath()%>/css/common.css" />
        <link rel="stylesheet" href="<%=request.getContextPath()%>/css/dashboard.css" />
    </head>
    <body>

        <%@ include file="../WEB-INF/jspf/header.jspf" %>

        <main class="container">
            <h2>Welcome, <%= HtmlUtil.escape(user.getName())%>!</h2>

            <%-- Display error/success messages --%>
            <%
                String errorMessage = (String) session.getAttribute("errorMessage");
                String successMessage = (String) session.getAttribute("successMessage");
                if (errorMessage != null) {
                    session.removeAttribute("errorMessage");
            %>
            <div style="color: #e74c3c; font-weight: bold; padding: 10px; background: #ffeaea; border-radius: 4px; margin-bottom: 15px;">
                <%= HtmlUtil.escape(errorMessage) %>
            </div>
            <% } else if (successMessage != null) {
                session.removeAttribute("successMessage");
            %>
            <div style="color: #27ae60; font-weight: bold; padding: 10px; background: #eafaf1; border-radius: 4px; margin-bottom: 15px;">
                <%= HtmlUtil.escape(successMessage) %>
            </div>
            <% } %>

            <div class="dashboard-container" aria-label="Student course summary">
                <div class="widget" tabindex="0" role="region" aria-labelledby="courseName">
                    <p>Your Course</p>
                    <h3 id="courseName"><%= HtmlUtil.escape(courseName)%></h3>
                </div>
            </div>

            <section>
                <h3>Your Enrolled Subjects and Classes</h3>

                <table class="data-table" aria-label="Enrolled Subjects and Classes">
                    <thead>
                        <tr>
                            <th>No.</th>
                            <th>Subject Name</th>
                            <th>Classes</th>
                        </tr>
                    </thead>
                    <tbody>
                        <%
                            if (enrolledSubjects != null && !enrolledSubjects.isEmpty()) {
                                int count = 1; // For numbering rows
                                for (Subject subject : enrolledSubjects) {
                                    List<com.project.model.Class> classesForSubject = null;
                                    try {
                                        classesForSubject = classesDAO.getEnrolledClassesByStudentAndSubject(studentId, subject.getSubjectId());
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        classesForSubject = new ArrayList<>(); // Ensure it's not null
                                    }

                                    StringBuilder classNamesBuilder = new StringBuilder();
                                    if (classesForSubject != null && !classesForSubject.isEmpty()) {
                                        for (com.project.model.Class cls : classesForSubject) {
                                            classNamesBuilder.append(HtmlUtil.escape(cls.getClassName())).append("<br/>");
                                        }
                                    } else {
                                        classNamesBuilder.append("N/A"); // No classes for this subject
                                    }
                        %>
                        <tr>
                            <td><%= count++%>.</td>
                            <td><%= HtmlUtil.escape(subject.getSubjectName())%></td>
                            <td><%= classNamesBuilder.toString()%></td>
                        </tr>
                        <%
                            }
                        } else {
                        %>
                        <tr>
                            <td colspan="3">You are not enrolled in any subjects yet.</td>
                        </tr>
                        <% }%>
                    </tbody>
                </table>
            </section>

            <nav>
                <ul class="dashboard-menu">
                    <li><a href="<%= request.getContextPath()%>/AttendanceStudentServlet">View Attendance</a></li>
                    <li><a href="<%= request.getContextPath()%>/GradeStudentServlet">View Grades</a></li>
                    <li><a href="#" onclick="showEvaluationForm(); return false;">Submit Course Evaluation</a></li>
                    <li><a href="<%= request.getContextPath()%>/LogoutServlet">Logout</a></li>
                </ul>
            </nav>
        </main>

        <!-- Evaluation Subject Selection Modal -->
        <div id="evalModal" style="display: none; position: fixed; z-index: 1000; left: 0; top: 0; width: 100%; height: 100%; background-color: rgba(0,0,0,0.5);">
            <div style="background: white; margin: 10% auto; padding: 25px; border-radius: 12px; max-width: 400px; box-shadow: 0 8px 25px rgba(230,126,34,0.25); position: relative;">
                <button onclick="closeEvalModal()" style="position: absolute; top: 12px; right: 18px; font-size: 28px; font-weight: bold; color: #e67e22; background: none; border: none; cursor: pointer;">&times;</button>
                <h3>Select Subject for Evaluation</h3>
                <form method="get" action="<%= request.getContextPath()%>/StudentCourseEvaluationServlet" id="evalSubjectForm">
                    <label for="evalSubjectId">Subject:</label>
                    <select name="subjectId" id="evalSubjectId" required style="width: 100%; padding: 8px; margin: 10px 0; border: 1px solid #ddd; border-radius: 4px;">
                        <option value="">-- Select Subject --</option>
                        <% if (enrolledSubjects != null && !enrolledSubjects.isEmpty()) {
                            for (Subject subj : enrolledSubjects) { %>
                        <option value="<%= subj.getSubjectId() %>">
                            <%= subj.getSubjectCode() != null ? HtmlUtil.escape(subj.getSubjectCode()) + " - " : "" %><%= HtmlUtil.escape(subj.getSubjectName()) %>
                        </option>
                        <% }
                        } %>
                    </select>
                    <div id="subjectError" style="color: #e74c3c; font-size: 0.9rem; margin-top: 5px; display: none;">Please select a subject before continuing.</div>
                    <div style="margin-top: 15px; text-align: right;">
                        <input type="submit" value="Continue" style="padding: 10px 20px;" />
                        <button type="button" onclick="closeEvalModal()" style="padding: 10px 20px; margin-left: 10px; background: #95a5a6;">Cancel</button>
                    </div>
                </form>
            </div>
        </div>

        <script>
            // Ensure DOM is ready before attaching event listeners
            document.addEventListener('DOMContentLoaded', function() {
                const form = document.getElementById('evalSubjectForm');
                if (form) {
                    form.addEventListener('submit', function(e) {
                        const subjectSelect = document.getElementById('evalSubjectId');
                        const errorDiv = document.getElementById('subjectError');
                        
                        if (!subjectSelect || !subjectSelect.value || subjectSelect.value === '') {
                            e.preventDefault();
                            if (errorDiv) {
                                errorDiv.style.display = 'block';
                            }
                            if (subjectSelect) {
                                subjectSelect.focus();
                            }
                            return false;
                        }
                        if (errorDiv) {
                            errorDiv.style.display = 'none';
                        }
                        // Allow form to submit normally
                        return true;
                    });
                }
            });

            function showEvaluationForm() {
                const modal = document.getElementById('evalModal');
                if (modal) {
                    modal.style.display = 'block';
                    // Reset form when opening
                    const form = document.getElementById('evalSubjectForm');
                    if (form) {
                        form.reset();
                    }
                    const errorDiv = document.getElementById('subjectError');
                    if (errorDiv) {
                        errorDiv.style.display = 'none';
                    }
                }
            }
            
            function closeEvalModal() {
                const modal = document.getElementById('evalModal');
                if (modal) {
                    modal.style.display = 'none';
                }
            }
            
            window.onclick = function(event) {
                const modal = document.getElementById('evalModal');
                if (event.target === modal) {
                    modal.style.display = 'none';
                }
            }
        </script>

        <%@ include file="../WEB-INF/jspf/footer.jspf" %>
    </body>
</html>
