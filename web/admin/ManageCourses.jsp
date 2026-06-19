<%-- 
    Document   : ManageClasses
    Created on : Jun 12, 2025, 6:24:29 AM
    Author     : ZULMAJDI
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.project.dao.CoursesDAO"%>
<%@page import="com.project.model.Course"%>
<%@page import="java.util.List"%>
<%@page import="com.project.util.HtmlUtil"%>
<%
    if (session.getAttribute("user") == null) {
        response.sendRedirect("../login.jsp");
        return;
    }
    request.setAttribute("headerTitle", "University Management System - Manage Courses");
%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8" />
        <title>Manage Courses</title>
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
            <h2>Manage Courses</h2>

            <a href="DashboardAdmin.jsp" class="btn-link" style="margin-bottom: 10px;">Home</a>

            <button id="openCourseModal" class="btn-primary" style="margin-bottom: 15px;">Add Course</button>

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
            <div id="courseModal" class="modal" aria-hidden="true" role="dialog" aria-labelledby="courseModalTitle" tabindex="-1">
                <div class="modal-content" role="document">
                    <button class="close" aria-label="Close Course Form">&times;</button>
                    <h3 id="courseModalTitle">Add Course</h3>
                    <form id="courseForm" action="<%= request.getContextPath()%>/CourseServlet" method="post" novalidate>
                        <input type="hidden" name="action" value="add" />
                        <input type="hidden" name="courseId" id="courseId" value="" />
                        <input type="hidden" name="csrfToken" id="csrfToken" value="<%= session.getAttribute("csrfToken") %>" />
                        
                        <label for="courseName">Course Name:</label>
                        <input type="text" id="courseName" name="courseName" required />
                        <input type="submit" value="Save" />
                    </form>
                </div>
            </div>

            <hr />

            <h3>Existing Courses:</h3>

            <table class="data-table" aria-label="Existing Courses">
                <thead>
                    <tr>
                        <th>No.</th><th>Course Name</th><th>Action</th>
                    </tr>
                </thead>
                <tbody>
                    <%
                        CoursesDAO courseDAO = new CoursesDAO();
                        List<Course> courseList = courseDAO.getAllCourses();
                        int count = 1;
                        for (Course c : courseList) {
                    %>
                    <tr>
                        <td><%= count++%></td>
                        <td><%= HtmlUtil.escape(c.getCourseName())%></td>
                        <td>
                            <button class="btn-link edit-btn" data-id="<%= c.getCourseId()%>" data-name="<%= HtmlUtil.escape(c.getCourseName())%>">Edit</button>
                            <a href="<%= request.getContextPath()%>/CourseServlet?action=delete&id=<%= c.getCourseId()%>" class="btn-link delete" onclick="return confirm('Are you sure you want to delete this course?');">Delete</a>
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
            const modal = document.getElementById('courseModal');
            const openBtn = document.getElementById('openCourseModal');
            const closeBtn = modal.querySelector('.close');
            const form = document.getElementById('courseForm');
            const modalTitle = document.getElementById('courseModalTitle');
            const courseIdInput = document.getElementById('courseId');
            const courseNameInput = document.getElementById('courseName');

            openBtn.addEventListener('click', () => {
                modal.style.display = 'block';
                modal.setAttribute('aria-hidden', 'false');
                modalTitle.textContent = 'Add Course';
                form.action.value = 'add';
                courseIdInput.value = '';
                courseNameInput.value = '';
                courseNameInput.focus();
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
                    modalTitle.textContent = 'Edit Course';
                    form.action.value = 'update';
                    courseIdInput.value = button.dataset.id;
                    courseNameInput.value = button.dataset.name;
                    courseNameInput.focus();
                });
            });
        </script>

    </body>
</html>
