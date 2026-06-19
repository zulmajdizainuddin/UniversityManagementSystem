<%-- 
    Document   : ManageUsers
    Created on : Jun 12, 2025, 5:23:06 AM
    Author     : ZULMAJDI
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.project.dao.UserDAO"%>
<%@page import="com.project.model.User"%>
<%@page import="com.project.dao.CoursesDAO"%>
<%@page import="com.project.model.Course"%>
<%@page import="java.util.List"%>
<%@page import="com.project.util.HtmlUtil"%>
<%
    if (session.getAttribute("user") == null || !"Admin".equalsIgnoreCase(((User) session.getAttribute("user")).getRole())) {
        response.sendRedirect("../login.jsp");
        return;
    }
    request.setAttribute("headerTitle", "University Management System - Manage Users");

    CoursesDAO coursesDAO = new CoursesDAO();
    List<Course> courses = coursesDAO.getAllCourses();
    request.setAttribute("courses", courses);
%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8" />
        <title>Manage Users</title>
        <link rel="icon" href="<%=request.getContextPath()%>/images/graduation.png" sizes="32x32" type="image/png" />
        <link rel="stylesheet" href="<%=request.getContextPath()%>/css/layout.css" />
        <link rel="stylesheet" href="<%=request.getContextPath()%>/css/common.css" />
        <style>
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
                background-color: #fff;
                margin: 8% auto;
                padding: 25px 30px;
                border-radius: 12px;
                max-width: 400px;
                box-shadow: 0 8px 25px rgba(230, 126, 34, 0.25);
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
        </style>
    </head>
    <body>

        <%@ include file="../WEB-INF/jspf/header.jspf" %>

        <main class="container">
            <h2>Manage Users</h2>

            <a href="DashboardAdmin.jsp" class="btn-link" style="margin-bottom: 10px;">Home</a>

            <button id="openAddUserModal" class="btn-primary" style="margin-bottom: 15px;">Add User</button>

            <%            String errorMessage = (String) session.getAttribute("errorMessage");
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

            <div id="addUserModal" class="modal" aria-hidden="true" role="dialog" aria-labelledby="addUserTitle" tabindex="-1">
                <div class="modal-content" role="document">
                    <button class="close" aria-label="Close Add User Form">&times;</button>
                    <h3 id="addUserTitle">Add New User</h3>
                    <form id="addUserForm" action="<%= request.getContextPath()%>/UserServlet" method="post" novalidate>
                        <input type="hidden" name="action" value="add" />
                        <input type="hidden" name="csrfToken" value="<%= session.getAttribute("csrfToken") %>" />
                        <label for="modalName">Name:</label>
                        <input type="text" id="modalName" name="name" required />

                        <label for="modalEmail">Email:</label>
                        <input type="email" id="modalEmail" name="email" required />

                        <label for="modalPassword">Password:</label>
                        <input type="password" id="modalPassword" name="password" required />

                        <label for="modalRole">Role:</label>
                        <select id="modalRole" name="role" required>
                            <option value="Admin">Admin</option>
                            <option value="Lecturer">Lecturer</option>
                            <option value="Student">Student</option>
                        </select>

                        <div class="role-fields staff-fields" style="display:none;">
                            <label for="staffNumber">Staff Number:</label>
                            <input type="text" id="staffNumber" name="staffNumber" />
                            
                            <label for="department">Department:</label>
                            <input type="text" id="department" name="department" />
                        </div>

                        <div class="role-fields student-fields" style="display:none;">
                            <label for="studentNumber">Student ID:</label>
                            <input type="text" id="studentNumber" name="studentNumber" />
                            
                            <label for="major">Major:</label>
                            <select id="major" name="major" required>
                                <option value="">-- Select Major --</option>
                                <%
                                    List<Course> coursesList = (List<Course>) request.getAttribute("courses");
                                    for (Course course : coursesList) {
                                %>
                                <option value="<%= HtmlUtil.escape(course.getCourseName())%>"><%= HtmlUtil.escape(course.getCourseName())%></option>
                                <%
                                    }
                                %>
                            </select>
                        </div>

                        <input type="submit" value="Add User" />
                    </form>
                </div>
            </div>

            <hr/>

            <h3>Existing Users:</h3>

            <table class="data-table" aria-label="Existing Users">
                <thead>
                    <tr>
                        <th>No.</th><th>Name</th><th>Email</th><th>Role</th><th>Action</th>
                    </tr>
                </thead>
                <tbody>
                    <%
                        UserDAO userDAO = new UserDAO();
                        List<User> userList = userDAO.getAllUsers();
                        int count = 1;
                        for (User user : userList) {
                    %>
                    <tr>
                        <td><%= count++%></td>
                        <td><%= HtmlUtil.escape(user.getName())%></td>
                        <td><%= HtmlUtil.escape(user.getEmail())%></td>
                        <td><%= HtmlUtil.escape(user.getRole())%></td>
                        <td>
                            <a href="EditUser.jsp?id=<%= user.getUserId()%>" class="btn-link">Edit</a>&nbsp;
                            <% if (!"admin".equalsIgnoreCase(user.getRole())) {%>
                            <a href="<%= request.getContextPath()%>/UserServlet?action=delete&id=<%= user.getUserId()%>" class="btn-link delete">Delete</a>
                            <% } %>
                        </td>
                    </tr>
                    <% }%>
                </tbody>
            </table>
        </main>

        <%@ include file="../WEB-INF/jspf/footer.jspf" %>

        <script>
            const modal = document.getElementById('addUserModal');
            const openBtn = document.getElementById('openAddUserModal');
            const closeBtn = modal.querySelector('.close');

            openBtn.addEventListener('click', () => {
                modal.style.display = 'block';
                modal.setAttribute('aria-hidden', 'false');
                modal.querySelector('input, select').focus();
            });

            closeBtn.addEventListener('click', () => {
                modal.style.display = 'none';
                modal.setAttribute('aria-hidden', 'true');
                openBtn.focus();
            });

            window.addEventListener('click', (event) => {
                if (event.target === modal) {
                    modal.style.display = 'none';
                    modal.setAttribute('aria-hidden', 'true');
                    openBtn.focus();
                }
            });

            document.querySelectorAll('.btn-link.delete').forEach(link => {
                link.addEventListener('click', e => {
                    if (!confirm('Are you sure you want to delete this user?')) {
                        e.preventDefault();
                    }
                });
            });

            function toggleRoleFields(role) {
                document.querySelector('.staff-fields').style.display = (role === 'Admin' || role === 'Lecturer') ? '' : 'none';
                document.querySelector('.student-fields').style.display = (role === 'Student') ? '' : 'none';
            }
            document.getElementById('modalRole').addEventListener('change', function () {
                toggleRoleFields(this.value);
            });
            window.onload = function () {
                toggleRoleFields(document.getElementById('modalRole').value);
            };
        </script>
    </body>
</html>
