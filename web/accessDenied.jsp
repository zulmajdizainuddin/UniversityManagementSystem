<%-- 
    Document   : accessDenied
    Created on : 18 Jun 2025, 7:47:14 pm
    Author     : NuNa
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8" />
        <title>Access Denied</title>
        <link rel="icon" href="<%=request.getContextPath()%>/images/graduation.png" sizes="32x32" type="image/png" />
        <link rel="stylesheet" href="<%=request.getContextPath()%>/css/common.css" />
        <link rel="stylesheet" href="<%=request.getContextPath()%>/css/layout.css" />
    </head>
    <body>
        <%@ include file="/WEB-INF/jspf/header.jspf" %>
        <main class="container">
            <h2>Access Denied</h2>
            <p>You do not have permission to access this page.</p>
            <a href="<%=request.getContextPath()%>/login.jsp" class="btn-link">Return to Login</a>
        </main>
        <%@ include file="/WEB-INF/jspf/footer.jspf" %>
    </body>
</html>

