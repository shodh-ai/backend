package com.shodhAI.ShodhAI.Service;

import com.shodhAI.ShodhAI.Component.Constant;
import com.shodhAI.ShodhAI.Component.JwtUtil;
import com.shodhAI.ShodhAI.Controller.AuthController;
import com.shodhAI.ShodhAI.Dto.ChangePasswordDto;
import com.shodhAI.ShodhAI.Dto.FacultyDto;
import com.shodhAI.ShodhAI.Dto.ForgotPasswordDto;
import com.shodhAI.ShodhAI.Dto.SignUpDto;
import com.shodhAI.ShodhAI.Dto.StudentDto;
import com.shodhAI.ShodhAI.Dto.VerifyOtpDto;
import com.shodhAI.ShodhAI.Entity.Faculty;
import com.shodhAI.ShodhAI.Entity.Role;
import com.shodhAI.ShodhAI.Entity.Student;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AuthenticationService {

    @Autowired
    private ExceptionHandlingService exceptionHandlingService;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private StudentService studentService;

    @Autowired
    private OtpService otpService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private FacultyService facultyService;

    @Autowired
    private JwtUtil jwtUtil;

    public void validateSignUp(SignUpDto signUpDto) throws Exception {
        try {

            if (signUpDto.getEmail() == null || signUpDto.getEmail().isEmpty() || !signUpDto.getEmail().endsWith(".com")) {
                throw new IllegalArgumentException("Email cannot be null or empty or having invalid format(***@**.com)");
            }

            if (signUpDto.getRoleId() == null || signUpDto.getRoleId() <= 0) {
                throw new IllegalArgumentException("Role Id cannot be null or <= 0");
            }

        } catch (IllegalArgumentException illegalArgumentException) {
            exceptionHandlingService.handleException(illegalArgumentException);
            throw new IllegalArgumentException(illegalArgumentException.getMessage());
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            throw new Exception(exception.getMessage());
        }
    }

    public void validateVerifyOtp(VerifyOtpDto verifyOtpDto) throws Exception {
        try {

            if (verifyOtpDto.getEmail() == null || verifyOtpDto.getEmail().isEmpty() || !verifyOtpDto.getEmail().endsWith(".com")) {
                throw new IllegalArgumentException("Email cannot be null or empty or having invalid format(***@**.com)");
            }

            if (verifyOtpDto.getRoleId() == null || verifyOtpDto.getRoleId() <= 0) {
                throw new IllegalArgumentException("Role Id cannot be null or <= 0");
            }

            if (verifyOtpDto.getOtp().length() != 6) {
                throw new IllegalArgumentException("Otp length must be of 6 digits");
            }
            Long otp = Long.parseLong(verifyOtpDto.getOtp());

        } catch (IllegalArgumentException illegalArgumentException) {
            exceptionHandlingService.handleException(illegalArgumentException);
            throw new IllegalArgumentException(illegalArgumentException.getMessage());
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            throw new Exception(exception.getMessage());
        }
    }

    public void validateForgotPasswordDto(ForgotPasswordDto forgotPasswordDto) throws Exception {
        try {

            forgotPasswordDto.setEmail(forgotPasswordDto.getEmail().trim());
            if (forgotPasswordDto.getEmail() == null || forgotPasswordDto.getEmail().isEmpty() || !forgotPasswordDto.getEmail().endsWith(".com")) {
                throw new IllegalArgumentException("Email cannot be null or empty or having invalid format(***@**.com)");
            }
            if(forgotPasswordDto.getRoleId() == null || forgotPasswordDto.getRoleId() <= 0) {
                throw new IllegalArgumentException("Role Id cannot be null or empty");
            }
            if (forgotPasswordDto.getNewPassword() == null || forgotPasswordDto.getNewPassword().isEmpty()) {
                throw new IllegalArgumentException("Password cannot be null or empty and length must be > 8");
            }

            /*if(forgotPasswordDto.getConfirmPassword() == null || forgotPasswordDto.getConfirmPassword().isEmpty() || forgotPasswordDto.getConfirmPassword().trim().length() <= 8) {
                throw new IllegalArgumentException("Confirm Password cannot be null or empty and length must be > 8");
            }
            forgotPasswordDto.setConfirmPassword(forgotPasswordDto.getConfirmPassword().trim());*/

            if (!forgotPasswordDto.getNewPassword().equals(forgotPasswordDto.getConfirmPassword())) {
                throw new IllegalArgumentException("Passwords must match each other");
            }

        } catch (IllegalArgumentException illegalArgumentException) {
            exceptionHandlingService.handleException(illegalArgumentException);
            throw new IllegalArgumentException(illegalArgumentException.getMessage());
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            throw new Exception(exception.getMessage());
        }
    }

    public void validateChangePasswordDto(ChangePasswordDto changePasswordDto) throws Exception {
        try {

            if (changePasswordDto.getNewPassword() == null || changePasswordDto.getNewPassword().isEmpty()) {
                throw new IllegalArgumentException("Password cannot be null or empty and length must be > 8");
            }

            /*if(changePasswordDto.getConfirmPassword() == null || changePasswordDto.getConfirmPassword().isEmpty() || changePasswordDto.getConfirmPassword().trim().length() <= 8) {
                throw new IllegalArgumentException("Confirm Password cannot be null or empty and length must be > 8");
            }
            changePasswordDto.setConfirmPassword(changePasswordDto.getConfirmPassword().trim());*/

            if (!changePasswordDto.getNewPassword().equals(changePasswordDto.getConfirmPassword())) {
                throw new IllegalArgumentException("Passwords must match each other");
            }

        } catch (IllegalArgumentException illegalArgumentException) {
            exceptionHandlingService.handleException(illegalArgumentException);
            throw new IllegalArgumentException(illegalArgumentException.getMessage());
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            throw new Exception(exception.getMessage());
        }
    }

    @Transactional
    public void saveSignUpDetails(Role role, SignUpDto signUpDto) {
        try {
            if (role.getRoleName().equals(Constant.ROLE_USER)) {

                List<Student> students = studentService.filterStudents(null, null, signUpDto.getEmail());
                if (!students.isEmpty()) {
                    Student student = students.get(0);

                    String otp = otpService.generateOtp(signUpDto.getEmail());
                    emailService.sendOtp(signUpDto.getEmail(), otp);
                    student.setOtp(otp);

                    entityManager.merge(student);
                } else {
                    String otp = otpService.generateOtp(signUpDto.getEmail());
                    emailService.sendOtp(signUpDto.getEmail(), otp);

                    StudentDto studentDto = new StudentDto();

                    Student student = new Student();
                    studentDto.setPersonalEmail(signUpDto.getEmail());
                    studentService.saveStudent(studentDto, otp, 'Y');
                }

            } else if (role.getRoleName().equals(Constant.ROLE_FACULTY)) {

                List<Faculty> faculties = facultyService.filterFaculties(null, null, signUpDto.getEmail());
                if (!faculties.isEmpty()) {
                    throw new IllegalArgumentException("Faculty already exists with this email");
                }

                String otp = otpService.generateOtp(signUpDto.getEmail());
                emailService.sendOtp(signUpDto.getEmail(), otp);

                FacultyDto facultyDto = new FacultyDto();
                facultyDto.setPersonalEmail(signUpDto.getEmail());

                facultyService.saveFaculty(facultyDto, otp, 'Y');
            } else {
                throw new IllegalArgumentException("Unable to recognize the role");
            }
        } catch (IllegalArgumentException illegalArgumentException) {
            exceptionHandlingService.handleException(illegalArgumentException);
            throw new IllegalArgumentException(illegalArgumentException.getMessage());
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            throw new IllegalArgumentException(exception.getMessage());
        }
    }

    @Transactional
    public AuthController.ApiResponse facultyLoginResponse(Faculty faculty, Long roleId, HttpSession session, HttpServletRequest request) throws Exception {
        try {
            String tokenKey = "authToken_" + faculty.getPersonalEmail();
            String ipAddress = request.getRemoteAddr();
            String userAgent = request.getHeader("User-Agent");

            String token = jwtUtil.generateToken(faculty.getId(), roleId, ipAddress, userAgent);
            faculty.setToken(token);
            if (faculty.getArchived().equals('Y')) {
                faculty.setArchived('N');
            }
            entityManager.persist(faculty);
            session.setAttribute(tokenKey, token);
            Map<String, Object> userDetails = new HashMap<>();
            userDetails.put("email", faculty.getPersonalEmail());
            userDetails.put("mobile_number", faculty.getMobileNumber());
            userDetails.put("faculty_id", faculty.getId());
            AuthController.ApiResponse response = new AuthController.ApiResponse(token, userDetails, HttpStatus.OK.value(), HttpStatus.OK.name(), "User has been Logged in Successfully");
            return response;
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            throw new Exception(exception.getMessage());
        }
    }

    @Transactional
    public AuthController.ApiResponse studentLoginResponse(Student student, Long roleId, HttpSession session, HttpServletRequest request) throws Exception {
        try {
            String tokenKey = "authToken_" + student.getPersonalEmail();
            String ipAddress = request.getRemoteAddr();
            String userAgent = request.getHeader("User-Agent");

            String token = jwtUtil.generateToken(student.getId(), roleId, ipAddress, userAgent);

            student.setToken(token);
            if (student.getArchived().equals('Y')) {
                student.setArchived('N');
            }
            entityManager.persist(student);
            session.setAttribute(tokenKey, token);
            Map<String, Object> userDetails = new HashMap<>();
            userDetails.put("email", student.getPersonalEmail());
            userDetails.put("mobile_number", student.getMobileNumber());
            userDetails.put("student_id", student.getId());
            AuthController.ApiResponse response = new AuthController.ApiResponse(token, userDetails, HttpStatus.OK.value(), HttpStatus.OK.name(), "User has been Logged in Successfully");
            return response;
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            throw new Exception(exception.getMessage());
        }
    }

}
