// package com.cams.user_service.filter;

// import jakarta.servlet.Filter;
// import jakarta.servlet.FilterChain;
// import jakarta.servlet.ServletException;
// import jakarta.servlet.ServletRequest;
// import jakarta.servlet.ServletResponse;
// import jakarta.servlet.http.HttpServletRequest;
// import jakarta.servlet.http.HttpServletResponse;
// import org.springframework.stereotype.Component;

// import java.io.IOException;
// import java.util.HashMap;
// import java.util.Map;
// import java.util.Set;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;

// @Component
// public class RoleAuthorizationFilter implements Filter {

//     private static final Logger logger = LoggerFactory.getLogger(RoleAuthorizationFilter.class);
//     private static final Map<String, Set<String>> ENDPOINT_ROLE_MAP = new HashMap<>();
//     private static final Set<String> PUBLIC_ENDPOINTS = Set.of(
//         "GET:/api/users/validate",
//         "GET:/api/users/email"

//     );

//     static {
//         // Example: Only ADMIN can GET all users
//         ENDPOINT_ROLE_MAP.put("GET:/api/users", Set.of("LECTURER", "STUDENT"));
//         ENDPOINT_ROLE_MAP.put("GET:/api/users/lecturer/user/{userId}", Set.of("LECTURER", "STUDENT"));
//         // Example: Only ADMIN can DELETE users
//         ENDPOINT_ROLE_MAP.put("DELETE:/api/users", Set.of("ADMIN"));
//         // Example: All roles can GET their own user info
//         ENDPOINT_ROLE_MAP.put("GET:/api/users/me", Set.of("ADMIN", "LECTURER", "STUDENT"));
//         // Add more endpoint/role mappings as needed
//     }

//     @Override
//     public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
//         HttpServletRequest httpRequest = (HttpServletRequest) request;
//         HttpServletResponse httpResponse = (HttpServletResponse) response;

//         // Log incoming request details
//         String method = httpRequest.getMethod();
//         String path = httpRequest.getRequestURI();
//         String key = method + ":" + path;
//         String userRole = httpRequest.getHeader("X-User-Role");
//         String userId = httpRequest.getHeader("X-User-Id");
        
//         logger.debug("=== STARTING AUTHORIZATION CHECK ===");
//         logger.debug("Request Details:");
//         logger.debug("  Method: {}", method);
//         logger.debug("  Path: {}", path);
//         logger.debug("  Key: {}", key);
//         logger.debug("  User Role: {}", userRole);
//         logger.debug("  User ID: {}", userId);

//         // Check if endpoint is public
//         logger.debug("Checking if endpoint is public...");
//         boolean isPublic = PUBLIC_ENDPOINTS.stream().anyMatch(endpoint -> {
//             String endpointPath = endpoint.substring(endpoint.indexOf(":") + 1);
//             String regexPath = endpointPath.replaceAll("\\{.*?\\}", ".*");
//             boolean matches = path.matches("^" + regexPath + "$");
//             logger.debug("  Checking public endpoint: {} -> {}", endpoint, matches);
//             return matches;
//         });

//         if (isPublic) {
//             logger.debug("Endpoint is public, skipping role check");
//             chain.doFilter(request, response);
//             return;
//         }
//         logger.debug("Endpoint is not public, proceeding with role check");

//         // Find matching endpoint in role map
//         logger.debug("Looking for matching endpoint in role map...");
//         String matchingKey = ENDPOINT_ROLE_MAP.keySet().stream()
//             .filter(endpointKey -> {
//                 String keyPath = endpointKey.substring(endpointKey.indexOf(":") + 1);
//                 String regexPath = keyPath.replaceAll("\\{.*?\\}", ".*");
//                 boolean matches = path.matches("^" + regexPath + "$");
//                 logger.debug("  Checking endpoint: {} -> {}", endpointKey, matches);
//                 return matches;
//             })
//             .findFirst()
//             .orElse(null);

//         if (matchingKey == null) {
//             logger.warn("No matching endpoint found in role map for: {}", key);
//             logger.warn("Available endpoints: {}", ENDPOINT_ROLE_MAP.keySet());
//         } else {
//             logger.debug("Found matching endpoint: {}", matchingKey);
//         }

//         if (matchingKey != null) {
//             Set<String> allowedRoles = ENDPOINT_ROLE_MAP.get(matchingKey);
//             logger.debug("Allowed roles for endpoint {}: {}", matchingKey, allowedRoles);
            
//             if (userRole == null) {
//                 logger.warn("No user role provided in request headers");
//                 httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
//                 httpResponse.getWriter().write("Forbidden: No role provided");
//                 return;
//             }
            
//             if (!allowedRoles.contains(userRole)) {
//                 logger.warn("User role {} not allowed for endpoint {}", userRole, matchingKey);
//                 httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
//                 httpResponse.getWriter().write("Forbidden: Insufficient role");
//                 return;
//             }
//             logger.debug("User role {} authorized for endpoint {}", userRole, matchingKey);
//         }
//         logger.debug("=== AUTHORIZATION CHECK PASSED ===");
//         chain.doFilter(request, response);
//     }
// } 