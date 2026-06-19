<%-- 
    Document   : ManageSubjects
    Created on : Jun 12, 2025, 6:19:51 AM
    Author     : ZULMAJDI
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.project.dao.SubjectDAO"%>
<%@page import="com.project.dao.CoursesDAO"%>
<%@page import="com.project.model.Subject"%>
<%@page import="com.project.model.Course"%>
<%@page import="java.util.List"%>
<%@page import="com.project.util.HtmlUtil"%>
<%
    if (session.getAttribute("user") == null) {
        response.sendRedirect("../login.jsp");
        return;
    }
    request.setAttribute("headerTitle", "University Management System - Manage Subjects");

    // Load courses for dropdown
    CoursesDAO coursesDAO = new CoursesDAO();
    List<Course> courseList = coursesDAO.getAllCourses();
%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8" />
        <title>Manage Subjects</title>
        <link rel="icon" href="<%=request.getContextPath()%>/images/graduation.png" sizes="32x32" type="image/png" />
        <link rel="stylesheet" href="<%=request.getContextPath()%>/css/layout.css" />
        <link rel="stylesheet" href="<%=request.getContextPath()%>/css/common.css" />
        <style>
            /* Modal styles */
            .modal {
                display: none;
                position: fixed;
                z-index: 1000;
                left: 0;
                top: 0;
                width: 100%;
                height: 100%;
                background-color: rgba(0,0,0,0.5);
                overflow: auto;
            }
            .modal-content {
                background: white;
                margin: 8% auto;
                padding: 25px 30px;
                border-radius: 12px;
                max-width: 400px;
                box-shadow: 0 8px 25px rgba(230,126,34,0.25);
                position: relative;
            }
            .close {
                position: absolute;
                top: 12px;
                right: 18px;
                font-size: 28px;
                font-weight: bold;
                color: #e67e22;
                background: none;
                border: none;
                cursor: pointer;
                transition: color 0.3s ease;
            }
            .close:hover {
                color: #d35400;
            }
            .success {
                color: #27ae60;
                font-weight: bold;
                margin-bottom: 18px;
                text-align: center;
            }
            .error {
                color: #e74c3c;
                font-weight: bold;
                margin-bottom: 18px;
                text-align: center;
            }
        </style>
    </head>
    <body>
        <%@ include file="../WEB-INF/jspf/header.jspf" %>

        <main class="container">
            <h2>Manage Subjects</h2>

            <a href="DashboardAdmin.jsp" class="btn-link" style="margin-bottom: 10px;">Home</a>

            <button id="openSubjectModal" class="btn-primary" style="margin-bottom: 15px;">Add Subject</button>

            <%-- Messages --%>
            <%        String errorMessage = (String) session.getAttribute("errorMessage");
                String successMessage = (String) session.getAttribute("successMessage");
                if (errorMessage != null) {
            %>
            <p class="error"><%= errorMessage%></p>
            <%
                session.removeAttribute("errorMessage");
            } else if (successMessage != null) {
            %>
            <p class="success"><%= successMessage%></p>
            <%
                    session.removeAttribute("successMessage");
                }
            %>

            <%-- Modal for Add/Edit --%>
            <div id="subjectModal" class="modal" aria-hidden="true" role="dialog" aria-labelledby="subjectModalTitle" tabindex="-1">
                <div class="modal-content" role="document">
                    <button class="close" aria-label="Close Subject Form">&times;</button>
                    <h3 id="subjectModalTitle">Add Subject</h3>
                    <form id="subjectForm" action="<%= request.getContextPath()%>/SubjectServlet" method="post" novalidate>
                        <input type="hidden" name="action" value="add" />
                        <input type="hidden" name="subjectId" id="subjectId" value="" />
                        <input type="hidden" name="csrfToken" value="<%= session.getAttribute("csrfToken") %>" />

                        <label for="subjectCode">Subject Code:</label>
                        <input type="text" id="subjectCode" name="subjectCode" required placeholder="e.g., CS101" />

                        <label for="subjectName">Subject Name:</label>
                        <input type="text" id="subjectName" name="subjectName" required />

                        <label for="courseId">Course:</label>
                        <select id="courseId" name="courseId" required>
                            <option value="">-- Select Course --</option>
                            <% for (Course course : courseList) {%>
                            <option value="<%= course.getCourseId()%>"><%= course.getCourseName()%></option>
                            <% } %>
                        </select>

                        <input type="submit" value="Save" />
                    </form>
                </div>
            </div>

            <hr />

            <h3>Existing Subjects:</h3>

            <table class="data-table" aria-label="Existing Subjects">
                <thead>
                    <tr>
                        <th>No.</th><th>Subject Code</th><th>Subject Name</th><th>Course</th><th>Action</th>
                    </tr>
                </thead>
                <tbody>
                    <%
                        SubjectDAO subjectDAO = new SubjectDAO();
                        List<Subject> subjectList = subjectDAO.getAllSubjects();
                        int count = 1;

                        // For displaying course name by courseId, create a Map for quick lookup
                        java.util.Map<Integer, String> courseMap = new java.util.HashMap<>();
                        for (Course c : courseList) {
                            courseMap.put(c.getCourseId(), c.getCourseName());
                        }

                        for (Subject subject : subjectList) {
                    %>
                    <tr>
                        <td><%= count++%></td>
                        <td><%= subject.getSubjectCode() != null ? HtmlUtil.escape(subject.getSubjectCode()) : "N/A"%></td>
                        <td><%= HtmlUtil.escape(subject.getSubjectName())%></td>
                        <td><%= courseMap.get(subject.getCourseId()) != null ? HtmlUtil.escape(courseMap.get(subject.getCourseId())) : "N/A"%></td>
                        <td>
                            <button class="btn-link edit-btn"
                                    data-id="<%= subject.getSubjectId()%>"
                                    data-code="<%= subject.getSubjectCode() != null ? HtmlUtil.escape(subject.getSubjectCode()) : ""%>"
                                    data-name="<%= HtmlUtil.escape(subject.getSubjectName())%>"
                                    data-courseid="<%= subject.getCourseId()%>">Edit</button>
                            <a href="<%= request.getContextPath()%>/SubjectServlet?action=delete&id=<%= subject.getSubjectId()%>" class="btn-link delete" onclick="return confirm('Are you sure you want to delete this subject?');">Delete</a>
                        </td>
                    </tr>
                    <%
                        }
                    %>
                </tbody>
            </table>
        </main>

        <%@ include file="../WEB-INF/jspf/footer.jspf" %>

        <script>
            const modal = document.getElementById('subjectModal');
            const openBtn = document.getElementById('openSubjectModal');
            const closeBtn = modal.querySelector('.close');
            const form = document.getElementById('subjectForm');
            const modalTitle = document.getElementById('subjectModalTitle');
            const subjectIdInput = document.getElementById('subjectId');
            const subjectCodeInput = document.getElementById('subjectCode');
            const subjectNameInput = document.getElementById('subjectName');
            const courseIdSelect = document.getElementById('courseId');

            openBtn.addEventListener('click', () => {
                modal.style.display = 'block';
                modal.setAttribute('aria-hidden', 'false');
                modalTitle.textContent = 'Add Subject';
                form.action.value = 'add';
                subjectIdInput.value = '';
                subjectCodeInput.value = '';
                subjectNameInput.value = '';
                courseIdSelect.value = '';
                subjectCodeInput.focus();
            });

            closeBtn.addEventListener('click', () => {
                modal.style.display = 'none';
                modal.setAttribute('aria-hidden', 'true');
            });

            window.addEventListener('click', (event) => {
                if (event.target === modal) {
                    modal.style.display = 'none';
                    modal.setAttribute('aria-hidden', 'true');
                }
            });

            document.querySelectorAll('.edit-btn').forEach(button => {
                button.addEventListener('click', () => {
                    modal.style.display = 'block';
                    modal.setAttribute('aria-hidden', 'false');
                    modalTitle.textContent = 'Edit Subject';
                    form.action.value = 'update';
                    subjectIdInput.value = button.dataset.id;
                    subjectCodeInput.value = button.dataset.code || '';
                    subjectNameInput.value = button.dataset.name;
                    courseIdSelect.value = button.dataset.courseid;
                    subjectCodeInput.focus();
                });
            });
        </script>

    </body>
</html>
