package com.shodhAI.ShodhAI.Controller;

import com.shodhAI.ShodhAI.Component.ApiConstants;
import com.shodhAI.ShodhAI.Component.Constant;
import com.shodhAI.ShodhAI.Entity.Student;
import com.shodhAI.ShodhAI.Service.ExceptionHandlingService;
import com.shodhAI.ShodhAI.Service.ResponseService;
import com.shodhAI.ShodhAI.Service.RoleService;
import com.shodhAI.ShodhAI.Service.StudentService;
import com.shodhAI.ShodhAI.Component.JwtUtil;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ResponseService responseService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private ExceptionHandlingService exceptionHandlingService;

    private PasswordEncoder passwordEncoder;

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login-with-password")
    @ResponseBody
    public ResponseEntity<?> loginWithPassword(@RequestBody Map<String, Object> loginDetails, HttpSession session, HttpServletRequest request) {
        try {
            String roleName = roleService.findRoleNameById((Long) loginDetails.get("role"));
            if (roleName.equals("EMPTY"))
                return ResponseService.generateErrorResponse("Role not found", HttpStatus.NOT_FOUND);

//            String mobileNumber = (String) loginDetails.get("mobileNumber");
            String username = (String) loginDetails.get("username");
//            if (mobileNumber != null) {
//                if (mobileNumber.startsWith("0"))
//                    mobileNumber = mobileNumber.substring(1);
//                if (studentService.isValidMobileNumber(mobileNumber) && isNumeric(mobileNumber)) {
//                    return loginWithCustomerPassword(loginDetails,session,request);
//                } else {
//                    return responseService.generateErrorResponse(ApiConstants.INVALID_MOBILE_NUMBER, HttpStatus.BAD_REQUEST);
//                }
//            } else
            if (username != null) {
                return loginWithUsername(loginDetails, session, request);
            } else {
                return responseService.generateErrorResponse(ApiConstants.INVALID_DATA, HttpStatus.INTERNAL_SERVER_ERROR);

            }
        } catch (Exception e) {
            exceptionHandlingService.handleException(e);
            return responseService.generateErrorResponse(ApiConstants.SOME_EXCEPTION_OCCURRED + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }

    // Login endpoint to authenticate user and return JWT token
    @Transactional
    @PostMapping("/login-with-username-password")
    public ResponseEntity<?> loginWithUsername(@RequestBody Map<String, Object> loginDetails, HttpSession session, HttpServletRequest request) {

        try {
            if (loginDetails == null) {
                return responseService.generateErrorResponse(ApiConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
            }

            String authHeader = Constant.BEARER;
            String username = (String) loginDetails.get("username");
            String password = (String) loginDetails.get("password");
            Long roleId = Long.parseLong(loginDetails.get("role").toString());

            if (username == null || password == null || roleId == null) {
                return responseService.generateErrorResponse("Username or Password or Role cannot be empty", HttpStatus.BAD_REQUEST);
            }
            if (roleService.findRoleNameById(roleId).equals(Constant.ROLE_USER)) {
                if (studentService == null) {
                    return responseService.generateErrorResponse("Student service is not initialized.", HttpStatus.INTERNAL_SERVER_ERROR);
                }

                Student student = studentService.retrieveStudentByUsername(username);
                if (student == null) {
                    return responseService.generateErrorResponse(ApiConstants.NO_RECORDS_FOUND, HttpStatus.NOT_FOUND);
                }

                if (passwordEncoder.matches(password, student.getPassword())) {

                    String tokenKey = "authToken_" + student.getMobileNumber();
                    String existingToken = student.getToken();
                    authHeader = authHeader + existingToken;
                    String ipAddress = request.getRemoteAddr();
                    String userAgent = request.getHeader("User-Agent");

                    if (existingToken != null && jwtUtil.validateToken(existingToken, ipAddress, userAgent)) {
                        Map<String, Object> userDetails = new HashMap<>();
                        userDetails.put("username", student.getUserName());
                        userDetails.put("password", student.getPassword());
                        ApiResponse response = new ApiResponse(existingToken, userDetails, HttpStatus.OK.value(), HttpStatus.OK.name(), "User has been signed in");
                        return ResponseEntity.ok(response);

                    } else {
                        String token = jwtUtil.generateToken(student.getId(), roleId, ipAddress, userAgent);
                        student.setToken(token);
                        entityManager.persist(student);
                        session.setAttribute(tokenKey, token);
                        Map<String, Object> userDetails = new HashMap<>();
                        userDetails.put("username", student.getUserName());
                        userDetails.put("password", student.getPassword());
                        ApiResponse response = new ApiResponse(token, userDetails, HttpStatus.OK.value(), HttpStatus.OK.name(), "User has been signed in");
                        return ResponseEntity.ok(response);
                    }
                } else {
                    return responseService.generateErrorResponse("Invalid password", HttpStatus.BAD_REQUEST);

                }
            } else {
                return responseService.generateErrorResponse(ApiConstants.INVALID_ROLE, HttpStatus.BAD_REQUEST);

            }
        } catch (IllegalArgumentException e) {
            return ResponseService.generateErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            exceptionHandlingService.handleException(e);
            return responseService.generateErrorResponse(ApiConstants.SOME_EXCEPTION_OCCURRED + e.getMessage(), HttpStatus.BAD_REQUEST);

        }
    }

    public static class ApiResponse {
        private Data data;
        private int status_code;
        private String status;
        private String message;
        private String token;


        public ApiResponse(String token, Map<String, Object> userDetails, int statusCodeValue, String statusCode, String message) {
            this.data = new Data(userDetails);
            this.status_code = statusCodeValue;
            this.status = statusCode;
            this.message = message;
            this.token = token;
        }

        public Data getData() {
            return data;
        }

        public int getStatus_code() {
            return status_code;
        }

        public String getStatus() {
            return status;
        }

        public String getToken() {
            return token;
        }

        public String getMessage() {
            return message;
        }

        public  class Data {
            private Map<String,Object> userDetails;

            public Data(Map<String,Object>customerDetails) {
                this.userDetails = customerDetails;
            }

            public Map<String,Object> getUserDetails() {
                return userDetails;
            }
        }
    }
}
