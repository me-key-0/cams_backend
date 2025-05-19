package com.cams.grade_service.filter;

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
    static {
        // Example: Only LECTURER and ADMIN can POST grades
        ENDPOINT_ROLE_MAP.put("POST:/api/grades", Set.of("LECTURER", "ADMIN"));
        // Example: Only ADMIN can DELETE grades
        ENDPOINT_ROLE_MAP.put("DELETE:/api/grades", Set.of("ADMIN"));
        // Example: All roles can GET grades
        ENDPOINT_ROLE_MAP.put("GET:/api/grades", Set.of("ADMIN", "LECTURER", "STUDENT"));
        // Add more endpoint/role mappings as needed
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String method = httpRequest.getMethod();
        String path = httpRequest.getRequestURI();
        String key = method + ":" + path;
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