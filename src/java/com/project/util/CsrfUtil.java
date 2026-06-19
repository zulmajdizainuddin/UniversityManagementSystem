package com.project.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.UUID;

public final class CsrfUtil {

    public static final String TOKEN_KEY = "csrfToken";

    private CsrfUtil() {}

    public static String generateToken(HttpSession session) {
        String token = UUID.randomUUID().toString();
        session.setAttribute(TOKEN_KEY, token);
        return token;
    }

    public static boolean isValidToken(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return false;
        String sessionToken = (String) session.getAttribute(TOKEN_KEY);
        String requestToken = request.getParameter(TOKEN_KEY);
        return sessionToken != null && sessionToken.equals(requestToken);
    }
}
