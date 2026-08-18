package org.designer.tongrong_property_company_2nd.common;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.nio.charset.StandardCharsets;

public class PermissionInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        if (!(handler instanceof HandlerMethod handlerMethod)) return true;

        MerchantOnly annotation = handlerMethod.getMethodAnnotation(MerchantOnly.class);
        if (annotation == null) return true;

        String role = SessionHelper.currentRole(request);
        if ("merchant".equals(role)) return true;

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");
        byte[] body = "{\"success\":false,\"message\":\"仅商家可进行商品增删改操作\"}"
                .getBytes(StandardCharsets.UTF_8);
        response.setContentLength(body.length);
        response.getOutputStream().write(body);
        return false;
    }
}
