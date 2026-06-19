<%-- 
    Document   : login
    Created on : Jun 12, 2025, 5:21:19 AM
    Author     : ZULMAJDI
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8" />
        <title>Login - University Management System</title>
        <link rel="icon" href="<%=request.getContextPath()%>/images/graduation.png" sizes="32x32" type="image/png" />
        <link rel="stylesheet" href="<%=request.getContextPath()%>/css/layout.css" />
        <link rel="stylesheet" href="<%=request.getContextPath()%>/css/common.css" />
    </head>
    <body>

        <%@ include file="WEB-INF/jspf/header.jspf" %>

        <main>
            <div class="login-wrapper container" role="main" aria-labelledby="loginHeading">
                <div class="login-image" aria-hidden="true">
                    <img src="<%=request.getContextPath()%>/images/welcomeToUni.jpg" alt="Welcoming To Uni Image" />
                </div>
                <div class="login-form-container">
                    <h2 id="loginHeading">Login</h2>

                    <form action="LoginServlet" method="post" novalidate>
                        <label for="email">Email</label>
                        <input type="email" id="email" name="email" required autofocus />

                        <label for="password">Password</label>
                        <input type="password" id="password" name="password" required />

                        <input type="submit" value="Login" />
                    </form>

                    <% String error = request.getParameter("error");
                    if (error != null) { %>
                    <p class="error" role="alert" aria-live="assertive">
                        Invalid email or password!
                    </p>
                    <% }%>
                </div>
            </div>
        </main>

        <%@ include file="WEB-INF/jspf/footer.jspf" %>
    </body>
</html>
