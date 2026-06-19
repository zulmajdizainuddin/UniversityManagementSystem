<%-- 
    Document   : ManageClasses
    Created on : 17 Jun 2025, 14:17:03
    Author     : nabil
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.project.dao.ClassesDAO"%>
<%@page import="com.project.dao.SubjectDAO"%>
<%@page import="com.project.model.Class"%>
<%@page import="com.project.model.Subject"%>
<%@page import="java.util.List"%>
<%@page import="com.project.util.HtmlUtil"%>
<%
    if (session.getAttribute("user") == null) {
        response.sendRedirect("../login.jsp");
        return;
    }
    request.setAttribute("headerTitle", "University Management System - Manage Classes");

    // Load Subject for dropdown
    SubjectDAO subjectsDAO = new SubjectDAO();
    List<Subject> subjectList = subjectsDAO.getAllSubjects();

    ClassesDAO classesDAO = new ClassesDAO();
    List<Class> classList = classesDAO.getAllClasses();
%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8" />
        <title>Manage Classes</title>
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
            <h2>Manage Classes</h2>

            <a href="DashboardAdmin.jsp" class="btn-link" style="margin-bottom: 10px;">Home</a>

            <button id="openClassModal" class="btn-primary" style="margin-bottom: 15px;">Add Class</button>

            <%-- Messages --%>
            <%                String errorMessage = (String) session.getAttribute("errorMessage");
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
            <div id="classModal" class="modal" aria-hidden="true" role="dialog" aria-labelledby="classModalTitle" tabindex="-1">
                <div class="modal-content" role="document">
                    <button class="close" aria-label="Close Class Form">&times;</button>
                    <h3 id="classModalTitle">Add Class</h3>
                    <form id="classForm" action="<%= request.getContextPath()%>/ClassServlet" method="post" novalidate>
                        <input type="hidden" name="action" value="add" />
                        <input type="hidden" name="classId" id="classId" value="" />
                        <input type="hidden" name="csrfToken" value="<%= session.getAttribute("csrfToken") %>" />

                        <label for="className">Class Name:</label>
                        <input type="text" id="className" name="className" required />

                        <label for="subjectId">Subject:</label>
                        <select id="subjectId" name="subjectId" required>
                            <option value="">-- Select Subject --</option>
                            <% for (Subject subject : subjectList) {%>
                            <option value="<%= subject.getSubjectId()%>"><%= subject.getSubjectName()%></option>
                            <% } %>
                        </select>

                        <input type="submit" value="Save" />
                    </form>
                </div>
            </div>

            <hr />

            <h3>Existing Classes:</h3>

            <table class="data-table" aria-label="Existing Classes">
                <thead>
                    <tr>
                        <th>No.</th><th>Class Name</th><th>Subject</th><th>Action</th>
                    </tr>
                </thead>
                <tbody>
                    <%
                        int count = 1;
                        java.util.Map<Integer, String> subjectMap = new java.util.HashMap<>();
                        for (Subject c : subjectList) {
                            subjectMap.put(c.getSubjectId(), c.getSubjectName());
                        }

                        for (Class c : classList) {
                    %>
                    <tr>
                        <td><%= count++%></td>
                        <td><%= HtmlUtil.escape(c.getClassName())%></td>
                        <td><%= subjectMap.get(c.getSubjectId()) != null ? HtmlUtil.escape(subjectMap.get(c.getSubjectId())) : "N/A"%></td>
                        <td>
                            <button class="btn-link edit-btn"
                                    data-id="<%= c.getClassId()%>"
                                    data-name="<%= HtmlUtil.escape(c.getClassName())%>"
                                    data-subjectid="<%= c.getSubjectId()%>">Edit</button>
                            <a href="<%= request.getContextPath()%>/ClassServlet?action=delete&id=<%= c.getClassId()%>" class="btn-link delete" onclick="return confirm('Are you sure you want to delete this class?');">Delete</a>
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
            const modal = document.getElementById('classModal');
            const openBtn = document.getElementById('openClassModal');
            const closeBtn = modal.querySelector('.close');
            const form = document.getElementById('classForm');
            const modalTitle = document.getElementById('classModalTitle');
            const classIdInput = document.getElementById('classId');
            const classNameInput = document.getElementById('className');
            const subjectIdSelect = document.getElementById('subjectId');

            openBtn.addEventListener('click', () => {
                modal.style.display = 'block';
                modal.setAttribute('aria-hidden', 'false');
                modalTitle.textContent = 'Add Class';
                form.action.value = 'add';
                classIdInput.value = '';
                classNameInput.value = '';
                subjectIdSelect.value = '';
                classNameInput.focus();
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
                    modalTitle.textContent = 'Edit Class';
                    form.action.value = 'update';
                    classIdInput.value = button.dataset.id;
                    classNameInput.value = button.dataset.name;
                    subjectIdSelect.value = button.dataset.subjectid;
                    classNameInput.focus();
                });
            });
        </script>

    </body>
</html>
