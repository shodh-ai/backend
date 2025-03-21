package com.shodhAI.ShodhAI.Controller;

import com.shodhAI.ShodhAI.Component.ApiConstants;
import com.shodhAI.ShodhAI.Component.Constant;
import com.shodhAI.ShodhAI.Dto.ForgotPasswordDto;
import com.shodhAI.ShodhAI.Dto.SignUpDto;
import com.shodhAI.ShodhAI.Dto.VerifyOtpDto;
import com.shodhAI.ShodhAI.Entity.Faculty;
import com.shodhAI.ShodhAI.Entity.Role;
import com.shodhAI.ShodhAI.Entity.Student;
import com.shodhAI.ShodhAI.Component.JwtUtil;
import com.shodhAI.ShodhAI.Service.AuthenticationService;
import com.shodhAI.ShodhAI.Service.EmailService;
import com.shodhAI.ShodhAI.Service.ExceptionHandlingService;
import com.shodhAI.ShodhAI.Service.FacultyService;
import com.shodhAI.ShodhAI.Service.OtpService;
import com.shodhAI.ShodhAI.Service.ResponseService;
import com.shodhAI.ShodhAI.Service.RoleService;
import com.shodhAI.ShodhAI.Service.StudentService;
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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private FacultyService facultyService;

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

    @Autowired
    private EmailService emailService;

    @Autowired
    private OtpService otpService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private JwtUtil jwtTokenUtil;

    private PasswordEncoder passwordEncoder;

    @PostMapping("/login-with-password")
    @ResponseBody
    public ResponseEntity<?> loginWithPassword(@RequestBody Map<String, Object> loginDetails, HttpSession session, HttpServletRequest request) {
        try {
            String roleName = roleService.findRoleNameById((Long) loginDetails.get("role"));
            if (roleName.equals("EMPTY"))
                return ResponseService.generateErrorResponse("Role not found", HttpStatus.NOT_FOUND);

//            String mobileNumber = (String) loginDetails.get("mobileNumber");
            String username = (String) loginDetails.get("username");
/*            if (mobileNumber != null) {
                if (mobileNumber.startsWith("0"))
                    mobileNumber = mobileNumber.substring(1);
                if (studentService.isValidMobileNumber(mobileNumber) && isNumeric(mobileNumber)) {
                    return loginWithCustomerPassword(loginDetails,session,request);
                } else {
                    return responseService.generateErrorResponse(ApiConstants.INVALID_MOBILE_NUMBER, HttpStatus.BAD_REQUEST);
                }
            } else*/
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
    @PostMapping("/login-with-username-password")
    public ResponseEntity<?> loginWithUsername(@RequestBody Map<String, Object> loginDetails, HttpSession session, HttpServletRequest request) {

        try {
            if (loginDetails == null) {
                return responseService.generateErrorResponse(ApiConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
            }

            String username = (String) loginDetails.get("username");
            String password = (String) loginDetails.get("password");
            Long roleId = Long.parseLong(loginDetails.get("role").toString());

            if (username == null || password == null || roleId == null) {
                return responseService.generateErrorResponse("Username or Password or Role cannot be empty", HttpStatus.BAD_REQUEST);
            }
            if (studentService == null) {
                return responseService.generateErrorResponse("Student service is not initialized.", HttpStatus.INTERNAL_SERVER_ERROR);
            }

            String ipAddress = request.getRemoteAddr();
            String userAgent = request.getHeader("User-Agent");

            if (roleService.findRoleNameById(roleId).equals(Constant.ROLE_USER)) {

                Student student = studentService.retrieveStudentByUsername(username);
                if (student == null) {
                    return responseService.generateErrorResponse(ApiConstants.NO_RECORDS_FOUND, HttpStatus.NOT_FOUND);
                }

                if (passwordEncoder.matches(password, student.getPassword())) {

                    String existingToken = student.getToken();
                    if (existingToken != null && jwtUtil.validateToken(existingToken, ipAddress, userAgent)) {
                        Map<String, Object> userDetails = new HashMap<>();
                        userDetails.put("username", student.getUserName());
                        userDetails.put("mobile_number", student.getMobileNumber());
                        userDetails.put("student_id", student.getId());
                        ApiResponse response = new ApiResponse(existingToken, userDetails, HttpStatus.OK.value(), HttpStatus.OK.name(), "User has been Logged in Successfully");
                        return ResponseEntity.ok(response);

                    } else {
                        ApiResponse response = authenticationService.studentLoginResponse(student, roleId, session, request);
                        return ResponseEntity.ok(response);
                    }
                } else {
                    return responseService.generateErrorResponse("Invalid password", HttpStatus.BAD_REQUEST);
                }
            } else if (roleService.findRoleNameById(roleId).equals(Constant.ROLE_FACULTY)) {

                Faculty faculty = facultyService.retrieveFacultyByUsername(username);
                if (faculty == null) {
                    return responseService.generateErrorResponse(ApiConstants.NO_RECORDS_FOUND, HttpStatus.NOT_FOUND);
                }

                if (passwordEncoder.matches(password, faculty.getPassword())) {

                    String existingToken = faculty.getToken();
                    if (existingToken != null && jwtUtil.validateToken(existingToken, ipAddress, userAgent)) {
                        Map<String, Object> userDetails = new HashMap<>();
                        userDetails.put("username", faculty.getUserName());
                        userDetails.put("mobile_number", faculty.getMobileNumber());
                        userDetails.put("faculty_id", faculty.getId());
                        ApiResponse response = new ApiResponse(existingToken, userDetails, HttpStatus.OK.value(), HttpStatus.OK.name(), "User has been Logged in Successfully");
                        return ResponseEntity.ok(response);

                    } else {
                        ApiResponse response = authenticationService.facultyLoginResponse(faculty, roleId, session, request);
                        return ResponseEntity.ok(response);
                    }
                } else {
                    return responseService.generateErrorResponse("Invalid password", HttpStatus.BAD_REQUEST);
                }
            } else {
                return responseService.generateErrorResponse(ApiConstants.INVALID_ROLE, HttpStatus.BAD_REQUEST);
            }
        } catch (IllegalArgumentException illegalArgumentException) {
            exceptionHandlingService.handleException(illegalArgumentException);
            return ResponseService.generateErrorResponse(illegalArgumentException.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            return responseService.generateErrorResponse(ApiConstants.SOME_EXCEPTION_OCCURRED + exception.getMessage(), HttpStatus.BAD_REQUEST);

        }
    }

    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(@RequestBody SignUpDto signUp) {
        try {

            authenticationService.validateSignUp(signUp);
            Role role = roleService.getRoleById(signUp.getRoleId());

            authenticationService.saveSignUpDetails(role, signUp);
            return ResponseEntity.ok("OTP sent to your email.");

        } catch (IllegalArgumentException illegalArgumentException) {
            exceptionHandlingService.handleException(illegalArgumentException);
            return ResponseService.generateErrorResponse(illegalArgumentException.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            return responseService.generateErrorResponse(ApiConstants.SOME_EXCEPTION_OCCURRED + exception.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody VerifyOtpDto verifyOtpDto, HttpSession session, HttpServletRequest request) {
        try {

            authenticationService.validateVerifyOtp(verifyOtpDto);

            Role role = roleService.getRoleById(verifyOtpDto.getRoleId());
            if (role.getRoleName().equals(Constant.ROLE_USER)) {

                List<Student> students = studentService.filterStudents(null, null, verifyOtpDto.getEmail());
                if (students.isEmpty()) {
                    throw new IllegalArgumentException("Student does not exists with this email");
                }

                Student student = students.get(0);
                if (!student.getOtp().equals(verifyOtpDto.getOtp())) {
                    throw new IllegalArgumentException("Invalid OTP");
                }

                ApiResponse response = authenticationService.studentLoginResponse(student, role.getRoleId(), session, request);
                return ResponseEntity.ok(response);

            } else if (role.getRoleName().equals(Constant.ROLE_FACULTY)) {

                List<Faculty> faculties = facultyService.filterFaculties(null, null, verifyOtpDto.getEmail());
                if (faculties.isEmpty()) {
                    throw new IllegalArgumentException("Faculty does not exists with this email");
                }

                Faculty faculty = faculties.get(0);
                if (!faculty.getOtp().equals(verifyOtpDto.getOtp())) {
                    throw new IllegalArgumentException("Invalid OTP");
                }

                ApiResponse response = authenticationService.facultyLoginResponse(faculty, role.getRoleId(), session, request);
                return ResponseEntity.ok(response);

            } else {
                throw new IllegalArgumentException("Unable to recognize the role");
            }
        } catch (IllegalArgumentException illegalArgumentException) {
            exceptionHandlingService.handleException(illegalArgumentException);
            return ResponseService.generateErrorResponse(illegalArgumentException.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            return responseService.generateErrorResponse(ApiConstants.SOME_EXCEPTION_OCCURRED + exception.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Transactional
    @PostMapping("/forgot-password")
    public ResponseEntity<?> verifyOtp(@RequestBody ForgotPasswordDto forgotPasswordDto, HttpSession session, HttpServletRequest request,
                                       @RequestHeader(value = "Authorization") String authHeader) {
        try {

            authenticationService.validateForgotPasswordDto(forgotPasswordDto);
            String jwtToken = authHeader.substring(7);
            Long roleId = jwtTokenUtil.extractRoleId(jwtToken);
            Long userId = jwtTokenUtil.extractId(jwtToken);
            Role role = roleService.getRoleById(roleId);
            if (role.getRoleName().equals(Constant.ROLE_USER)) {

                List<Student> students = studentService.filterStudents(null, userId, null);
                if (students.isEmpty()) {
                    throw new IllegalArgumentException("Student does not exists with this studentId");
                }

                Student student = students.get(0);
                // set new bcrypt password
                String hashedPassword = passwordEncoder.encode(forgotPasswordDto.getNewPassword());
                student.setPassword(hashedPassword);
                entityManager.merge(student);

                return ResponseEntity.ok(student);

            } else if (role.getRoleName().equals(Constant.ROLE_FACULTY)) {

                List<Faculty> faculties = facultyService.filterFaculties(null, userId, null);
                if (faculties.isEmpty()) {
                    throw new IllegalArgumentException("Faculty does not exists with this userId");
                }

                Faculty faculty = faculties.get(0);
                String hashedPassword = passwordEncoder.encode(forgotPasswordDto.getNewPassword());
                faculty.setPassword(hashedPassword);
                entityManager.merge(faculty);

                return ResponseEntity.ok(faculty);

            } else {
                throw new IllegalArgumentException("Unable to recognize the role");
            }
        } catch (IllegalArgumentException illegalArgumentException) {
            exceptionHandlingService.handleException(illegalArgumentException);
            return ResponseService.generateErrorResponse(illegalArgumentException.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            return responseService.generateErrorResponse(ApiConstants.SOME_EXCEPTION_OCCURRED + exception.getMessage(), HttpStatus.BAD_REQUEST);
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

        public class Data {
            private Map<String, Object> userDetails;

            public Data(Map<String, Object> customerDetails) {
                this.userDetails = customerDetails;
            }

            public Map<String, Object> getUserDetails() {
                return userDetails;
            }
        }
    }

}
