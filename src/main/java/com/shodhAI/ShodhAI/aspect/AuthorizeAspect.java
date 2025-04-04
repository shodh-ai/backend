package com.shodhAI.ShodhAI.aspect;

import com.shodhAI.ShodhAI.Component.Constant;
import com.shodhAI.ShodhAI.Component.JwtUtil;
import com.shodhAI.ShodhAI.Service.ExceptionHandlingImplement;
import com.shodhAI.ShodhAI.Service.ResponseService;
import com.shodhAI.ShodhAI.Service.RoleService;
import com.shodhAI.ShodhAI.annotation.Authorize;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import java.util.Arrays;
import java.util.List;

@Aspect
@Component
public class AuthorizeAspect {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RoleService roleService;

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = Constant.BEARER_CONST;;
    private static final int BEARER_PREFIX_LENGTH = BEARER_PREFIX.length();

    // Pointcut to match methods annotated with @Authorize
    @Pointcut("@annotation(com.shodhAI.ShodhAI.annotation.Authorize)")

    public void authorizeMethods() {}

    // Advice runs before the annotated method executes
    @Around("authorizeMethods()")
    public Object checkRole(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        // Get the method that is being called
        MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
        // Retrieve the @Authorize annotation from the method
        Authorize authorize = methodSignature.getMethod().getAnnotation(Authorize.class);

        if (authorize != null) {
            String[] requiredRoles = authorize.value();
            // Validate role
            if (!checkValidRole(Constant.request, requiredRoles)) {
                //Send a custom response back (if you need to terminate the request)
                return ResponseService.generateErrorResponse("Forbidden Access",HttpStatus.FORBIDDEN);
            }
        }
        // Proceed with the method execution if the role is valid
        return proceedingJoinPoint.proceed();
    }

    public Boolean checkValidRole(HttpServletRequest request, String[] requiredRoles) throws Exception {
        String authorizationHeader = request.getHeader(AUTHORIZATION_HEADER);
        // Ensure the Authorization header is present and has the Bearer prefix
        if (authorizationHeader == null || !authorizationHeader.startsWith(BEARER_PREFIX)) {
            return false;  // No valid authorization header
        }
        // Extract the JWT token (substring after Constant.BEARER_CONST;)
        String jwt = authorizationHeader.substring(BEARER_PREFIX_LENGTH);
        // Extract the roleId from the JWT token using the JwtUtil class
        Long roleId = jwtUtil.extractRoleId(jwt);
        // If roleId extraction fails (e.g., invalid token), deny access
        if (roleId == null) {
            return false;
        }
        String roleName=roleService.findRoleNameById(roleId);
        List<String> roleList = Arrays.asList(requiredRoles);
        return roleList.contains(roleName);
    }
}
