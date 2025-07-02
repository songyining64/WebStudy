package com.cupk.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
public class HttpMethodConfig {

    @Bean
    public FilterRegistrationBean<RequestDebugFilter> requestDebugFilter() {
        FilterRegistrationBean<RequestDebugFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new RequestDebugFilter());
        registration.addUrlPatterns("/*");
        registration.setOrder(0);
        return registration;
    }

    public class RequestDebugFilter extends OncePerRequestFilter {
        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                FilterChain filterChain)
                throws ServletException, IOException {
            // 设置CORS头
            response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
            response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, PATCH");
            response.setHeader("Access-Control-Allow-Headers",
                    "Authorization, Content-Type, X-User-Role, X-Auth-Token, Origin, Accept");
            response.setHeader("Access-Control-Allow-Credentials", "true");
            response.setHeader("Access-Control-Max-Age", "3600");

            // 处理预检请求
            if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
                response.setStatus(HttpServletResponse.SC_OK);
                return;
            }

            // 记录所有请求信息，便于调试
            System.out.println("------------------------------------------");
            System.out.println("处理请求: " + request.getMethod() + " " + request.getRequestURI());
            System.out.println("X-User-Role: " + request.getHeader("X-User-Role"));
            System.out.println("Authorization: " + request.getHeader("Authorization"));
            System.out.println("Content-Type: " + request.getHeader("Content-Type"));
            System.out.println("------------------------------------------");

            filterChain.doFilter(request, response);
        }
    }
}