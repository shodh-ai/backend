package com.shodhAI.ShodhAI.Component;

import com.shodhAI.ShodhAI.Entity.Faculty;
import com.shodhAI.ShodhAI.Entity.Student;
import com.shodhAI.ShodhAI.Service.ExceptionHandlingService;
import com.shodhAI.ShodhAI.Service.RoleService;
import com.shodhAI.ShodhAI.Service.StudentService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.persistence.EntityManager;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * The type Jwt authentication filter.
 */
@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = Constant.BEARER;
    private static final int BEARER_PREFIX_LENGTH = BEARER_PREFIX.length();
    private static final Pattern UNSECURED_URI_PATTERN = Pattern.compile(
            "^/api/v1/(account|otp|test|files/avisoftdocument/[^/]+/[^/]+|files/[^/]+|avisoftdocument/[^/]+|swagger-ui.html|swagger-resources|v2/api-docs|images|webjars).*"
    );
    private String apiKey = "IaJGL98yHnKjnlhKshiWiy1IhZ+uFsKnktaqFX3Dvfg=";

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private StudentService studentService;
    @Autowired
    private RoleService roleService;

    /**
     * The Token blacklist.
     */
    @Autowired
    TokenBlacklist tokenBlacklist;

    @Autowired
    private ExceptionHandlingService exceptionHandling;

    @Autowired
    private EntityManager entityManager;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        Constant.request = request;
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }
        try {

            String requestURI = request.getRequestURI();
            if (isUnsecuredUri(requestURI) || bypassimages(requestURI)) {
                chain.doFilter(request, response);
                return;
            }
            if (isApiKeyRequiredUri(request) && validateApiKey(request)) {
                chain.doFilter(request, response);
                return;
            }
            /*if((checkRole(requestURI,request)).equals(false))
                throw new AccessDeniedException("Access not granted");*/
            boolean responseHandled = authenticateUser(request, response);
            if (!responseHandled) {
                chain.doFilter(request, response);
            } else {
                return;
            }

        } catch (AccessDeniedException accessDeniedException) {
            handleException(response, HttpServletResponse.SC_UNAUTHORIZED, accessDeniedException.getMessage());
        } catch (ExpiredJwtException e) {
            handleException(response, HttpServletResponse.SC_UNAUTHORIZED, "JWT token is expired");
            log.error("ExpiredJwtException caught: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            handleException(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token");
            exceptionHandling.handleException(e);
            log.error("MalformedJwtException caught: {}", e.getMessage());
        } catch (Exception e) {
            handleException(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            exceptionHandling.handleException(e);
            log.error("Exception caught: {}", e.getMessage());
        }
    }

    private boolean bypassimages(String requestURI) {
        return UNSECURED_URI_PATTERN.matcher(requestURI).matches();
    }

    private boolean isApiKeyRequiredUri(HttpServletRequest request) {

        String requestURI = request.getRequestURI();
        String path = requestURI.split("\\?")[0].trim();

        List<Pattern> bypassPatterns = Arrays.asList(
                Pattern.compile("^/api/v1/category-custom/get-products-by-category-id/\\d+$"),
                Pattern.compile("^/api/v1/category-custom/get-all-categories$")
        );

        boolean isBypassed = bypassPatterns.stream().anyMatch(pattern -> pattern.matcher(path).matches());
        return isBypassed;
    }

    private boolean validateApiKey(HttpServletRequest request) {
        String requestApiKey = request.getHeader("x-api-key");
        return apiKey.equals(requestApiKey);
    }

    private boolean isUnsecuredUri(String requestURI) {
        return requestURI.startsWith("/api/v1/auth")
                | requestURI.startsWith("/api/v1/faculty/add")
                | requestURI.startsWith("/api/v1/student/add");
    }

    private boolean authenticateUser(HttpServletRequest request, HttpServletResponse response) throws Exception {
        final String authorizationHeader = request.getHeader(AUTHORIZATION_HEADER);
        if (authorizationHeader == null || !authorizationHeader.startsWith(BEARER_PREFIX)) {
            respondWithUnauthorized(response, "JWT token cannot be empty");
            return true;
        }

        if (studentService == null) {
            respondWithUnauthorized(response, "StudentService is null");
            return true;
        }

        String jwt = authorizationHeader.substring(BEARER_PREFIX_LENGTH);
        Long id = jwtUtil.extractId(jwt);

        if (tokenBlacklist.isTokenBlacklisted(jwt)) {
            respondWithUnauthorized(response, "Token has been blacklisted");
            return true;
        }

        if (id == null) {
            respondWithUnauthorized(response, "Invalid details in token");
            return true;
        }

        String ipAddress = request.getRemoteAddr();
        String User_Agent = request.getHeader("User-Agent");

        try {
            if (!jwtUtil.validateToken(jwt, ipAddress, User_Agent)) {
                respondWithUnauthorized(response, "Invalid JWT token");
                return true;
            }
        } catch (ExpiredJwtException e) {
            jwtUtil.logoutUser(jwt);
            respondWithUnauthorized(response, "Token is expired");
            return true;
        }

        Student student = null;
        Faculty faculty = null;
//        Admin admin = null;

        if (id != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            if (roleService.findRoleNameById(jwtUtil.extractRoleId(jwt)).equals(Constant.ROLE_USER)) {
                student = studentService.getStudentById(id);
                if (student != null && jwtUtil.validateToken(jwt, ipAddress, User_Agent)) {
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            student.getId(), null, new ArrayList<>());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    return false;
                } else {
                    jwtUtil.logoutUser(jwt);

                    respondWithUnauthorized(response, "Invalid data provided for this student");
                    return true;
                }
            } else if (roleService.findRoleNameById(jwtUtil.extractRoleId(jwt)).equals(Constant.ROLE_FACULTY)) {
                faculty = entityManager.find(Faculty.class, id);
                if (faculty != null && jwtUtil.validateToken(jwt, ipAddress, User_Agent)) {
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            faculty.getId(), null, new ArrayList<>());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    return false;
                } else {
                    respondWithUnauthorized(response, "Invalid data provided for this customer");
                    return true;
                }
            }
