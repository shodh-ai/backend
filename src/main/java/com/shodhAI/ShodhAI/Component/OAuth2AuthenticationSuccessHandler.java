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

@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Value("${app.oauth2.authorized-redirect-uri}")
    private String defaultRedirectUri;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String targetUrl = determineTargetUrl(request, response, authentication);

        if (response.isCommitted()) {
            logger.debug("Response has already been committed. Unable to redirect to " + targetUrl);
            return;
        }

        clearAuthenticationAttributes(request, response);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        Optional<String> redirectUri = getCookieValue(request, "redirect_uri");

        String targetUrl = redirectUri.orElse(defaultRedirectUri);

        // Get user details from the authentication
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        // Check if this was a signup or login
        HttpSession session = request.getSession(false);
        boolean isSignup = session != null && Boolean.TRUE.equals(session.getAttribute("is_signup"));

        // Clear the signup attribute
        if (session != null) {
            session.removeAttribute("is_signup");
            session.removeAttribute("selected_role_id");
        }

        // Generate JWT token
        String token = jwtUtil.generateToken(userPrincipal.getId(),
                getRoleId(userPrincipal.getRole()),
                request.getRemoteAddr(),
                request.getHeader("User-Agent"));

        // Add parameters to the redirect URL
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("token", token)
                .queryParam("userId", userPrincipal.getId());

        // Add email if available
        if (userPrincipal.getEmail() != null) {
            builder.queryParam("email", userPrincipal.getEmail());
        }

        // Add name if available
        String name = (String) userPrincipal.getAttributes().get("name");
        if (name != null) {
            builder.queryParam("name", name);
        }

        // Add signup flag if applicable
        if (isSignup) {
            builder.queryParam("isNewUser", "true");
        }

        return builder.build().toUriString();
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