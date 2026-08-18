package org.designer.tongrong_property_company_2nd.common;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.util.Map;

public class SessionHelper {

    private static final String SESSION_USER_KEY = "loginUser";

    private SessionHelper() {}

    @SuppressWarnings("unchecked")
    private static Map<?, ?> loginUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return null;
        Object attr = session.getAttribute(SESSION_USER_KEY);
        return attr instanceof Map<?, ?> m ? m : null;
    }

    public static String currentRole(HttpServletRequest request) {
        Map<?, ?> user = loginUser(request);
        if (user == null) return null;
        Object roleObj = user.get("role");
        String role = (roleObj == null || String.valueOf(roleObj).isBlank())
                ? (Boolean.TRUE.equals(user.get("isOrdinaryUser")) ? "merchant" : "user")
                : String.valueOf(roleObj);
        return role;
    }

    public static String currentUsername(HttpServletRequest request) {
        Map<?, ?> user = loginUser(request);
        if (user == null) return null;
        Object u = user.get("username");
        return u == null ? null : String.valueOf(u);
    }

    public static boolean isLoggedIn(HttpServletRequest request) {
        return loginUser(request) != null;
    }

    @SuppressWarnings("unchecked")
    public static void updateUsername(HttpServletRequest request, String newName) {
        HttpSession session = request.getSession(false);
        if (session == null) return;
        Object attr = session.getAttribute(SESSION_USER_KEY);
        if (attr instanceof Map<?, ?> old) {
            Map<String, Object> updated = new java.util.HashMap<>((Map<String, Object>) old);
            updated.put("username", newName);
            session.setAttribute(SESSION_USER_KEY, updated);
        }
    }
}
