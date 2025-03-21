package com.shodhAI.ShodhAI.Controller;

import com.shodhAI.ShodhAI.Component.ApiConstants;
import com.shodhAI.ShodhAI.Component.Constant;
import com.shodhAI.ShodhAI.Dto.AcademicDegreeDto;
import com.shodhAI.ShodhAI.Dto.AccuracyDto;
import com.shodhAI.ShodhAI.Dto.CriticalThinkingDto;
import com.shodhAI.ShodhAI.Dto.ForgotPasswordDto;
import com.shodhAI.ShodhAI.Dto.MemoryDto;
import com.shodhAI.ShodhAI.Dto.SignUpDto;
import com.shodhAI.ShodhAI.Dto.TimeSpentDto;
import com.shodhAI.ShodhAI.Dto.UnderstandingDto;
import com.shodhAI.ShodhAI.Dto.VerifyOtpDto;
import com.shodhAI.ShodhAI.Entity.AcademicDegree;
import com.shodhAI.ShodhAI.Entity.Accuracy;
import com.shodhAI.ShodhAI.Entity.CriticalThinking;
import com.shodhAI.ShodhAI.Entity.Faculty;
import com.shodhAI.ShodhAI.Entity.Gender;
import com.shodhAI.ShodhAI.Entity.Memory;
import com.shodhAI.ShodhAI.Entity.Role;
import com.shodhAI.ShodhAI.Entity.Student;
import com.shodhAI.ShodhAI.Entity.TimeSpent;
import com.shodhAI.ShodhAI.Entity.Understanding;
import com.shodhAI.ShodhAI.Service.*;
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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
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


    @Autowired
    AcademicDegreeService academicDegreeService;
    @Autowired
    TimeSpentService timeSpentService;
    @Autowired
    AccuracyService accuracyService;
    @Autowired
    UnderstandingService understandingService;
    @Autowired
    MemoryService memoryService;
    @Autowired
    CriticalThinkingService criticalThinkingService;
    @Autowired
    GenderService genderService;


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
            if (studentService == null) {
                return responseService.generateErrorResponse("Student service is not initialized.", HttpStatus.INTERNAL_SERVER_ERROR);
            }

            if (roleService.findRoleNameById(roleId).equals(Constant.ROLE_USER)) {

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
                        userDetails.put("mobile_number", student.getMobileNumber());
                        userDetails.put("student_id", student.getId());
                        ApiResponse response = new ApiResponse(existingToken, userDetails, HttpStatus.OK.value(), HttpStatus.OK.name(), "User has been Logged in Successfully");
                        return ResponseEntity.ok(response);

                    } else {
                        String token = jwtUtil.generateToken(student.getId(), roleId, ipAddress, userAgent);
                        student.setToken(token);
                        entityManager.persist(student);
                        session.setAttribute(tokenKey, token);
                        Map<String, Object> userDetails = new HashMap<>();
                        userDetails.put("username", student.getUserName());
                        userDetails.put("mobile_number", student.getMobileNumber());
                        userDetails.put("student_id", student.getId());
                        ApiResponse response = new ApiResponse(token, userDetails, HttpStatus.OK.value(), HttpStatus.OK.name(), "User has been Logged in Successfully");
                        return ResponseEntity.ok(response);
                    }
                } else {
                    return responseService.generateErrorResponse("Invalid password", HttpStatus.BAD_REQUEST);
                }
            } else if(roleService.findRoleNameById(roleId).equals(Constant.ROLE_FACULTY)) {

                Faculty faculty = facultyService.retrieveFacultyByUsername(username);
                if (faculty == null) {
                    return responseService.generateErrorResponse(ApiConstants.NO_RECORDS_FOUND, HttpStatus.NOT_FOUND);
                }

                if (passwordEncoder.matches(password, faculty.getPassword())) {

                    String tokenKey = "authToken_" + faculty.getMobileNumber();
                    String existingToken = faculty.getToken();
                    authHeader = authHeader + existingToken;
                    String ipAddress = request.getRemoteAddr();
                    String userAgent = request.getHeader("User-Agent");

                    if (existingToken != null && jwtUtil.validateToken(existingToken, ipAddress, userAgent)) {
                        Map<String, Object> userDetails = new HashMap<>();
                        userDetails.put("username", faculty.getUserName());
                        userDetails.put("mobile_number", faculty.getMobileNumber());
                        userDetails.put("faculty_id", faculty.getId());
                        ApiResponse response = new ApiResponse(existingToken, userDetails, HttpStatus.OK.value(), HttpStatus.OK.name(), "User has been Logged in Successfully");
                        return ResponseEntity.ok(response);

                    } else {
                        String token = jwtUtil.generateToken(faculty.getId(), roleId, ipAddress, userAgent);
                        faculty.setToken(token);
                        entityManager.persist(faculty);
                        session.setAttribute(tokenKey, token);
                        Map<String, Object> userDetails = new HashMap<>();
                        userDetails.put("username", faculty.getUserName());
                        userDetails.put("mobile_number", faculty.getMobileNumber());
                        userDetails.put("faculty_id", faculty.getId());
                        ApiResponse response = new ApiResponse(token, userDetails, HttpStatus.OK.value(), HttpStatus.OK.name(), "User has been Logged in Successfully");
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

    @Transactional
    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(@RequestBody SignUpDto signUp) {
        try{

            authenticationService.validateSignUp(signUp);

            Role role = roleService.getRoleById(signUp.getRoleId());
            if(role.getRoleName().equals(Constant.ROLE_USER)) {

                List<Student> students = studentService.filterStudents(null, null, signUp.getEmail());
                if(!students.isEmpty()) {
                    // Set is otp to null
                    Student student = students.get(0);
                    student.setOtp(null);
                    entityManager.merge(student);

                    throw new IllegalArgumentException("Student already exists with this email");
                }

                String otp = otpService.generateOtp(signUp.getEmail());
                emailService.sendOtp(signUp.getEmail(), otp);

                Date currentDate = new Date();

                Student student = new Student();
                student.setPersonalEmail(signUp.getEmail());
                student.setArchived('Y');
                student.setOtp(otp);
                student.setRole(role);
                student.setCreatedDate(currentDate);
                student.setUpdatedDate(currentDate);
                student.setCollegeEmail(signUp.getEmail());

                Gender gender = genderService.getGenderById(1L);
                student.setGender(gender);

                AcademicDegreeDto academicDegreeDto = new AcademicDegreeDto();
                academicDegreeDto.setDegreeName("hello");
                AcademicDegree academicDegree = academicDegreeService.saveAcademicDegree(academicDegreeDto);
                TimeSpent timeSpent = timeSpentService.saveTimeSpent(new TimeSpentDto());
                Accuracy accuracy = accuracyService.saveAccuracy(new AccuracyDto());
                Understanding understanding = understandingService.saveUnderstanding(new UnderstandingDto());
                Memory memory = memoryService.saveMemory(new MemoryDto());
                CriticalThinking criticalThinking = criticalThinkingService.saveCriticalThinking(new CriticalThinkingDto());

                student.setAccuracy(accuracy);
                student.setAcademicDegree(academicDegree);
                student.setTimeSpent(timeSpent);
                student.setUnderstanding(understanding);
                student.setMemory(memory);
                student.setCriticalThinking(criticalThinking);
                System.out.println(student+ "issue lies in here");
                entityManager.merge(student);

            } else if( role.getRoleName().equals(Constant.ROLE_FACULTY)) {

                List<Faculty> faculties = facultyService.filterFaculties(null, null, signUp.getEmail());
                if(!faculties.isEmpty()) {
                    // Set is otp to null
                    Faculty faculty = faculties.get(0);
                    faculty.setOtp(null);
                    entityManager.merge(faculty);

                    throw new IllegalArgumentException("Faculty already exists with this email");
                }

                String otp = otpService.generateOtp(signUp.getEmail());
                emailService.sendOtp(signUp.getEmail(), otp);

                Faculty faculty = new Faculty();
                faculty.setPersonalEmail(signUp.getEmail());
                faculty.setArchived('Y');
                faculty.setOtp(otp);
                faculty.setRole(role);
                Gender gender = genderService.getGenderById(1L);
                faculty.setGender(gender);
                faculty.setCollegeEmail(signUp.getEmail());
                entityManager.merge(faculty);
            } else {
                throw new IllegalArgumentException("Unable to recognize the role");
            }

            return ResponseEntity.ok("OTP sent to your email.");

        } catch (IllegalArgumentException illegalArgumentException) {
            exceptionHandlingService.handleException(illegalArgumentException);
            return ResponseService.generateErrorResponse(illegalArgumentException.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            return responseService.generateErrorResponse(ApiConstants.SOME_EXCEPTION_OCCURRED + exception.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Transactional
    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody VerifyOtpDto verifyOtpDto, HttpSession session, HttpServletRequest request) {
        try {

            authenticationService.validateVerifyOtp(verifyOtpDto);

            Role role = roleService.getRoleById(verifyOtpDto.getRoleId());
            if(role.getRoleName().equals(Constant.ROLE_USER)) {

                List<Student> students = studentService.filterStudents(null, null, verifyOtpDto.getEmail());
                if(students.isEmpty()) {
                    throw new IllegalArgumentException("Student does not exists with this email");
                }

                Student student = students.get(0);
                if(!student.getOtp().equals(verifyOtpDto.getOtp())){
                    throw new IllegalArgumentException("Invalid OTP");
                }
                student.setArchived('N');
                entityManager.merge(student);

                String tokenKey = "authToken_" + student.getMobileNumber();
                String ipAddress = request.getRemoteAddr();
                String userAgent = request.getHeader("User-Agent");

                String token = jwtUtil.generateToken(student.getId(), verifyOtpDto.getRoleId(), ipAddress, userAgent);
                student.setToken(token);
                entityManager.persist(student);
                session.setAttribute(tokenKey, token);
                Map<String, Object> userDetails = new HashMap<>();
                userDetails.put("username", student.getUserName());
                userDetails.put("mobile_number", student.getMobileNumber());
                userDetails.put("student_id", student.getId());
                ApiResponse response = new ApiResponse(token, userDetails, HttpStatus.OK.value(), HttpStatus.OK.name(), "User has been Logged in Successfully");
                return ResponseEntity.ok(response);

            } else if( role.getRoleName().equals(Constant.ROLE_FACULTY)) {

                List<Faculty> faculties = facultyService.filterFaculties(null, null, verifyOtpDto.getEmail());
                if(faculties.isEmpty()) {
                    throw new IllegalArgumentException("Faculty does not exists with this email");
                }

                Faculty faculty = faculties.get(0);
                if(!faculty.getOtp().equals(verifyOtpDto.getOtp())){
                    throw new IllegalArgumentException("Invalid OTP");
                }
                faculty.setArchived('N');
                entityManager.merge(faculty);

                String tokenKey = "authToken_" + faculty.getMobileNumber();
                String ipAddress = request.getRemoteAddr();
                String userAgent = request.getHeader("User-Agent");

                String token = jwtUtil.generateToken(faculty.getId(), verifyOtpDto.getRoleId(), ipAddress, userAgent);
                faculty.setToken(token);
                entityManager.persist(faculty);
                session.setAttribute(tokenKey, token);
                Map<String, Object> userDetails = new HashMap<>();
                userDetails.put("username", faculty.getUserName());
                userDetails.put("mobile_number", faculty.getMobileNumber());
                userDetails.put("faculty_id", faculty.getId());
                ApiResponse response = new ApiResponse(token, userDetails, HttpStatus.OK.value(), HttpStatus.OK.name(), "User has been Logged in Successfully");
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
            if(role.getRoleName().equals(Constant.ROLE_USER)) {

                List<Student> students = studentService.filterStudents(null, userId, null);
                if(students.isEmpty()) {
                    throw new IllegalArgumentException("Student does not exists with this studentId");
                }

                Student student = students.get(0);
                // set new bcrypt password
                String hashedPassword = passwordEncoder.encode(forgotPasswordDto.getNewPassword());
                student.setPassword(hashedPassword);
                entityManager.merge(student);

                return ResponseEntity.ok(student);

            } else if( role.getRoleName().equals(Constant.ROLE_FACULTY)) {

                List<Faculty> faculties = facultyService.filterFaculties(null, userId, null);
                if(faculties.isEmpty()) {
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
