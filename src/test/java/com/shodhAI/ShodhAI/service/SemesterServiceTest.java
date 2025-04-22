package com.shodhAI.ShodhAI.service;

import com.shodhAI.ShodhAI.Component.Constant;
import com.shodhAI.ShodhAI.Dto.CourseSemesterDegreeDto;
import com.shodhAI.ShodhAI.Dto.SemesterDto;
import com.shodhAI.ShodhAI.Entity.*;
import com.shodhAI.ShodhAI.Service.ExceptionHandlingService;
import com.shodhAI.ShodhAI.Service.RoleService;
import com.shodhAI.ShodhAI.Service.SemesterService;
import com.shodhAI.ShodhAI.Service.SharedUtilityService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SemesterServiceTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private ExceptionHandlingService exceptionHandlingService;

    @Mock
    private SharedUtilityService sharedUtilityService;

    @Mock
    private RoleService roleService;

    @Mock
    private TypedQuery<Semester> semesterTypedQuery;

    @Mock
    private TypedQuery<Long> longTypedQuery;

    @InjectMocks
    private SemesterService semesterService;

    private SemesterDto validDto;
    private Semester semester;
    private Date startDate;
    private Date endDate;
    private final String DATE_FORMAT = "dd-MM-yyyy";

    @BeforeEach
    void setUp() throws ParseException {
        // Setup valid semester DTO
        validDto = new SemesterDto();
        validDto.setSemesterName("Fall 2024");
        validDto.setStartDate("01-08-2024");
        validDto.setEndDate("31-12-2024");
        List<Long> academicDegreeIds = new ArrayList<>();
        academicDegreeIds.add(1L);
        validDto.setAcademicDegreeIds(academicDegreeIds);
        
        // Setup dates
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        startDate = dateFormat.parse("01-08-2024");
        endDate = dateFormat.parse("31-12-2024");
        
        // Setup semester entity
        semester = new Semester();
        semester.setSemesterId(1L);
        semester.setSemesterName("Fall 2024");
        semester.setStartDate(startDate);
        semester.setEndDate(endDate);
        semester.setAcademicDegrees(new ArrayList<>());
    }

    @Test
    @DisplayName("Should validate semester successfully")
    void testValidateSemester() throws Exception {
        // Given
        SemesterDto dto = new SemesterDto();
        dto.setSemesterName("  Fall 2024  ");
        dto.setStartDate("  01-08-2024  ");
        dto.setEndDate("  31-12-2024  ");
        
        doNothing().when(sharedUtilityService).compareTwoDates(any(Date.class), any(Date.class), anyString());

        // When
        semesterService.validateSemester(dto);

        // Then
        assertEquals("Fall 2024", dto.getSemesterName());
        assertEquals("01-08-2024", dto.getStartDate());
        assertEquals("31-12-2024", dto.getEndDate());
        verify(sharedUtilityService, times(2)).validateDate(anyString(), eq(DATE_FORMAT), anyString());
        verify(sharedUtilityService, times(1)).compareTwoDates(any(Date.class), any(Date.class), eq("Semester"));
    }

    @Test
    @DisplayName("Should throw exception when semester name is empty")
    void testValidateSemesterWithEmptyName() {
        // Given
        SemesterDto dto = new SemesterDto();
        dto.setSemesterName("");
        dto.setStartDate("01-08-2024");
        dto.setEndDate("31-12-2024");

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> semesterService.validateSemester(dto));
        assertEquals("Semester name cannot be null or empty", exception.getMessage());
        verify(exceptionHandlingService, times(1)).handleException(any(IllegalArgumentException.class));
    }

    @Test
    @DisplayName("Should throw exception when start date is empty")
    void testValidateSemesterWithEmptyStartDate() {
        // Given
        SemesterDto dto = new SemesterDto();
        dto.setSemesterName("Fall 2024");
        dto.setStartDate("");
        dto.setEndDate("31-12-2024");

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> semesterService.validateSemester(dto));
        assertEquals("Start date of a semester cannot be null or empty", exception.getMessage());
        verify(exceptionHandlingService, times(1)).handleException(any(IllegalArgumentException.class));
    }

    @Test
    @DisplayName("Should throw exception when end date is empty")
    void testValidateSemesterWithEmptyEndDate() throws Exception {
        // Given
        SemesterDto dto = new SemesterDto();
        dto.setSemesterName("Fall 2024");
        dto.setStartDate("01-08-2024");
        dto.setEndDate("");

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> semesterService.validateSemester(dto));
        assertEquals("End date of a semester cannot be null or empty", exception.getMessage());
        verify(exceptionHandlingService, times(1)).handleException(any(IllegalArgumentException.class));
    }

    @Test
    @DisplayName("Should save semester successfully")
    void testSaveSemester() throws Exception {
        // Given
        when(entityManager.createQuery(anyString(), eq(Long.class))).thenReturn(longTypedQuery);
        when(longTypedQuery.getSingleResult()).thenReturn(0L);
        doNothing().when(entityManager).persist(any(Semester.class));

        // When
        Semester result = semesterService.saveSemester(validDto);

        // Then
        assertNotNull(result);
        assertEquals("Fall 2024", result.getSemesterName());
        assertEquals(startDate, result.getStartDate());
        assertEquals(endDate, result.getEndDate());
        assertEquals(1L, result.getSemesterId());

        verify(entityManager, times(1)).createQuery(anyString(), eq(Long.class));
        verify(longTypedQuery, times(1)).getSingleResult();
        verify(entityManager, times(1)).persist(any(Semester.class));
    }

    @Test
    @DisplayName("Should get all semesters")
    void testGetAllSemesters() throws Exception {
        // Given
        List<Semester> semesters = Arrays.asList(semester);
        when(entityManager.createQuery(anyString(), eq(Semester.class))).thenReturn(semesterTypedQuery);
        when(semesterTypedQuery.getResultList()).thenReturn(semesters);

        // When
        List<Semester> result = semesterService.getAllSemesters();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Fall 2024", result.get(0).getSemesterName());
        verify(entityManager, times(1)).createQuery(anyString(), eq(Semester.class));
        verify(semesterTypedQuery, times(1)).getResultList();
    }

    @Test
    @DisplayName("Should get semester by id")
    void testGetSemesterById() throws Exception {
        // Given
        List<Semester> semesters = Arrays.asList(semester);
        when(entityManager.createQuery(anyString(), eq(Semester.class))).thenReturn(semesterTypedQuery);
        when(semesterTypedQuery.setParameter(eq("semesterId"), eq(1L))).thenReturn(semesterTypedQuery);
        when(semesterTypedQuery.getResultList()).thenReturn(semesters);

        // When
        Semester result = semesterService.getSemesterById(1L);

        // Then
        assertNotNull(result);
        assertEquals("Fall 2024", result.getSemesterName());
        verify(entityManager, times(1)).createQuery(anyString(), eq(Semester.class));
        verify(semesterTypedQuery, times(1)).setParameter(eq("semesterId"), eq(1L));
    }

    @Test
    @DisplayName("Should throw exception when semester not found by id")
    void testGetSemesterByIdNotFound() {
        // Given
        when(entityManager.createQuery(anyString(), eq(Semester.class))).thenReturn(semesterTypedQuery);
        when(semesterTypedQuery.setParameter(eq("semesterId"), eq(99L))).thenReturn(semesterTypedQuery);
        when(semesterTypedQuery.getResultList()).thenReturn(Collections.emptyList());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> semesterService.getSemesterById(99L));
        assertEquals("Semester with id 99 not found", exception.getMessage());
        verify(exceptionHandlingService, times(1)).handleException(any(IllegalArgumentException.class));
    }

    @Test
    @DisplayName("Should update and save semester with all fields")
    void testValidateAndSaveSemesterForUpdate() throws Exception {
        // Given
        SemesterDto updateDto = new SemesterDto();
        updateDto.setSemesterName("Updated Semester");
        updateDto.setStartDate("15-08-2024");
        updateDto.setEndDate("15-12-2024");
        List<Long> academicDegreeIds = new ArrayList<>();
        academicDegreeIds.add(1L);
        updateDto.setAcademicDegreeIds(academicDegreeIds);

        AcademicDegree degree = new AcademicDegree();
        degree.setDegreeId(1L);
        degree.setSemesters(new ArrayList<>());

        when(entityManager.find(AcademicDegree.class, 1L)).thenReturn(degree);
        when(entityManager.merge(any(AcademicDegree.class))).thenReturn(degree);
        when(entityManager.merge(any(Semester.class))).thenReturn(semester);
        
        doNothing().when(sharedUtilityService).compareTwoDates(any(Date.class), any(Date.class), anyString());

        // When
        Semester result = semesterService.validateAndSaveSemesterForUpdate(updateDto, semester);

        // Then
        assertEquals("Updated Semester", result.getSemesterName());
        verify(entityManager, times(1)).find(AcademicDegree.class, 1L);
        verify(entityManager, times(1)).merge(any(Semester.class));
        verify(entityManager, times(1)).merge(any(AcademicDegree.class));
    }

    @Test
    @DisplayName("Should update semester with partial fields")
    void testValidateAndSaveSemesterForUpdatePartial() throws Exception {
        // Given
        SemesterDto updateDto = new SemesterDto();
        updateDto.setSemesterName("Updated Semester");
        // Only updating name, not dates

        when(entityManager.merge(any(Semester.class))).thenReturn(semester);

        // When
        Semester result = semesterService.validateAndSaveSemesterForUpdate(updateDto, semester);

        // Then
        assertEquals("Updated Semester", result.getSemesterName());
        assertEquals(startDate, result.getStartDate()); // Should remain unchanged
        assertEquals(endDate, result.getEndDate()); // Should remain unchanged
        verify(entityManager, times(1)).merge(any(Semester.class));
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent semester")
    void testUpdateNonExistentSemester() {
        // Given
        when(entityManager.find(Semester.class, 99L)).thenReturn(null);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> semesterService.updateSemester(99L, validDto));
        assertEquals("Semester with id 99 does not exist", exception.getMessage());
        verify(exceptionHandlingService, times(1)).handleException(any(IllegalArgumentException.class));
    }

    @Test
    @DisplayName("Should add courses to semester successfully")
    void testAddCoursesToSemester() throws Exception {
        // Given
        List<CourseSemesterDegreeDto> courseAssociations = new ArrayList<>();
        CourseSemesterDegreeDto association = new CourseSemesterDegreeDto();
        association.setCourseId(1L);
        association.setAcademicDegreeId(1L);
        courseAssociations.add(association);
        
        Course course = new Course();
        course.setCourseId(1L);
        
        AcademicDegree degree = new AcademicDegree();
        degree.setDegreeId(1L);
        degree.setDegreeName("BSc");
        
        List<AcademicDegree> degrees = new ArrayList<>();
        degrees.add(degree);
        semester.setAcademicDegrees(degrees);
        
        when(entityManager.find(Semester.class, 1L)).thenReturn(semester);
        when(entityManager.find(Course.class, 1L)).thenReturn(course);
        when(entityManager.find(AcademicDegree.class, 1L)).thenReturn(degree);
        when(entityManager.createQuery(anyString(), eq(Long.class))).thenReturn(longTypedQuery);
        when(longTypedQuery.getSingleResult()).thenReturn(0L);
        
        TypedQuery<CourseSemesterDegree> csdQuery = mock(TypedQuery.class);
        when(entityManager.createQuery(anyString(), eq(CourseSemesterDegree.class))).thenReturn(csdQuery);
        when(csdQuery.setParameter(anyString(), any())).thenReturn(csdQuery);
        when(csdQuery.getResultList()).thenReturn(Collections.emptyList());
        
        // When
        semesterService.addCoursesToSemester(1L, courseAssociations);
        
        // Then
        verify(entityManager, times(1)).find(Semester.class, 1L);
        verify(entityManager, times(1)).find(Course.class, 1L);
        verify(entityManager, times(1)).find(AcademicDegree.class, 1L);
        verify(entityManager, times(1)).persist(any(CourseSemesterDegree.class));
    }

    @Test
    @DisplayName("Should throw exception when adding course to non-existent semester")
    void testAddCoursesToNonExistentSemester() {
        // Given
        List<CourseSemesterDegreeDto> courseAssociations = new ArrayList<>();
        CourseSemesterDegreeDto association = new CourseSemesterDegreeDto();
        association.setCourseId(1L);
        association.setAcademicDegreeId(1L);
        courseAssociations.add(association);
        
        when(entityManager.find(Semester.class, 99L)).thenReturn(null);
        
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> semesterService.addCoursesToSemester(99L, courseAssociations));
        assertEquals("Semester with id 99 does not exist", exception.getMessage());
        verify(exceptionHandlingService, times(1)).handleException(any(IllegalArgumentException.class));
    }

    @Test
    @DisplayName("Should filter semesters for admin user")
    void testSemesterFilterForAdmin() throws Exception {
        // Given
        Role adminRole = new Role();
        adminRole.setRoleName("ADMIN");
        
        List<Semester> semesters = Arrays.asList(semester);
        
        when(roleService.getRoleById(anyLong())).thenReturn(adminRole);
        when(entityManager.createQuery(anyString(), eq(Semester.class))).thenReturn(semesterTypedQuery);
        when(semesterTypedQuery.getResultList()).thenReturn(semesters);
        
        // When
        List<Semester> result = semesterService.semesterFilter(1L, 1L, 1L, null);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Fall 2024", result.get(0).getSemesterName());
        verify(roleService, times(1)).getRoleById(anyLong());
        verify(entityManager, times(1)).createQuery(anyString(), eq(Semester.class));
        verify(semesterTypedQuery, times(1)).getResultList();
    }

    @Test
    @DisplayName("Should filter semesters for student user")
    void testSemesterFilterForStudent() throws Exception {
        // Given
        Role userRole = new Role();
        userRole.setRoleName(Constant.ROLE_USER);
        
        Student student = new Student();
        AcademicDegree degree = new AcademicDegree();
        degree.setDegreeId(1L);
        student.setAcademicDegree(degree);
        
        List<Semester> semesters = Arrays.asList(semester);
        
        when(roleService.getRoleById(anyLong())).thenReturn(userRole);
        when(entityManager.find(Student.class, 1L)).thenReturn(student);
        when(entityManager.createQuery(anyString(), eq(Semester.class))).thenReturn(semesterTypedQuery);
        when(semesterTypedQuery.setParameter(eq("academicDegreeId"), eq(1L))).thenReturn(semesterTypedQuery);
        when(semesterTypedQuery.getResultList()).thenReturn(semesters);
        
        // When
        List<Semester> result = semesterService.semesterFilter(null, 1L, 1L, null);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(roleService, times(1)).getRoleById(anyLong());
        verify(entityManager, times(1)).find(Student.class, 1L);
        verify(entityManager, times(1)).createQuery(anyString(), eq(Semester.class));
        verify(semesterTypedQuery, times(1)).getResultList();
    }

    @Test
    @DisplayName("Should find semester count")
    void testFindSemesterCount() throws Exception {
        // Given
        when(entityManager.createQuery(anyString(), eq(Long.class))).thenReturn(longTypedQuery);
        when(longTypedQuery.getSingleResult()).thenReturn(5L);
        
        // When
        long result = semesterService.findSemesterCount();
        
        // Then
        assertEquals(5L, result);
        verify(entityManager, times(1)).createQuery(anyString(), eq(Long.class));
        verify(longTypedQuery, times(1)).getSingleResult();
    }

    @Test
    @DisplayName("Should return 0 when no semesters found")
    void testFindSemesterCountEmpty() throws Exception {
        // Given
        when(entityManager.createQuery(anyString(), eq(Long.class))).thenReturn(longTypedQuery);
        when(longTypedQuery.getSingleResult()).thenReturn(null);
        
        // When
        long result = semesterService.findSemesterCount();
        
        // Then
        assertEquals(0L, result);
        verify(entityManager, times(1)).createQuery(anyString(), eq(Long.class));
        verify(longTypedQuery, times(1)).getSingleResult();
    }

    @Test
    @DisplayName("Should handle no result exception when finding semester count")
    void testFindSemesterCountNoResult() {
        // Given
        when(entityManager.createQuery(anyString(), eq(Long.class))).thenReturn(longTypedQuery);
        when(longTypedQuery.getSingleResult()).thenThrow(new NoResultException("No semester is found"));
        
        // When & Then
        NoResultException exception = assertThrows(NoResultException.class,
                () -> semesterService.findSemesterCount());
        assertEquals("No semester is found", exception.getMessage());
        verify(exceptionHandlingService, times(1)).handleException(any(NoResultException.class));
    }

    @Test
    @DisplayName("Should find course semester degree count")
    void testFindCourseDegreeSemesterCount() throws Exception {
        // Given
        when(entityManager.createQuery(anyString(), eq(Long.class))).thenReturn(longTypedQuery);
        when(longTypedQuery.getSingleResult()).thenReturn(10L);
        
        // When
        long result = semesterService.findCourseDegreeSemesterCount();
        
        // Then
        assertEquals(10L, result);
        verify(entityManager, times(1)).createQuery(anyString(), eq(Long.class));
        verify(longTypedQuery, times(1)).getSingleResult();
    }

    @Test
    @DisplayName("Should convert string to date successfully")
    void testConvertStringToDate() throws ParseException {
        // Given
        String dateStr = "01-08-2024";
        
        // When
        Date result = SemesterService.convertStringToDate(dateStr, DATE_FORMAT);
        
        // Then
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        assertEquals(formatter.parse(dateStr), result);
    }

    @Test
    @DisplayName("Should throw exception when converting invalid date string")
    void testConvertInvalidStringToDate() {
        // Given
        String invalidDateStr = "32-13-2024"; // Invalid date
        
        // When & Then
        ParseException exception = assertThrows(ParseException.class,
                () -> SemesterService.convertStringToDate(invalidDateStr, DATE_FORMAT));
        assertTrue(exception.getMessage().contains("Unparseable date"));
    }

    @Test
    @DisplayName("Should throw exception when converting empty date string")
    void testConvertEmptyStringToDate() {
        // Given
        String emptyDateStr = "";
        
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> SemesterService.convertStringToDate(emptyDateStr, DATE_FORMAT));
        assertEquals("Date string cannot be null or empty", exception.getMessage());
    }
}