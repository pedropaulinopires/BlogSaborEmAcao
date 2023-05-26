package com.saboremacao.blog.interceptors;

import com.saboremacao.blog.service.CookieService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AdmInterceptors implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try {
            if (CookieService.getCookie(request, "admAuthenticated") != null) {
                return true;
            } else {
                response.sendRedirect("/");
                return false;
            }
        } catch (Exception e) {
            response.sendRedirect("/");
            return false;
        }

    }
}