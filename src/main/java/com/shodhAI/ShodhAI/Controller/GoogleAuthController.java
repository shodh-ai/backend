package com.shodhAI.ShodhAI.Controller;

import com.shodhAI.ShodhAI.Component.ApiConstants;
import com.shodhAI.ShodhAI.Component.Constant;
import com.shodhAI.ShodhAI.Dto.GoogleAuthDto;
import com.shodhAI.ShodhAI.Entity.Faculty;
import com.shodhAI.ShodhAI.Entity.Role;
import com.shodhAI.ShodhAI.Entity.Student;
import com.shodhAI.ShodhAI.Service.AuthenticationService;
import com.shodhAI.ShodhAI.Service.ExceptionHandlingService;
import com.shodhAI.ShodhAI.Service.FacultyService;
import com.shodhAI.ShodhAI.Service.GoogleAuthService;
import com.shodhAI.ShodhAI.Service.ResponseService;
import com.shodhAI.ShodhAI.Service.RoleService;
import com.shodhAI.ShodhAI.Service.StudentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/auth/google")
public class GoogleAuthController {

    @Autowired
    private GoogleAuthService googleAuthService;
    
    @Autowired
    private RoleService roleService;
    
    @Autowired
    private StudentService studentService;
    
    @Autowired
    private FacultyService facultyService;
    
    @Autowired
    private AuthenticationService authenticationService;
    
    @Autowired
    private ExceptionHandlingService exceptionHandlingService;
    
    @Autowired
    private ResponseService responseService;

    @PostMapping("/login")
    public ResponseEntity<?> googleLogin(@RequestBody GoogleAuthDto googleAuthDto, 
                                        HttpSession session, 
                                        HttpServletRequest request) {
        try {
            // Validate the Google token
            String email = googleAuthService.validateGoogleToken(googleAuthDto.getToken());
            if (email == null) {
                return responseService.generateErrorResponse("Invalid Google token", HttpStatus.UNAUTHORIZED);
            }
            
            Role role = roleService.getRoleById(googleAuthDto.getRoleId());
            if (role == null) {
                return responseService.generateErrorResponse(ApiConstants.INVALID_ROLE, HttpStatus.BAD_REQUEST);
            }
            
            // Check if user exists
            if (role.getRoleName().equals(Constant.ROLE_USER)) {
                List<Student> students = studentService.filterStudents(null, null, email);
                
                if (students.isEmpty()) {
                    // New user - auto-register them
                    Student student = googleAuthService.registerStudentWithGoogle(email, role.getRoleId());
                    AuthController.ApiResponse response = authenticationService.studentLoginResponse(student, role.getRoleId(), session, request);
                    return ResponseService.generateSuccessResponse("Student Registration is done", response,HttpStatus.OK);
                } else {
                    // Returning user
                    Student student = students.get(0);
                    AuthController.ApiResponse response = authenticationService.studentLoginResponse(student, role.getRoleId(), session, request);
                    return ResponseService.generateSuccessResponse("Login is successfully done for the student", response,HttpStatus.OK);
                }
            } else if (role.getRoleName().equals(Constant.ROLE_FACULTY)) {
                List<Faculty> faculties = facultyService.filterFaculties(null, null, email);
                
                if (faculties.isEmpty()) {
                    // New user - auto-register them
                    Faculty faculty = googleAuthService.registerFacultyWithGoogle(email, role.getRoleId());
                    AuthController.ApiResponse response = authenticationService.facultyLoginResponse(faculty, role.getRoleId(), session, request);
                    return ResponseService.generateSuccessResponse("Faculty Registration is done ",response,HttpStatus.OK);
                } else {
                    // Returning user
                    Faculty faculty = faculties.get(0);
                    AuthController.ApiResponse response = authenticationService.facultyLoginResponse(faculty, role.getRoleId(), session, request);
                    return ResponseService.generateSuccessResponse("Login is successfully done for the faculty",response,HttpStatus.OK);
                }
            } else {
                return responseService.generateErrorResponse("Unable to recognize the role", HttpStatus.BAD_REQUEST);
            }
            
        } catch (IllegalArgumentException illegalArgumentException) {
            exceptionHandlingService.handleException(illegalArgumentException);
            return ResponseService.generateErrorResponse(illegalArgumentException.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            return responseService.generateErrorResponse(ApiConstants.SOME_EXCEPTION_OCCURRED + exception.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}