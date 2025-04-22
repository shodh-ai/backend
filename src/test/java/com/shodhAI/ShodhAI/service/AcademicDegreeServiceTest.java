package com.shodhAI.ShodhAI.service;

import com.shodhAI.ShodhAI.Dto.AcademicDegreeDto;
import com.shodhAI.ShodhAI.Entity.AcademicDegree;
import com.shodhAI.ShodhAI.Entity.CourseSemesterDegree;
import com.shodhAI.ShodhAI.Entity.Semester;
import com.shodhAI.ShodhAI.Service.AcademicDegreeService;
import com.shodhAI.ShodhAI.Service.ExceptionHandlingService;
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

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AcademicDegreeServiceTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private ExceptionHandlingService exceptionHandlingService;

    @Mock
    private TypedQuery<AcademicDegree> academicDegreeTypedQuery;

    @Mock
    private TypedQuery<Long> longTypedQuery;

    @InjectMocks
    private AcademicDegreeService academicDegreeService;

    private AcademicDegreeDto validDto;
    private AcademicDegree academicDegree;

    @BeforeEach
    void setUp() {
        validDto = new AcademicDegreeDto();
        validDto.setDegreeName("BSc");
        validDto.setInstitutionName("University");
        validDto.setProgramName("CS");
        List<Long> semesterIds = new ArrayList<>();
        semesterIds.add(1L);
        validDto.setSemesterIds(semesterIds);

        academicDegree = new AcademicDegree();
        academicDegree.setDegreeId(1L);
        academicDegree.setDegreeName("BSc");
        academicDegree.setInstitutionName("University");
        academicDegree.setProgramName("CS");
        academicDegree.setSemesters(new ArrayList<>());
        academicDegree.setCourseSemesterDegrees(new ArrayList<>());
    }

    @Test
    @DisplayName("Should validate academic degree successfully")
    void testValidateAcademicDegree() throws Exception {
        // Given
        AcademicDegreeDto dto = new AcademicDegreeDto();
        dto.setDegreeName("  BSc  ");
        dto.setInstitutionName("  University  ");
        dto.setProgramName("  CS  ");

        // When
        academicDegreeService.validateAcademicDegree(dto);

        // Then
        assertEquals("BSc", dto.getDegreeName());
        assertEquals("University", dto.getInstitutionName());
        assertEquals("CS", dto.getProgramName());
    }

    @Test
    @DisplayName("Should throw exception when degree name is empty")
    void testValidateAcademicDegreeWithEmptyName() {
        // Given
        AcademicDegreeDto dto = new AcademicDegreeDto();
        dto.setDegreeName("");
        dto.setInstitutionName("University");
        dto.setProgramName("CS");

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> academicDegreeService.validateAcademicDegree(dto));
        assertEquals("Degree name cannot be null or empty", exception.getMessage());
        verify(exceptionHandlingService, times(1)).handleException(any(IllegalArgumentException.class));
    }

    @Test
    @DisplayName("Should throw exception when institution name is empty")
    void testValidateAcademicDegreeWithEmptyInstitution() {
        // Given
        AcademicDegreeDto dto = new AcademicDegreeDto();
        dto.setDegreeName("BSc");
        dto.setInstitutionName("");
        dto.setProgramName("CS");

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> academicDegreeService.validateAcademicDegree(dto));
        assertEquals("Institute name cannot be empty", exception.getMessage());
        verify(exceptionHandlingService, times(1)).handleException(any(IllegalArgumentException.class));
    }

    @Test
    @DisplayName("Should throw exception when program name is empty")
    void testValidateAcademicDegreeWithEmptyProgram() {
        // Given
        AcademicDegreeDto dto = new AcademicDegreeDto();
        dto.setDegreeName("BSc");
        dto.setInstitutionName("University");
        dto.setProgramName("");

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> academicDegreeService.validateAcademicDegree(dto));
        assertEquals("Program name cannot be null or empty", exception.getMessage());
        verify(exceptionHandlingService, times(1)).handleException(any(IllegalArgumentException.class));
    }

    @Test
    @DisplayName("Should save academic degree successfully")
    void testSaveAcademicDegree() throws Exception {
        // Given
        when(entityManager.createQuery(anyString(), eq(Long.class))).thenReturn(longTypedQuery);
        when(longTypedQuery.getSingleResult()).thenReturn(0L);
        when(entityManager.merge(any(AcademicDegree.class))).thenReturn(academicDegree);

        // When
        AcademicDegree result = academicDegreeService.saveAcademicDegree(validDto);

        // Then
        assertNotNull(result);
        assertEquals("BSc", result.getDegreeName());
        assertEquals("University", result.getInstitutionName());
        assertEquals("CS", result.getProgramName());
        assertEquals(1L, result.getDegreeId());

        verify(entityManager, times(1)).createQuery(anyString(), eq(Long.class));
        verify(longTypedQuery, times(1)).getSingleResult();
        verify(entityManager, times(1)).merge(any(AcademicDegree.class));
    }

    @Test
    @DisplayName("Should handle persistence exception when saving academic degree")
    void testSaveAcademicDegreeWithPersistenceException() throws Exception {
        // Given
        when(entityManager.createQuery(anyString(), eq(Long.class))).thenReturn(longTypedQuery);
        when(longTypedQuery.getSingleResult()).thenReturn(0L);
        when(entityManager.merge(any(AcademicDegree.class))).thenThrow(new PersistenceException("Database error"));

        // When & Then
        PersistenceException exception = assertThrows(PersistenceException.class,
                () -> academicDegreeService.saveAcademicDegree(validDto));
        assertEquals("Database error", exception.getMessage());
        verify(exceptionHandlingService, times(1)).handleException(any(PersistenceException.class));
    }

    @Test
    @DisplayName("Should get all academic degrees")
    void testGetAllAcademicDegree() throws Exception {
        // Given
        List<AcademicDegree> degrees = Arrays.asList(academicDegree);
        when(entityManager.createQuery(anyString(), eq(AcademicDegree.class))).thenReturn(academicDegreeTypedQuery);
        when(academicDegreeTypedQuery.getResultList()).thenReturn(degrees);

        // When
        List<AcademicDegree> result = academicDegreeService.getAllAcademicDegree();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("BSc", result.get(0).getDegreeName());
        verify(entityManager, times(1)).createQuery(anyString(), eq(AcademicDegree.class));
        verify(academicDegreeTypedQuery, times(1)).getResultList();
    }

    @Test
    @DisplayName("Should get academic degree by id")
    void testGetAcademicDegreeById() throws Exception {
        // Given
        List<AcademicDegree> degrees = Arrays.asList(academicDegree);
        when(entityManager.createQuery(anyString(), eq(AcademicDegree.class))).thenReturn(academicDegreeTypedQuery);
        when(academicDegreeTypedQuery.setParameter(eq("degreeId"), eq(1L))).thenReturn(academicDegreeTypedQuery);
        when(academicDegreeTypedQuery.getResultList()).thenReturn(degrees);

        // When
        AcademicDegree result = academicDegreeService.getAcademicDegreeById(1L);

        // Then
        assertNotNull(result);
        assertEquals("BSc", result.getDegreeName());
        verify(entityManager, times(1)).createQuery(anyString(), eq(AcademicDegree.class));
        verify(academicDegreeTypedQuery, times(1)).setParameter(eq("degreeId"), eq(1L));
        verify(academicDegreeTypedQuery, times(1)).getResultList();
    }

    @Test
    @DisplayName("Should throw exception when academic degree not found by id")
    void testGetAcademicDegreeByIdNotFound() {
        // Given
        when(entityManager.createQuery(anyString(), eq(AcademicDegree.class))).thenReturn(academicDegreeTypedQuery);
        when(academicDegreeTypedQuery.setParameter(eq("degreeId"), eq(99L))).thenReturn(academicDegreeTypedQuery);
        when(academicDegreeTypedQuery.getResultList()).thenReturn(Collections.emptyList());

        // When & Then
        IndexOutOfBoundsException exception = assertThrows(IndexOutOfBoundsException.class,
                () -> academicDegreeService.getAcademicDegreeById(99L));
        assertEquals("Degree not found with given Id", exception.getMessage());
        verify(exceptionHandlingService, times(1)).handleException(any(IndexOutOfBoundsException.class));
    }

    @Test
    @DisplayName("Should update academic degree successfully")
    void testUpdateAcademicDegree() throws Exception {
        // Given
        Semester semester = new Semester();
        semester.setSemesterId(1L);
        semester.setAcademicDegrees(new ArrayList<>() {
        });

        AcademicDegree existingDegree = new AcademicDegree();
        existingDegree.setDegreeId(1L);
        existingDegree.setDegreeName("Old Degree");
        existingDegree.setInstitutionName("Old University");
        existingDegree.setProgramName("Old Program");
        List<Semester> semesters = new ArrayList<>();
        semesters.add(semester);
        existingDegree.setSemesters(semesters);
        existingDegree.setCourseSemesterDegrees(new ArrayList<>());

        when(entityManager.find(AcademicDegree.class, 1L)).thenReturn(existingDegree);
        when(entityManager.find(Semester.class, 1L)).thenReturn(semester);
        when(entityManager.merge(any(AcademicDegree.class))).thenReturn(existingDegree);
        when(entityManager.merge(any(Semester.class))).thenReturn(semester);

        // When
        AcademicDegree result = academicDegreeService.updateAcademicDegree(1L, validDto);

        // Then
        assertNotNull(result);
        assertEquals("BSc", result.getDegreeName());
        verify(entityManager, times(1)).find(AcademicDegree.class, 1L);
        verify(entityManager, times(1)).find(Semester.class, 1L);
        verify(entityManager, atLeastOnce()).merge(any(AcademicDegree.class));
        verify(entityManager, atLeastOnce()).merge(any(Semester.class));
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent academic degree")
    void testUpdateNonExistentAcademicDegree() {
        // Given
        when(entityManager.find(AcademicDegree.class, 99L)).thenReturn(null);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> academicDegreeService.updateAcademicDegree(99L, validDto));
        assertEquals("Academic degree with id 99 does not exist", exception.getMessage());
        verify(exceptionHandlingService, times(1)).handleException(any(IllegalArgumentException.class));
    }

    @Test
    @DisplayName("Should filter academic degrees successfully")
    void testAcademicDegreeFilter() throws Exception {
        // Given
        List<AcademicDegree> degrees = Arrays.asList(academicDegree);
        when(entityManager.createQuery(anyString(), eq(AcademicDegree.class))).thenReturn(academicDegreeTypedQuery);
        when(academicDegreeTypedQuery.getResultList()).thenReturn(degrees);

        // When
        List<AcademicDegree> result = academicDegreeService.academicDegreeFilter(1L, 1L, 1L);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(entityManager, times(1)).createQuery(anyString(), eq(AcademicDegree.class));
        verify(academicDegreeTypedQuery, times(1)).getResultList();
    }

    @Test
    @DisplayName("Should find academic degree count")
    void testFindAcademicDegreeCount() throws Exception {
        // Given
        when(entityManager.createQuery(anyString(), eq(Long.class))).thenReturn(longTypedQuery);
        when(longTypedQuery.getSingleResult()).thenReturn(10L);

        // When
        long result = academicDegreeService.findAcademicDegreeCount();

        // Then
        assertEquals(10L, result);
        verify(entityManager, times(1)).createQuery(anyString(), eq(Long.class));
        verify(longTypedQuery, times(1)).getSingleResult();
    }

    @Test
    @DisplayName("Should return 0 when no academic degrees found")
    void testFindAcademicDegreeCountEmpty() throws Exception {
        // Given
        when(entityManager.createQuery(anyString(), eq(Long.class))).thenReturn(longTypedQuery);
        when(longTypedQuery.getSingleResult()).thenReturn(null);

        // When
        long result = academicDegreeService.findAcademicDegreeCount();

        // Then
        assertEquals(0L, result);
        verify(entityManager, times(1)).createQuery(anyString(), eq(Long.class));
        verify(longTypedQuery, times(1)).getSingleResult();
    }

    @Test
    @DisplayName("Should handle no result exception when finding academic degree count")
    void testFindAcademicDegreeCountNoResult() {
        // Given
        when(entityManager.createQuery(anyString(), eq(Long.class))).thenReturn(longTypedQuery);
        when(longTypedQuery.getSingleResult()).thenThrow(new NoResultException("No degree is found"));

        // When & Then
        NoResultException exception = assertThrows(NoResultException.class,
                () -> academicDegreeService.findAcademicDegreeCount());
        assertEquals("No degree is found", exception.getMessage());
        verify(exceptionHandlingService, times(1)).handleException(any(NoResultException.class));
    }

    @Test
    @DisplayName("Should delete academic degree by id")
    void testDeleteAcademicDegreeById() throws Exception {
        // Given
        when(entityManager.find(AcademicDegree.class, 1L)).thenReturn(academicDegree);
        when(entityManager.merge(academicDegree)).thenReturn(academicDegree);

        // When
        AcademicDegree result = academicDegreeService.deleteAcademicDegreeById(1L);

        // Then
        assertNotNull(result);
        assertEquals('Y', result.getArchived());
        verify(entityManager, times(1)).find(AcademicDegree.class, 1L);
        verify(entityManager, times(1)).merge(academicDegree);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent academic degree")
    void testDeleteNonExistentAcademicDegree() {
        // Given
        when(entityManager.find(AcademicDegree.class, 99L)).thenReturn(null);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> academicDegreeService.deleteAcademicDegreeById(99L));
        assertEquals("Academic Degree with id 99 not found", exception.getMessage());
    }

    @Test
    @DisplayName("Should update and save academic degree")
    void testUpdateAndSaveAcademicDegree() throws Exception {
        // Given
        Semester semester = new Semester();
        semester.setSemesterId(1L);
        semester.setAcademicDegrees(new ArrayList<>());

        AcademicDegree toUpdate = new AcademicDegree();
        toUpdate.setDegreeId(1L);
        toUpdate.setDegreeName("Old Degree");
        toUpdate.setInstitutionName("Old University");
        toUpdate.setProgramName("Old Program");
        toUpdate.setSemesters(new ArrayList<>());
        toUpdate.setCourseSemesterDegrees(new ArrayList<>());

        when(entityManager.find(Semester.class, 1L)).thenReturn(semester);
        when(entityManager.merge(any(AcademicDegree.class))).thenReturn(toUpdate);
        when(entityManager.merge(any(Semester.class))).thenReturn(semester);

        // When
        AcademicDegree result = academicDegreeService.updateAndSaveAcademicDegree(validDto, toUpdate);

        // Then
        assertNotNull(result);
        assertEquals("BSc", result.getDegreeName());
        verify(entityManager, times(1)).find(Semester.class, 1L);
        verify(entityManager, atLeastOnce()).merge(any(AcademicDegree.class));
        verify(entityManager, atLeastOnce()).merge(any(Semester.class));
    }

    @Test
    @DisplayName("Should throw exception when updating with invalid data")
    void testUpdateAndSaveAcademicDegreeWithInvalidData() {
        // Given
        AcademicDegreeDto invalidDto = new AcademicDegreeDto();
        invalidDto.setDegreeName("");

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> academicDegreeService.updateAndSaveAcademicDegree(invalidDto, academicDegree));
        assertEquals("Degree name cannot be null or empty", exception.getMessage());
        verify(exceptionHandlingService, times(1)).handleException(any(IllegalArgumentException.class));
    }
}