//            else if (roleService.findRoleName(jwtUtil.extractRoleId(jwt)).equals(Constant.ADMIN) || roleService.findRoleName(jwtUtil.extractRoleId(jwt)).equals(Constant.SUPER_ADMIN) || roleService.findRoleName(jwtUtil.extractRoleId(jwt)).equals(Constant.roleAdminServiceProvider)) {
//                customAdmin = entityManager.find(CustomAdmin.class, id);
//                if (customAdmin != null && jwtUtil.validateToken(jwt, ipAdress, User_Agent)) {
//                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
//                            customAdmin.getAdmin_id(), null, new ArrayList<>());
//                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//                    SecurityContextHolder.getContext().setAuthentication(authentication);
//                    return false;
//                } else {
//                    respondWithUnauthorized(response, "Invalid data provided for this user");
//                    return true;
//                }
//            }
        }
        return false;
    }

    private void respondWithUnauthorized(HttpServletResponse response, String message) throws IOException {
        if (!response.isCommitted()) {

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"status\":\"UNAUTHORIZED\",\"status_code\":401,\"message\":\"" + message + "\"}");
            response.getWriter().flush();
        }
    }

    private void handleException(HttpServletResponse response, int statusCode, String message) throws IOException {
        if (!response.isCommitted()) {
            response.setStatus(statusCode);
            response.setContentType("application/json");

            String status;
            if (statusCode == HttpServletResponse.SC_BAD_REQUEST) {
                status = "BAD_REQUEST";
            } else if (statusCode == HttpServletResponse.SC_UNAUTHORIZED) {
                status = "UNAUTHORIZED";
            } else {
                status = "ERROR";
            }

            String jsonResponse = String.format(
                    "{\"status\":\"%s\",\"status_code\":%d,\"message\":\"%s\"}",
                    status,
                    statusCode,
                    message
            );
            response.getWriter().write(jsonResponse);
            response.getWriter().flush();
        }
    }
}
