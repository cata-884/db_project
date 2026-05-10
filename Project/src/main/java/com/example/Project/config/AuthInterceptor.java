package com.example.Project.config;

import com.example.Project.service.SesiuneService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Optional;
import java.util.Set;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Autowired
    private SesiuneService sesiuneService;

    private static final Set<String> RUTE_PUBLICE = Set.of(
        "/api/auth/login",
        "/api/auth/register"
    );

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(req.getMethod())) {
            return true;
        }

        String path = req.getRequestURI();

        if (RUTE_PUBLICE.stream().anyMatch(path::startsWith)) {
            return true;
        }

        if (!path.startsWith("/api/")) {
            return true;
        }

        String authHeader = req.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            res.setStatus(401);
            res.setContentType("application/json");
            res.getWriter().write("{\"error\":\"Token lipsa\"}");
            return false;
        }

        String token = authHeader.substring("Bearer ".length()).trim();
        Optional<Long> idClient = sesiuneService.validareToken(token);
        if (idClient.isEmpty()) {
            res.setStatus(401);
            res.setContentType("application/json");
            res.getWriter().write("{\"error\":\"Token invalid sau expirat\"}");
            return false;
        }

        req.setAttribute("idClientCurent", idClient.get());
        req.setAttribute("tokenCurent", token);
        return true;
    }
}
