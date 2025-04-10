package com.shodhAI.ShodhAI.Component;
import com.shodhAI.ShodhAI.Entity.UserPrincipal;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.Optional;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Value("${app.oauth2.authorized-redirect-uri}")
    private String defaultRedirectUri;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // Get user details from the authentication
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        // Generate JWT token
        String token = jwtUtil.generateToken(userPrincipal.getId(),
                getRoleId(userPrincipal.getRole()),
                request.getRemoteAddr(),
                request.getHeader("User-Agent"));

        // Get additional user information to pass in the URL
        Long userId = userPrincipal.getId();
        String email = userPrincipal.getEmail();
        String name = userPrincipal.getName() != null ? userPrincipal.getName() : userId.toString();

        // Build redirect URL with token and user info as query parameters
        String targetUrl = UriComponentsBuilder.fromUriString(defaultRedirectUri)
                .queryParam("token", token)
                .queryParam("userId", userId)
                .queryParam("email", URLEncoder.encode(email, StandardCharsets.UTF_8))
                .queryParam("name", URLEncoder.encode(name, StandardCharsets.UTF_8))
                .build().toUriString();

        // Clear cookies
        clearAuthenticationAttributes(request, response);

        // Redirect to frontend
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    private Long getRoleId(String roleName) {
        // Map role name to ID - adjust this based on your role IDs
        if (Constant.ROLE_FACULTY.equals(roleName)) {
            return 3L; // Faculty role ID
        } else {
            return 4L; // Student role ID
        }
    }

    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        // Clear cookies
        Cookie cookie = new Cookie("redirect_uri", "");
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    private Optional<String> getCookieValue(HttpServletRequest request, String name) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals(name)) {
                    return Optional.of(cookie.getValue());
                }
            }
        }
        return Optional.empty();
    }
}