package com.shodhAI.ShodhAI.Service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.shodhAI.ShodhAI.Component.Constant;
import com.shodhAI.ShodhAI.Dto.FacultyDto;
import com.shodhAI.ShodhAI.Dto.StudentDto;
import com.shodhAI.ShodhAI.Entity.Faculty;
import com.shodhAI.ShodhAI.Entity.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.UUID;

@Service
public class GoogleAuthService {

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;
    
    @Autowired
    private StudentService studentService;
    
    @Autowired
    private FacultyService facultyService;
    
    @Autowired
    private ExceptionHandlingService exceptionHandlingService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Validates a Google ID token and returns the email if valid
     * 
     * @param idTokenString The Google ID token
     * @return The email from the token, or null if invalid
     */
    public String validateGoogleToken(String idTokenString) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(), new GsonFactory())
                    .setAudience(Collections.singletonList(clientId))
                    .build();

            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken != null) {
                Payload payload = idToken.getPayload();
                
                // Get user email
                String email = payload.getEmail();
                
                // You could also get other information if needed
                // String name = (String) payload.get("name");
                // String pictureUrl = (String) payload.get("picture");
                
                return email;
            } else {
                return null;
            }
        } catch (GeneralSecurityException | IOException e) {
            exceptionHandlingService.handleException(e);
            return null;
        }
    }
    
    /**
     * Registers a new student account with Google authentication
     */
    @Transactional
    public Student registerStudentWithGoogle(String email, Long roleId) {
        try {
            StudentDto studentDto = new StudentDto();
            studentDto.setPersonalEmail(email);
            studentDto.setUserName(email.split("@")[0]); // Use part of email as username
            
            // Generate a secure random password (user won't need this since they'll use Google login)
            String randomPassword = UUID.randomUUID().toString();
            studentDto.setPassword(passwordEncoder.encode(randomPassword));
            
            // Create and save the student
            Student student = new Student();
            student.setPersonalEmail(email);
            student.setUserName(studentDto.getUserName());
            student.setPassword(studentDto.getPassword());
            student.setAuthProvider("GOOGLE");
            student.setArchived('N');
            
            // Save with empty OTP since we don't need it for Google auth
            return studentService.saveStudent(studentDto, "", 'N');
            
        } catch (Exception e) {
            exceptionHandlingService.handleException(e);
            throw new IllegalArgumentException("Failed to register student: " + e.getMessage());
        }
    }
    
    /**
     * Registers a new faculty account with Google authentication
     */
    @Transactional
    public Faculty registerFacultyWithGoogle(String email, Long roleId) {
        try {
            FacultyDto facultyDto = new FacultyDto();
            facultyDto.setPersonalEmail(email);
            facultyDto.setUserName(email.split("@")[0]); // Use part of email as username
            
            // Generate a secure random password (user won't need this since they'll use Google login)
            String randomPassword = UUID.randomUUID().toString();
            facultyDto.setPassword(passwordEncoder.encode(randomPassword));
            
            // Create and save the faculty
            Faculty faculty = new Faculty();
            faculty.setPersonalEmail(email);
            faculty.setUserName(facultyDto.getUserName());
            faculty.setPassword(facultyDto.getPassword());
            faculty.setAuthProvider("GOOGLE");
            faculty.setArchived('N');
            
            // Save with empty OTP since we don't need it for Google auth
            return facultyService.saveFaculty(facultyDto, "", 'N');
            
        } catch (Exception e) {
            exceptionHandlingService.handleException(e);
            throw new IllegalArgumentException("Failed to register faculty: " + e.getMessage());
        }
    }
}