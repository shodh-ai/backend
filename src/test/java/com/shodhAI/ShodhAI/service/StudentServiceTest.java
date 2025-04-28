package com.shodhAI.ShodhAI.service;

import com.shodhAI.ShodhAI.Dto.*;
import com.shodhAI.ShodhAI.Entity.*;
import com.shodhAI.ShodhAI.Service.AcademicDegreeService;
import com.shodhAI.ShodhAI.Service.AccuracyService;
import com.shodhAI.ShodhAI.Service.CriticalThinkingService;
import com.shodhAI.ShodhAI.Service.ExceptionHandlingService;
import com.shodhAI.ShodhAI.Service.GenderService;
import com.shodhAI.ShodhAI.Service.MemoryService;
import com.shodhAI.ShodhAI.Service.RoleService;
import com.shodhAI.ShodhAI.Service.StudentService;
import com.shodhAI.ShodhAI.Service.TimeSpentService;
import com.shodhAI.ShodhAI.Service.UnderstandingService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StudentServiceTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private ExceptionHandlingService exceptionHandlingService;

    @Mock
    private GenderService genderService;

    @Mock
    private RoleService roleService;

    @Mock
    private AcademicDegreeService academicDegreeService;

    @Mock
    private AccuracyService accuracyService;

    @Mock
    private CriticalThinkingService criticalThinkingService;

    @Mock
    private TimeSpentService timeSpentService;

    @Mock
    private UnderstandingService understandingService;

    @Mock
    private MemoryService memoryService;

    @Mock
    private TypedQuery<Student> studentTypedQuery;

    @Mock
    private TypedQuery<Course> courseTypedQuery;

    @InjectMocks
    private StudentService studentService;

    private StudentDto validDto;
    private Student student;
    private Gender gender;
    private Role role;
    private AcademicDegree academicDegree;
    private CriticalThinking criticalThinking;
    private Accuracy accuracy;
    private TimeSpent timeSpent;
    private Understanding understanding;
    private Memory memory;

    @BeforeEach
    void setUp() {
        validDto = new StudentDto();
        validDto.setFirstName("John");
        validDto.setLastName("Doe");
        validDto.setUserName("johndoe");
        validDto.setCountryCode("+1");
        validDto.setMobileNumber("1234567890");
        validDto.setPassword("password");
        validDto.setGenderId(1L);
        validDto.setAcademicDegreeId(1L);
        validDto.setCollegeEmail("john.doe@college.edu");
        validDto.setPersonalEmail("john.doe@example.com");

        gender = new Gender();
        gender.setGenderId(1L);
        gender.setGenderName("Male");

        role = new Role();
        role.setRoleId(4L);
        role.setRoleName("STUDENT");

        academicDegree = new AcademicDegree();
        academicDegree.setDegreeId(1L);
        academicDegree.setDegreeName("BSc");

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

        understanding = new Understanding();
        understanding.setUnderstanding(1D);
        understanding.setUnderstandingImprovement(1D);
        understanding.setUnderstandingImprovementFlag(true);

        memory = new Memory();
        memory.setMemory(1D);
        memory.setMemoryImprovement(1D);
        memory.setMemoryImprovementFlag(true);

        student = new Student();
        student.setId(1L);
        student.setFirstName("John");
        student.setLastName("Doe");
        student.setUserName("johndoe");
        student.setCountryCode("+1");
        student.setMobileNumber("1234567890");
        student.setPassword("hashedPassword");
        student.setGender(gender);
        student.setRole(role);
        student.setAcademicDegree(academicDegree);
        student.setCollegeEmail("john.doe@college.edu");
        student.setPersonalEmail("john.doe@example.com");
        student.setCriticalThinking(criticalThinking);
        student.setAccuracy(accuracy);
        student.setTimeSpent(timeSpent);
        student.setUnderstanding(understanding);
        student.setMemory(memory);
        student.setCourses(new ArrayList<>());
        student.setFacultyMembers(new ArrayList<>());
    }

    @Test
    @DisplayName("Should validate student successfully")
    void testValidateStudent() throws Exception {
        // Given
        StudentDto dto = new StudentDto();
        dto.setFirstName("  John  ");
        dto.setLastName("  Doe  ");
        dto.setCountryCode("  +1  ");
        dto.setMobileNumber("  1234567890  ");
        dto.setUserName("  johndoe  ");
        dto.setPassword("password");
        dto.setGenderId(1L);
        dto.setAcademicDegreeId(1L);

        // When
        studentService.validateStudent(dto);

        // Then
        assertEquals("John", dto.getFirstName());
        assertEquals("Doe", dto.getLastName());
        assertEquals("+1", dto.getCountryCode());
        assertEquals("1234567890", dto.getMobileNumber());
        assertEquals("johndoe", dto.getUserName());
        assertNotEquals("password", dto.getPassword()); // Password should be hashed
    }

    @Test
    @DisplayName("Should throw exception when first name is empty")
    void testValidateStudentWithEmptyFirstName() {
        // Given
        StudentDto dto = new StudentDto();
        dto.setFirstName("");
        dto.setLastName("Doe");
        dto.setCountryCode("+1");
        dto.setMobileNumber("1234567890");
        dto.setUserName("johndoe");
        dto.setPassword("password");
        dto.setGenderId(1L);
        dto.setAcademicDegreeId(1L);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> studentService.validateStudent(dto));
        assertEquals("Student name cannot be null or empty", exception.getMessage());
        verify(exceptionHandlingService, times(1)).handleException(any(IllegalArgumentException.class));
    }

    @Test
    @DisplayName("Should throw exception when mobile number is empty")
    void testValidateStudentWithEmptyMobileNumber() {
        // Given
        StudentDto dto = new StudentDto();
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setCountryCode("+1");
        dto.setMobileNumber("");
        dto.setUserName("johndoe");
        dto.setPassword("password");
        dto.setGenderId(1L);
        dto.setAcademicDegreeId(1L);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> studentService.validateStudent(dto));
        assertEquals("Student Mobile Number cannot be null or empty", exception.getMessage());
        verify(exceptionHandlingService, times(1)).handleException(any(IllegalArgumentException.class));
    }

    @Test
    @DisplayName("Should throw exception when username is empty")
    void testValidateStudentWithEmptyUsername() {
        // Given
        StudentDto dto = new StudentDto();
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setCountryCode("+1");
        dto.setMobileNumber("1234567890");
        dto.setUserName("");
        dto.setPassword("password");
        dto.setGenderId(1L);
        dto.setAcademicDegreeId(1L);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> studentService.validateStudent(dto));
        assertEquals("User name cannot be null or empty", exception.getMessage());
        verify(exceptionHandlingService, times(1)).handleException(any(IllegalArgumentException.class));
    }

    @Test
    @DisplayName("Should throw exception when password is null")
    void testValidateStudentWithNullPassword() {
        // Given
        StudentDto dto = new StudentDto();
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setCountryCode("+1");
        dto.setMobileNumber("1234567890");
        dto.setUserName("johndoe");
        dto.setPassword(null);
        dto.setGenderId(1L);
        dto.setAcademicDegreeId(1L);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> studentService.validateStudent(dto));
        assertEquals("Password cannot be null", exception.getMessage());
        verify(exceptionHandlingService, times(1)).handleException(any(IllegalArgumentException.class));
    }

    @Test
    @DisplayName("Should save student successfully")
    void testSaveStudent() throws Exception {
        // Given
        String otp = "123456";
        Character archived = 'N';

        when(genderService.getGenderById(1L)).thenReturn(gender);
        when(roleService.getRoleById(4L)).thenReturn(role);
        when(academicDegreeService.getAcademicDegreeById(1L)).thenReturn(academicDegree);
        when(criticalThinkingService.saveCriticalThinking(any(CriticalThinkingDto.class))).thenReturn(criticalThinking);
        when(accuracyService.saveAccuracy(any(AccuracyDto.class))).thenReturn(accuracy);
        when(timeSpentService.saveTimeSpent(any(TimeSpentDto.class))).thenReturn(timeSpent);
        when(understandingService.saveUnderstanding(any(UnderstandingDto.class))).thenReturn(understanding);
        when(memoryService.saveMemory(any(MemoryDto.class))).thenReturn(memory);
        when(entityManager.merge(any(Student.class))).thenReturn(student);

        // When
        Student result = studentService.saveStudent(validDto, otp, archived);

        // Then
        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals("johndoe", result.getUserName());
        verify(entityManager, times(1)).merge(any(Student.class));
    }

    @Test
    @DisplayName("Should handle persistence exception when saving student")
    void testSaveStudentWithPersistenceException() throws Exception {
        // Given
        String otp = "123456";
        Character archived = 'N';

        when(genderService.getGenderById(1L)).thenReturn(gender);
        when(roleService.getRoleById(4L)).thenReturn(role);
        when(academicDegreeService.getAcademicDegreeById(1L)).thenReturn(academicDegree);
        when(criticalThinkingService.saveCriticalThinking(any(CriticalThinkingDto.class))).thenReturn(criticalThinking);
        when(accuracyService.saveAccuracy(any(AccuracyDto.class))).thenReturn(accuracy);
        when(timeSpentService.saveTimeSpent(any(TimeSpentDto.class))).thenReturn(timeSpent);
        when(understandingService.saveUnderstanding(any(UnderstandingDto.class))).thenReturn(understanding);
        when(memoryService.saveMemory(any(MemoryDto.class))).thenReturn(memory);
        when(entityManager.merge(any(Student.class))).thenThrow(new PersistenceException("Database error"));

        // When & Then
        PersistenceException exception = assertThrows(PersistenceException.class,
                () -> studentService.saveStudent(validDto, otp, archived));
        assertEquals("Database error", exception.getMessage());
        verify(exceptionHandlingService, times(1)).handleException(any(PersistenceException.class));
    }

    @Test
    @DisplayName("Should get all students")
    void testGetAllStudent() throws Exception {
        // Given
        List<Student> students = Collections.singletonList(student);
        when(entityManager.createQuery(anyString(), eq(Student.class))).thenReturn(studentTypedQuery);
        when(studentTypedQuery.getResultList()).thenReturn(students);

        // When
        List<Student> result = studentService.getAllStudent();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("John", result.get(0).getFirstName());
        verify(entityManager, times(1)).createQuery(anyString(), eq(Student.class));
        verify(studentTypedQuery, times(1)).getResultList();
    }

    @Test
    @DisplayName("Should get student by id")
    void testGetStudentById() throws Exception {
        // Given
        List<Student> students = Collections.singletonList(student);
        when(entityManager.createQuery(anyString(), eq(Student.class))).thenReturn(studentTypedQuery);
        when(studentTypedQuery.setParameter(eq("studentId"), eq(1L))).thenReturn(studentTypedQuery);
        when(studentTypedQuery.getResultList()).thenReturn(students);

        // When
        Student result = studentService.getStudentById(1L);

        // Then
        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        verify(entityManager, times(1)).createQuery(anyString(), eq(Student.class));
        verify(studentTypedQuery, times(1)).setParameter(eq("studentId"), eq(1L));
        verify(studentTypedQuery, times(1)).getResultList();
    }

    @Test
    @DisplayName("Should throw exception when student not found by id")
    void testGetStudentByIdNotFound() {
        // Given
        when(entityManager.createQuery(anyString(), eq(Student.class))).thenReturn(studentTypedQuery);
        when(studentTypedQuery.setParameter(eq("studentId"), eq(99L))).thenReturn(studentTypedQuery);
        when(studentTypedQuery.getResultList()).thenReturn(Collections.emptyList());

        // When & Then
        IndexOutOfBoundsException exception = assertThrows(IndexOutOfBoundsException.class,
                () -> studentService.getStudentById(99L));
        assertEquals("Student not found with given Id", exception.getMessage());
        verify(exceptionHandlingService, times(1)).handleException(any(IndexOutOfBoundsException.class));
    }

    @Test
    @DisplayName("Should retrieve student by username")
    void testRetrieveStudentByUsername() {
        // Given
        String username = "john.doe@college.edu";
        List<Student> students = Collections.singletonList(student);
        when(entityManager.createQuery(anyString(), eq(Student.class))).thenReturn(studentTypedQuery);
        when(studentTypedQuery.setParameter(eq("username"), eq(username))).thenReturn(studentTypedQuery);
        when(studentTypedQuery.getResultList()).thenReturn(students);

        // When
        Student result = studentService.retrieveStudentByUsername(username);

        // Then
        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        verify(entityManager, times(1)).createQuery(anyString(), eq(Student.class));
        verify(studentTypedQuery, times(1)).setParameter(eq("username"), eq(username));
        verify(studentTypedQuery, times(1)).getResultList();
    }

    @Test
    @DisplayName("Should throw exception when student not found by username")
    void testRetrieveStudentByUsernameNotFound() {
        // Given
        String username = "unknown@college.edu";
        when(entityManager.createQuery(anyString(), eq(Student.class))).thenReturn(studentTypedQuery);
        when(studentTypedQuery.setParameter(eq("username"), eq(username))).thenReturn(studentTypedQuery);
        when(studentTypedQuery.getResultList()).thenReturn(Collections.emptyList());

        // When & Then
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                () -> studentService.retrieveStudentByUsername(username));
        assertEquals("Student not found with username: " + username, exception.getMessage());
    }

    @Test
    @DisplayName("Should update student successfully")
    void testUpdateStudent() throws Exception {
        // Given
        Long studentId = 1L;
        StudentDto updateDto = new StudentDto();
        updateDto.setFirstName("Jane");
        updateDto.setGenderId(2L);

        Gender newGender = new Gender();
        newGender.setGenderId(2L);
        newGender.setGenderName("Female");

        when(entityManager.find(Student.class, studentId)).thenReturn(student);
        when(genderService.getGenderById(2L)).thenReturn(newGender);
        when(entityManager.merge(any(Student.class))).thenReturn(student);

        // When
        Student result = studentService.updateStudent(studentId, updateDto);

        // Then
        assertNotNull(result);
        verify(entityManager, times(1)).find(Student.class, studentId);
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent student")
    void testUpdateNonExistentStudent() {
        // Given
        Long studentId = 99L;
        StudentDto updateDto = new StudentDto();
        updateDto.setFirstName("Jane");

        when(entityManager.find(Student.class, studentId)).thenReturn(null);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> studentService.updateStudent(studentId, updateDto));
        assertEquals("Student with id " + studentId + " not found", exception.getMessage());
        verify(exceptionHandlingService, times(1)).handleException(any(IllegalArgumentException.class));
    }

    @Test
    @DisplayName("Should filter students successfully")
    void testFilterStudents() throws Exception {
        // Given
        String username = "johndoe";
        Long studentId = 1L;
        String personalEmail = "john.doe@example.com";
        List<Student> students = Collections.singletonList(student);

        when(entityManager.createQuery(anyString(), eq(Student.class))).thenReturn(studentTypedQuery);
        when(studentTypedQuery.setParameter(eq("username"), eq(username))).thenReturn(studentTypedQuery);
        when(studentTypedQuery.setParameter(eq("studentId"), eq(studentId))).thenReturn(studentTypedQuery);
        when(studentTypedQuery.setParameter(eq("personalEmail"), eq(personalEmail))).thenReturn(studentTypedQuery);
        when(studentTypedQuery.getResultList()).thenReturn(students);

        // When
        List<Student> result = studentService.filterStudents(username, studentId, personalEmail);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("John", result.get(0).getFirstName());
        verify(entityManager, times(1)).createQuery(anyString(), eq(Student.class));
        verify(studentTypedQuery, times(1)).getResultList();
    }

    @Test
    @DisplayName("Should throw exception when submitting assignment for non-existent assignment")
    void testSubmitAssignmentNonExistentAssignment() {
        // Given
        Long assignmentId = 99L;
        Long studentId = 1L;
        String submissionText = "This is my submission";

        when(entityManager.find(Assignment.class, assignmentId)).thenReturn(null);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> studentService.submitAssignment(assignmentId, studentId, submissionText));
        assertEquals("Assignment with id " + studentId + " not found", exception.getMessage());
        verify(exceptionHandlingService, times(1)).handleException(any(IllegalArgumentException.class));
    }
}