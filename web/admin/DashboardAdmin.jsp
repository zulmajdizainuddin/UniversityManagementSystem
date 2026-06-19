<%-- 
    Document   : DashboardAdmin
    Created on : Jun 12, 2025, 5:39:53 AM
    Author     : ZULMAJDI
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.project.dao.*"%>
<%@page import="java.util.*"%>
<%@page import="com.project.util.HtmlUtil"%>
<%
    com.project.model.User user = (com.project.model.User) session.getAttribute("user");
    if (user == null || !"Admin".equalsIgnoreCase(user.getRole())) {
        response.sendRedirect("../login.jsp");
        return;
    }
    request.setAttribute("headerTitle", "University Management System - Admin Dashboard");

    UserDAO usersDAO = new UserDAO();
    CoursesDAO coursesDAO = new CoursesDAO();
    SubjectDAO subjectDAO = new SubjectDAO();
    ClassesDAO classesDAO = new ClassesDAO();
    StaffDAO staffDAO = new StaffDAO();

    int totalUsers = usersDAO.getTotalUsers();
    int totalCourses = coursesDAO.getAllCourses().size();
    int totalSubjects = subjectDAO.getAllSubjects().size();
    int totalClasses = classesDAO.getAllClasses().size();
    int totalLecturers = staffDAO.getAllLecturers().size();
%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8" />
        <title>Admin Dashboard</title>
        <link rel="icon" href="<%=request.getContextPath()%>/images/graduation.png" sizes="32x32" type="image/png" />
        <link rel="stylesheet" href="<%=request.getContextPath()%>/css/layout.css" />
        <link rel="stylesheet" href="<%=request.getContextPath()%>/css/common.css" />
        <link rel="stylesheet" href="<%=request.getContextPath()%>/css/dashboard.css" />
    </head>
    <body>
        <%@ include file="../WEB-INF/jspf/header.jspf" %>

        <main class="container">
            <h2>Welcome, <%= HtmlUtil.escape(user.getName())%>!</h2>

            <section class="dashboard-container" aria-label="Summary statistics">
                <div class="widget" tabindex="0" role="region" aria-labelledby="usersCount">
                    <h3 id="usersCount"><%= totalUsers%></h3>
                    <p>Total Users</p>
                </div>
                <div class="widget" tabindex="0" role="region" aria-labelledby="coursesCount">
                    <h3 id="coursesCount"><%= totalCourses%></h3>
                    <p>Total Courses</p>
                </div>
                <div class="widget" tabindex="0" role="region" aria-labelledby="subjectsCount">
                    <h3 id="subjectsCount"><%= totalSubjects%></h3>
                    <p>Total Subjects</p>
                </div>
                <div class="widget" tabindex="0" role="region" aria-labelledby="classesCount">
                    <h3 id="classesCount"><%= totalClasses%></h3>
                    <p>Total Classes</p>
                </div>
                <div class="widget" tabindex="0" role="region" aria-labelledby="lecturersCount">
                    <h3 id="lecturersCount"><%= totalLecturers%></h3>
                    <p>Total Lecturers</p>
                </div>
            </section>

            <nav aria-label="Admin navigation">
                <ul>
                    <li><a href="ManageUsers.jsp">Manage Users</a></li>
                    <li>
                        <span tabindex="0" class="toggle-submenu" aria-expanded="false" aria-controls="coursesSubmenu" role="button">Courses & Subjects</span>
                        <ul id="coursesSubmenu" aria-hidden="true">
                            <li><a href="ManageCourses.jsp">Manage Courses</a></li>
                            <li><a href="ManageSubjects.jsp">Manage Subjects</a></li>
                            <li><a href="ManageClasses.jsp">Manage Classes</a></li>
                        </ul>
                    </li>
                    <li>
                        <span tabindex="0" class="toggle-submenu" aria-expanded="false" aria-controls="lecturerSubmenu" role="button">Lecturer Assignments</span>
                        <ul id="lecturerSubmenu" aria-hidden="true">
                            <li><a href="ManageLecturerClasses.jsp">Assign Lecturers to Classes</a></li>
                            <li><a href="ManageLecturerSubjects.jsp">Assign Lecturers to Subjects</a></li>
                        </ul>
                    </li>
                    <li><a href="AssignStudent.jsp">Assign Students to Subjects & Classes</a></li>
                    <li><a href="<%= request.getContextPath()%>/AdminAttendanceReportServlet?action=subjectView">Attendance Analysis Report</a></li>
                    <li><a href="<%= request.getContextPath()%>/AdminEvaluationQuestionServlet">Manage Evaluation Questions</a></li>
                    <li><a href="<%= request.getContextPath()%>/LogoutServlet">Logout</a></li>
                </ul>
            </nav>
        </main>

        <%@ include file="../WEB-INF/jspf/footer.jspf" %>

        <script>
            // Toggle submenu visibility and ARIA attributes
            document.querySelectorAll('.toggle-submenu').forEach(button => {
                button.addEventListener('click', () => {
                    const submenu = document.getElementById(button.getAttribute('aria-controls'));
                    const isExpanded = button.getAttribute('aria-expanded') === 'true';
                    button.setAttribute('aria-expanded', !isExpanded);
                    if (submenu) {
                        submenu.classList.toggle('show');
                        submenu.setAttribute('aria-hidden', isExpanded);
                    }
                });
                button.addEventListener('keydown', e => {
                    if (e.key === 'Enter' || e.key === ' ') {
                        e.preventDefault();
                        button.click();
                    }
                });
            });
        </script>
    </body>
</html>
