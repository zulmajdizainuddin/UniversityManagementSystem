/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.project.filter;

import com.project.model.User;
import com.project.util.Roles;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.*;
import java.io.IOException;

import java.util.*;

/**
 *
 * @author NuNa
 */

/**
 * AuthFilter intercepts requests to secure pages and enforces: - User login
 * (session check) - Role-based access control by URL pattern
 */
@WebFilter(urlPatterns = {"/admin/*", "/lecturer/*", "/student/*"})
public class AuthFilter implements Filter {

    // Map user roles to allowed URL prefixes
    private static final Map<String, List<String>> roleAccessMap = new HashMap<>();

    static {
        roleAccessMap.put(Roles.ADMIN, Collections.singletonList("/admin/"));
        roleAccessMap.put(Roles.LECTURER, Collections.singletonList("/lecturer/"));
        roleAccessMap.put(Roles.STUDENT, Collections.singletonList("/student/"));
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // No init needed
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String contextPath = request.getContextPath();
        String uri = request.getRequestURI();

        // Allow public resources and login page without authentication
        if (uri.startsWith(contextPath + "/css/")
                || uri.startsWith(contextPath + "/js/")
                || uri.startsWith(contextPath + "/images/")
                || uri.endsWith("login.jsp")
                || uri.endsWith("logout")
                || uri.endsWith("LoginServlet")) {
            chain.doFilter(req, res);
            return;
        }

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("user") == null) {
            // Not logged in - redirect to login page
            response.sendRedirect(contextPath + "/login.jsp");
            return;
        }

        User user = (User) session.getAttribute("user");
        String role = user.getRole();

        List<String> allowedPaths = roleAccessMap.get(role);
        boolean authorized = false;

        if (allowedPaths != null) {
            for (String path : allowedPaths) {
                if (uri.startsWith(contextPath + path)) {
                    authorized = true;
                    break;
                }
            }
        }

        if (!authorized) {
            // User logged in but trying to access unauthorized page
            response.sendRedirect(contextPath + "/accessDenied.jsp");
            return;
        }

        // User is authorized, continue request
        chain.doFilter(req, res);
    }

    @Override
    public void destroy() {
        // No cleanup needed
    }
}
