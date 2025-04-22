package com.shodhAI.ShodhAI.service;

import com.shodhAI.ShodhAI.Dto.FacultyDto;
import com.shodhAI.ShodhAI.Entity.*;
import com.shodhAI.ShodhAI.Service.AcademicDegreeService;
import com.shodhAI.ShodhAI.Service.CourseService;
import com.shodhAI.ShodhAI.Service.ExceptionHandlingService;
import com.shodhAI.ShodhAI.Service.FacultyService;
import com.shodhAI.ShodhAI.Service.GenderService;
import com.shodhAI.ShodhAI.Service.RoleService;
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

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FacultyServiceTest {
    @Mock
    private EntityManager entityManager;

    @Mock
    private
    ExceptionHandlingService exceptionHandlingService;

    @Mock
    private TypedQuery<AcademicDegree> academicDegreeTypedQuery;

    @Mock
    private TypedQuery<Long> longTypedQuery;

    @Mock
    private GenderService genderService;

    @Mock
    private RoleService roleService;

    @Mock
    private AcademicDegreeService academicDegreeService;

    @Mock
    private CourseService courseService;

    @Mock
    private TypedQuery<Faculty> facultyTypedQuery;

    @Mock
    private TypedQuery<Course> courseTypedQuery;

    @InjectMocks
    private FacultyService facultyService;

    private FacultyDto validDto;
    private Faculty faculty;
    private Gender gender;
    private Role role;

    @BeforeEach
    void setUp() {
        validDto = new FacultyDto();
        validDto.setFirstName("Jane");
        validDto.setLastName("Smith");
        validDto.setUserName("janesmith");
        validDto.setCountryCode("+1");
        validDto.setMobileNumber("0987654321");
        validDto.setPassword("password");
        validDto.setGenderId(2L);
        validDto.setCollegeEmail("jane.smith@college.edu");
        validDto.setPersonalEmail("jane.smith@example.com");

        gender = new Gender();
        gender.setGenderId(2L);
        gender.setGenderName("Female");

        role = new Role();
        role.setRoleId(3L);
        role.setRoleName("FACULTY");

        faculty = new Faculty();
        faculty.setId(1L);
        faculty.setFirstName("Jane");
        faculty.setLastName("Smith");
        faculty.setUserName("janesmith");
        faculty.setCountryCode("+1");
        faculty.setMobileNumber("0987654321");
        faculty.setPassword("hashedPassword");
        faculty.setGender(gender);
        faculty.setRole(role);
        faculty.setCollegeEmail("jane.smith@college.edu");
        faculty.setPersonalEmail("jane.smith@example.com");
        faculty.setCourses(new ArrayList<>());
        faculty.setStudents(new ArrayList<>());
    }

    @Test
    @DisplayName("Should validate faculty successfully")
    void testValidateFaculty() throws Exception {
        // Given
        FacultyDto dto = new FacultyDto();
        dto.setFirstName("  Jane  ");
        dto.setLastName("  Smith  ");
        dto.setCountryCode("  +1  ");
        dto.setMobileNumber("  0987654321  ");
        dto.setUserName("  janesmith  ");
        dto.setPassword("password");
        dto.setGenderId(2L);

        // When
        facultyService.validateFaculty(dto);

        // Then
        assertEquals("Jane", dto.getFirstName());
        assertEquals("Smith", dto.getLastName());
        assertEquals("+1", dto.getCountryCode());
        assertEquals("0987654321", dto.getMobileNumber());
        assertEquals("janesmith", dto.getUserName());
        assertNotEquals("password", dto.getPassword()); // Password should be hashed
    }

    @Test
    @DisplayName("Should throw exception when first name is empty")
    void testValidateFacultyWithEmptyFirstName() {
        // Given
        FacultyDto dto = new FacultyDto();
        dto.setFirstName("");
        dto.setLastName("Smith");
        dto.setCountryCode("+1");
        dto.setMobileNumber("0987654321");
        dto.setUserName("janesmith");
        dto.setPassword("password");
        dto.setGenderId(2L);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> facultyService.validateFaculty(dto));
        assertEquals("Faculty name cannot be null or empty", exception.getMessage());
        verify(exceptionHandlingService, times(1)).handleException(any(IllegalArgumentException.class));
    }

    @Test
    @DisplayName("Should throw exception when mobile number is empty")
    void testValidateFacultyWithEmptyMobileNumber() {
        // Given
        FacultyDto dto = new FacultyDto();
        dto.setFirstName("Jane");
        dto.setLastName("Smith");
        dto.setCountryCode("+1");
        dto.setMobileNumber("");
        dto.setUserName("janesmith");
        dto.setPassword("password");
        dto.setGenderId(2L);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> facultyService.validateFaculty(dto));
        assertEquals("Faculty Mobile Number cannot be null or empty", exception.getMessage());
        verify(exceptionHandlingService, times(1)).handleException(any(IllegalArgumentException.class));
    }

    @Test
    @DisplayName("Should throw exception when username is empty")
    void testValidateFacultyWithEmptyUsername() {
        // Given
        FacultyDto dto = new FacultyDto();
        dto.setFirstName("Jane");
        dto.setLastName("Smith");
        dto.setCountryCode("+1");
        dto.setMobileNumber("0987654321");
        dto.setUserName("");
        dto.setPassword("password");
        dto.setGenderId(2L);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> facultyService.validateFaculty(dto));
        assertEquals("User name cannot be null or empty", exception.getMessage());
        verify(exceptionHandlingService, times(1)).handleException(any(IllegalArgumentException.class));
    }

    @Test
    @DisplayName("Should throw exception when password is null")
    void testValidateFacultyWithNullPassword() {
        // Given
        FacultyDto dto = new FacultyDto();
        dto.setFirstName("Jane");
        dto.setLastName("Smith");
        dto.setCountryCode("+1");
        dto.setMobileNumber("0987654321");
        dto.setUserName("janesmith");
        dto.setPassword(null);
        dto.setGenderId(2L);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> facultyService.validateFaculty(dto));
        assertEquals("Password cannot be null", exception.getMessage());
        verify(exceptionHandlingService, times(1)).handleException(any(IllegalArgumentException.class));
    }

    @Test
    @DisplayName("Should save faculty successfully")
    void testSaveFaculty() throws Exception {
        // Given
        String otp = "654321";
        Character archived = 'N';

        when(genderService.getGenderById(2L)).thenReturn(gender);
        when(roleService.getRoleById(3L)).thenReturn(role);
        when(entityManager.merge(any(Faculty.class))).thenReturn(faculty);

        // When
        Faculty result = facultyService.saveFaculty(validDto, otp, archived);

        // Then
        assertNotNull(result);
        assertEquals("Jane", result.getFirstName());
        assertEquals("Smith", result.getLastName());
        assertEquals("janesmith", result.getUserName());
        verify(entityManager, times(1)).merge(any(Faculty.class));
    }

}