package com.shodhAI.ShodhAI.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.cloudinary.Cloudinary;
import com.shodhAI.ShodhAI.Component.JwtUtil;
import com.shodhAI.ShodhAI.Component.TokenBlacklist;
import com.shodhAI.ShodhAI.Controller.StudentController;
import com.shodhAI.ShodhAI.Dto.StudentDto;
import com.shodhAI.ShodhAI.Dto.StudentWrapper;
import com.shodhAI.ShodhAI.Entity.Accuracy;
import com.shodhAI.ShodhAI.Entity.CriticalThinking;
import com.shodhAI.ShodhAI.Entity.Student;
import com.shodhAI.ShodhAI.Entity.StudentAssignment;
import com.shodhAI.ShodhAI.Entity.TimeSpent;
import com.shodhAI.ShodhAI.Service.ExceptionHandlingService;
import com.shodhAI.ShodhAI.Service.S3StorageService;
import com.shodhAI.ShodhAI.Service.RoleService;
import com.shodhAI.ShodhAI.Service.SanitizerService;
import com.shodhAI.ShodhAI.Service.StudentService;
import com.shodhAI.ShodhAI.configuration.TestJwtConfig;
import com.shodhAI.ShodhAI.configuration.TestSecurityConfig;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import({TestSecurityConfig.class, TestJwtConfig.class})
@WebMvcTest(StudentController.class)
@ExtendWith({SpringExtension.class, MockitoExtension.class})
@ContextConfiguration(classes = TestSecurityConfig.class)
public class StudentControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SanitizerService sanitizerService;

    @MockBean
    private RoleService roleService;

    @MockBean
    private StudentService studentService;

    @MockBean
    private Cloudinary cloudinary;

    @MockBean
    private S3StorageService s3StorageService;

    @MockBean
    private EntityManager entityManager;

    @MockBean
    private ExceptionHandlingService exceptionHandlingService;

    @Test
    @DisplayName("test_addStudent_success")
    void testAddStudent() throws Exception {
        // Create a sample StudentDto
        StudentDto studentDto = new StudentDto();
        studentDto.setFirstName("John");
        studentDto.setLastName("Doe");
        studentDto.setCollegeEmail("john.doe@example.com");

        Student student = new Student();
        student.setId(1L);
        student.setFirstName("John");
        student.setLastName("Doe");
        student.setCollegeEmail("john.doe@example.com");

        // Mock service calls
        doNothing().when(studentService).validateStudent(any(StudentDto.class));
        when(studentService.saveStudent(any(StudentDto.class), eq(null), eq('N'))).thenReturn(student);

        // Perform POST request
        mockMvc.perform(post("/student/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(studentDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Student Created Successfully"))
                .andExpect(jsonPath("$.data.student_id").value(1L))
                .andExpect(jsonPath("$.data.first_name").value("John"));

        // Verify service methods were called
        verify(studentService).validateStudent(any(StudentDto.class));
        verify(studentService).saveStudent(any(StudentDto.class), eq(null), eq('N'));
    }

    @Test
    @DisplayName("test_addStudent_validationError")
    void testAddStudentValidationError() throws Exception {
        // Create a sample StudentDto with invalid data
        StudentDto studentDto = new StudentDto();
        studentDto.setFirstName(""); // Invalid empty name

        // Mock validation exception
        doThrow(new IllegalArgumentException("First name cannot be empty"))
                .when(studentService).validateStudent(any(StudentDto.class));

        // Perform POST request
        mockMvc.perform(post("/student/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(studentDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("Illegal Exception Caught: First name cannot be empty"));

        // Verify service methods were called correctly
        verify(studentService).validateStudent(any(StudentDto.class));
        verify(studentService, never()).saveStudent(any(StudentDto.class), any(), anyChar());
        verify(exceptionHandlingService).handleException(any(IllegalArgumentException.class));
    }

    @Test
    @DisplayName("test_uploadProfilePicture_success")
    void testUploadProfilePicture() throws Exception {
        // Create test data
        Student student = new Student();
        student.setId(1L);
        student.setFirstName("John");
        student.setLastName("Doe");

        // Mock service calls
        when(studentService.getStudentById(1L)).thenReturn(student);
        when(studentService.uploadProfilePicture(any(Student.class))).thenReturn(student);

        // Mock S3 service
        when(s3StorageService.uploadFile(any(File.class), anyString())).thenReturn("https://s3.example.com/profile.jpg");

        // Mock cloudinary upload
        Map<String, Object> uploadResult = new HashMap<>();
        uploadResult.put("url", "https://cloudinary.example.com/profile.jpg");
        when(cloudinary.uploader()).thenReturn(mock(com.cloudinary.Uploader.class));
        when(cloudinary.uploader().upload(any(byte[].class), any())).thenReturn(uploadResult);

        // Create multipart file
        MockMultipartFile profilePicture = new MockMultipartFile(
            "profile_picture",
            "profile.jpg",
            "image/jpeg",
            "test image content".getBytes()
        );

        // Perform POST request
        mockMvc.perform(multipart("/student/upload-profile-picture/1")
                        .file(profilePicture))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Profile Picture Uploaded Successfully"));

        // Verify service methods were called
        verify(studentService).getStudentById(1L);
        verify(s3StorageService).uploadFile(any(File.class), anyString());
        verify(studentService).uploadProfilePicture(any(Student.class));
    }

    @Test
    @DisplayName("test_getAllStudents_success")
    void testGetAllStudents() throws Exception {
        // Create sample students
        List<Student> students = new ArrayList<>();
        Student student1 = new Student();
        student1.setId(1L);
        student1.setFirstName("John");
        student1.setLastName("Doe");

        Student student2 = new Student();
        student2.setId(2L);
        student2.setFirstName("Jane");
        student2.setLastName("Smith");

        students.add(student1);
        students.add(student2);

        // Mock service call
        when(studentService.getAllStudent()).thenReturn(students);

        // Perform GET request
        mockMvc.perform(get("/student/get-all")
                        .param("offset", "0")
                        .param("limit", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Student Data Retrieved Successfully"))
                .andExpect(jsonPath("$.data.totalItems").value(2))
                .andExpect(jsonPath("$.data.currentPage").value(0));

        // Verify service method was called
        verify(studentService).getAllStudent();
    }

    @Test
    @DisplayName("test_getStudentById_success")
    void testGetStudentById() throws Exception {

        Accuracy accuracy= new Accuracy();
        accuracy.setAccuracy(1D);
        accuracy.setAccuracyImprovement(1D);
        accuracy.setAccuracyImprovementFlag(true);

        CriticalThinking criticalThinking= new CriticalThinking();
        criticalThinking.setCriticalThinking(1D);
        criticalThinking.setCriticalThinkingImprovement(1D);
        criticalThinking.setCriticalThinkingImprovementFlag(true);

        TimeSpent timeSpent= new TimeSpent();
        timeSpent.setTimeSpent(1D);
        timeSpent.setTimeSpentIncreased(1D);
        // Create sample student
        Student student = new Student();
        student.setId(1L);
        student.setFirstName("John");
        student.setLastName("Doe");
        student.setAccuracy(accuracy);
        student.setCriticalThinking(criticalThinking);
        student.setTimeSpent(timeSpent);
        student.setProfilePictureUrl("profile.jpg");

        // Mock service calls
        when(studentService.getStudentById(1L)).thenReturn(student);
        when(s3StorageService.getPresignedUrl(anyString())).thenReturn(new URL("https://s3.example.com/presigned-url"));

        // Perform GET request
        mockMvc.perform(get("/student/get-student-by-id/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Student Retrieved Successfully"));

        // Verify service method was called
        verify(studentService).getStudentById(1L);
        verify(s3StorageService).getPresignedUrl(anyString());
    }

    @Test
    @DisplayName("test_getStudentLeaderboard_success")
    void testGetStudentLeaderboard() throws Exception {
        Accuracy accuracy= new Accuracy();
        accuracy.setAccuracy(1D);
        accuracy.setAccuracyImprovement(1D);
        accuracy.setAccuracyImprovementFlag(true);

        CriticalThinking criticalThinking= new CriticalThinking();
        criticalThinking.setCriticalThinking(1D);
        criticalThinking.setCriticalThinkingImprovement(1D);
        criticalThinking.setCriticalThinkingImprovementFlag(true);

        TimeSpent timeSpent= new TimeSpent();
        timeSpent.setTimeSpent(1D);
        timeSpent.setTimeSpentIncreased(1D);
        // Create sample students
        List<Student> students = new ArrayList<>();
        Student student1 = new Student();
        student1.setId(1L);
        student1.setFirstName("John");
        student1.setLastName("Doe");
        student1.setMarksObtained(95D);
        student1.setAccuracy(accuracy);
        student1.setCriticalThinking(criticalThinking);
        student1.setTimeSpent(timeSpent);

        Student student2 = new Student();
        student2.setId(2L);
        student2.setFirstName("Jane");
        student2.setLastName("Smith");
        student2.setMarksObtained(92D);
        student2.setAccuracy(accuracy);
        student2.setCriticalThinking(criticalThinking);
        student2.setTimeSpent(timeSpent);

        students.add(student1);
        students.add(student2);

        // Mock service call
        when(studentService.getStudentLeaderboard()).thenReturn(students);

        // Perform GET request
        mockMvc.perform(get("/student/get-leaderboard"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Student Data Retrieved Successfully"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2));

        // Verify service method was called
        verify(studentService).getStudentLeaderboard();
    }

    @Test
    @DisplayName("test_updateStudent_success")
    void testUpdateStudent() throws Exception {
        // Create sample DTO and entity
        StudentDto studentDto = new StudentDto();
        studentDto.setFirstName("Updated John");
        studentDto.setLastName("Updated Doe");

        Student updatedStudent = new Student();
        updatedStudent.setId(1L);
        updatedStudent.setFirstName("Updated John");
        updatedStudent.setLastName("Updated Doe");

        // Mock service call
        when(studentService.updateStudent(eq(1L), any(StudentDto.class))).thenReturn(updatedStudent);

        // Perform PATCH request
        mockMvc.perform(patch("/student/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(studentDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Student updated Successfully"));

        // Verify service method was called
        verify(studentService).updateStudent(eq(1L), any(StudentDto.class));
    }
}