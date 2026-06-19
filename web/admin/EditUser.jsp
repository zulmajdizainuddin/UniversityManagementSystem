<%-- 
    Document   : EditUser
    Created on : Jun 12, 2025, 6:07:54 AM
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
    int userId = 0;
    try {
        userId = Integer.parseInt(request.getParameter("id"));
    } catch (Exception e) {
        response.sendRedirect("ManageUsers.jsp");
        return;
    }

    UserDAO userDAO = new UserDAO();
    User user = null;
    try {
        user = userDAO.getUserById(userId);
        if (user == null) {
            response.sendRedirect("ManageUsers.jsp");
            return;
        }
    } catch (Exception e) {
        e.printStackTrace();
        response.sendRedirect("ManageUsers.jsp");
        return;
    }
    request.setAttribute("headerTitle", "University Management System - Edit User");

    CoursesDAO coursesDAO = new CoursesDAO();
    List<Course> courses = coursesDAO.getAllCourses();
    request.setAttribute("courses", courses);
%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8" />
        <title>Edit User</title>
        <link rel="icon" href="<%=request.getContextPath()%>/images/graduation.png" sizes="32x32" type="image/png" />
        <link rel="stylesheet" href="<%=request.getContextPath()%>/css/layout.css" />
        <link rel="stylesheet" href="<%=request.getContextPath()%>/css/common.css" />
        <style>
            .modal {
                display: block; /* Show modal immediately */
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
        </style>
    </head>
    <body>

        <%@ include file="../WEB-INF/jspf/header.jspf" %>

        <main class="container">
            <div id="editUserModal" class="modal" aria-hidden="false" role="dialog" aria-labelledby="editUserTitle" tabindex="-1">
                <div class="modal-content" role="document">
                    <button id="closeModal" class="close" aria-label="Close Edit User Form">&times;</button>
                    <h3 id="editUserTitle">Edit User</h3>
                    <form id="editUserForm" action="<%= request.getContextPath()%>/UserServlet" method="post" novalidate>
                        <input type="hidden" name="action" value="update" />
                        <input type="hidden" name="userId" value="<%= user.getUserId()%>" />
                        <input type="hidden" name="csrfToken" value="<%= session.getAttribute("csrfToken") %>" />

                        <label for="editName">Name:</label>
                        <input type="text" id="editName" name="name" value="<%= HtmlUtil.escape(user.getName())%>" required />

                        <label for="editEmail">Email:</label>
                        <input type="email" id="editEmail" name="email" value="<%= HtmlUtil.escape(user.getEmail())%>" required />

                        <label for="editPassword">Password: <small>Leave blank to keep unchanged</small></label>
                        <input type="password" id="editPassword" name="password" placeholder="New password" />

                        <label for="editRole">Role:</label>
                        <select id="editRole" name="role" required>
                            <option value="Admin" <%= "Admin".equalsIgnoreCase(user.getRole()) ? "selected" : ""%>>Admin</option>
                            <option value="Lecturer" <%= "Lecturer".equalsIgnoreCase(user.getRole()) ? "selected" : ""%>>Lecturer</option>
                            <option value="Student" <%= "Student".equalsIgnoreCase(user.getRole()) ? "selected" : ""%>>Student</option>
                        </select>

                        <div class="role-fields staff-fields" style="display:none;">
                            <label for="staffNumber">Staff Number:</label>
                            <input type="text" id="staffNumber" name="staffNumber" value="<%= HtmlUtil.escape(user.getStaffNumber() != null ? user.getStaffNumber() : "")%>" />

                            <label for="department">Department:</label>
                            <input type="text" id="department" name="department" value="<%= HtmlUtil.escape(user.getDepartment() != null ? user.getDepartment() : "")%>" />
                        </div>

                        <div class="role-fields student-fields" style="display:none;">
                            <label for="studentNumber">Student ID:</label>
                            <input type="text" id="studentNumber" name="studentNumber" value="<%= HtmlUtil.escape(user.getStudentNumber() != null ? user.getStudentNumber() : "")%>" />

                            <label for="major">Major:</label>
                            <select id="major" name="major" required>
                                <option value="">-- Select Major --</option>
                                <%
                                    List<Course> coursesList = (List<Course>) request.getAttribute("courses");
                                    for (Course course : coursesList) {
                                        String selected = course.getCourseName().equals(user.getMajor()) ? "selected" : "";
                                %>
                                <option value="<%= HtmlUtil.escape(course.getCourseName())%>" <%= selected%>><%= HtmlUtil.escape(course.getCourseName())%></option>
                                <%
                                    }
                                %>
                            </select>
                        </div>

                        <input type="submit" value="Update User" style="margin-top: 20px;" />
                    </form>
                </div>
            </div>
        </main>

        <%@ include file="../WEB-INF/jspf/footer.jspf" %>

        <script>
            function toggleRoleFields(role) {
                const staffFields = document.querySelector('.staff-fields');
                const studentFields = document.querySelector('.student-fields');
                if (role === 'Admin' || role === 'Lecturer') {
                    staffFields.style.display = '';
                    studentFields.style.display = 'none';
                } else if (role === 'Student') {
                    staffFields.style.display = 'none';
                    studentFields.style.display = '';
                } else {
                    staffFields.style.display = 'none';
                    studentFields.style.display = 'none';
                }
            }

            document.addEventListener('DOMContentLoaded', () => {
                const roleSelect = document.getElementById('editRole');
                toggleRoleFields(roleSelect.value);
                roleSelect.addEventListener('change', () => {
                    toggleRoleFields(roleSelect.value);
                });

                document.getElementById('closeModal').addEventListener('click', () => {
                    window.location.href = 'ManageUsers.jsp';
                });

                window.addEventListener('click', (event) => {
                    const modal = document.getElementById('editUserModal');
                    if (event.target === modal) {
                        window.location.href = 'ManageUsers.jsp';
                    }
                });
            });
        </script>
    </body>
</html>
