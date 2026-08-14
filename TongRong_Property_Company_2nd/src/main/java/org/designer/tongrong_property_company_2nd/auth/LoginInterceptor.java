package org.designer.tongrong_property_company_2nd.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

import java.nio.charset.StandardCharsets;

/**
 * 登录拦截器：未登录的请求一律返回 401，无法使用任何查询 / 购买功能
 */
public class LoginInterceptor implements HandlerInterceptor {

    private static final String SESSION_USER_KEY = "loginUser";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute(SESSION_USER_KEY) != null) {
            return true;
        }
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        byte[] body = "{\"success\":false,\"message\":\"未登录，请先登录后再进行查询与购买\"}"
                .getBytes(StandardCharsets.UTF_8);
        response.setContentLength(body.length);
        response.getOutputStream().write(body);
        return false;
    }
}
