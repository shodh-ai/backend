package com.shodhAI.ShodhAI.Controller;

import com.shodhAI.ShodhAI.Component.ApiConstants;
import com.shodhAI.ShodhAI.Entity.Role;
import com.shodhAI.ShodhAI.Service.ExceptionHandlingService;
import com.shodhAI.ShodhAI.Service.ResponseService;
import com.shodhAI.ShodhAI.Service.RoleService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/auth")
public class RoleSelectionController {

    @Autowired
    private RoleService roleService;
    
    @Autowired
    private ExceptionHandlingService exceptionHandlingService;
    
    @Autowired
    private ResponseService responseService;
    
    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;
    
    @GetMapping("/oauth2/google/authorize")
    public ResponseEntity<?> authorizeGoogle(@RequestParam("roleId") Long roleId, 
                                           @RequestParam(value = "redirect_uri", required = false) String redirectUri,
                                           HttpServletResponse response,
                                           HttpSession session) {
        try {
            // Validate role
            Role role = roleService.getRoleById(roleId);
            if (role == null) {
                return responseService.generateErrorResponse(ApiConstants.INVALID_ROLE, HttpStatus.BAD_REQUEST);
            }
            
            // Store role in session
            session.setAttribute("selected_role_id", roleId);
            
            // If redirect_uri is provided, store it in a cookie
            if (redirectUri != null && !redirectUri.isEmpty()) {
                Cookie redirectUriCookie = new Cookie("redirect_uri", redirectUri);
                redirectUriCookie.setPath("/");
                redirectUriCookie.setMaxAge(180); // 3 minutes
                response.addCookie(redirectUriCookie);
            }
            
            // Construct the OAuth2 authorization URL
            String authorizationUrl = UriComponentsBuilder.fromHttpUrl("https://accounts.google.com/o/oauth2/v2/auth")
                    .queryParam("client_id", googleClientId)
                    .queryParam("response_type", "code")
                    .queryParam("scope", "email profile")
                    .queryParam("redirect_uri", "http://localhost:8080/oauth2/callback/google")
                    .queryParam("state", session.getId())
                    .build().toUriString();
            
            return ResponseService.generateSuccessResponse("Url is generated for performing google authentication",authorizationUrl,HttpStatus.OK);
            
        } catch (IllegalArgumentException illegalArgumentException) {
            exceptionHandlingService.handleException(illegalArgumentException);
            return ResponseService.generateErrorResponse(illegalArgumentException.getMessage(),HttpStatus.BAD_REQUEST);
        }
        catch (Exception e) {
            exceptionHandlingService.handleException(e);
            return responseService.generateErrorResponse(ApiConstants.SOME_EXCEPTION_OCCURRED + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}