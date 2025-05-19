package com.cams.user_service.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class RoleAuthorizationFilter implements Filter {

    private static final Map<String, Set<String>> ENDPOINT_ROLE_MAP = new HashMap<>();
    private static final Set<String> PUBLIC_ENDPOINTS = Set.of(
        "GET:/api/users/validate",
        "GET:/api/users/email"
    );

    static {
        // Example: Only ADMIN can GET all users
        ENDPOINT_ROLE_MAP.put("GET:/api/users", Set.of("ADMIN"));
        // Example: Only ADMIN can DELETE users
        ENDPOINT_ROLE_MAP.put("DELETE:/api/users", Set.of("ADMIN"));
        // Example: All roles can GET their own user info
        ENDPOINT_ROLE_MAP.put("GET:/api/users/me", Set.of("ADMIN", "LECTURER", "STUDENT"));
        // Add more endpoint/role mappings as needed
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String method = httpRequest.getMethod();
        String path = httpRequest.getRequestURI();
        String key = method + ":" + path;

        // Skip role check for public endpoints
        if (PUBLIC_ENDPOINTS.stream().anyMatch(endpoint -> path.startsWith(endpoint.substring(endpoint.indexOf(":"))))) {
            chain.doFilter(request, response);
            return;
        }

        String userRole = httpRequest.getHeader("X-User-Role");

        if (ENDPOINT_ROLE_MAP.containsKey(key)) {
            Set<String> allowedRoles = ENDPOINT_ROLE_MAP.get(key);
            if (userRole == null || !allowedRoles.contains(userRole)) {
                httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
                httpResponse.getWriter().write("Forbidden: Insufficient role");
                return;
            }
        }
        chain.doFilter(request, response);
    }
} 