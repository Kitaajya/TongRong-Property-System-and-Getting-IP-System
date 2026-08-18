package org.designer.tongrong_property_company_2nd.auth;

import org.designer.tongrong_property_company_2nd.common.PermissionInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor())
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/auth/login", "/api/auth/register", "/api/auth/logout",
                        "/api/auth/status", "/api/auth/send-code");

        registry.addInterceptor(new PermissionInterceptor())
                .addPathPatterns("/api/**");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }
}
