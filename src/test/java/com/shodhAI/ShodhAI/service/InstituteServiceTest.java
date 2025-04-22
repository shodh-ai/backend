package com.shodhAI.ShodhAI.service;

import com.shodhAI.ShodhAI.Component.Constant;
import com.shodhAI.ShodhAI.Dto.InstituteDto;
import com.shodhAI.ShodhAI.Entity.AcademicDegree;
import com.shodhAI.ShodhAI.Entity.Institute;
import com.shodhAI.ShodhAI.Service.AcademicDegreeService;
import com.shodhAI.ShodhAI.Service.ExceptionHandlingService;
import com.shodhAI.ShodhAI.Service.InstituteService;
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
public class InstituteServiceTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private ExceptionHandlingService exceptionHandlingService;

    @Mock
    private AcademicDegreeService academicDegreeService;

    @Mock
    private TypedQuery<Institute> instituteTypedQuery;

    @InjectMocks
    private InstituteService instituteService;

    private InstituteDto validDto;
    private Institute institute;
    private AcademicDegree academicDegree;

    @BeforeEach
    void setUp() {
        // Set up valid DTO
        validDto = new InstituteDto();
        validDto.setInstitutionName("Test University");
        List<Long> academicDegreeIds = new ArrayList<>();
        academicDegreeIds.add(1L);
        validDto.setAcademicDegreeIds(academicDegreeIds);

        // Set up academic degree
        academicDegree = new AcademicDegree();
        academicDegree.setDegreeId(1L);
        academicDegree.setDegreeName("BSc");
        academicDegree.setInstitutionName("Test University");
        academicDegree.setProgramName("Computer Science");

        // Set up institute
        institute = new Institute();
        institute.setInstituteId(1L);
        institute.setInstitutionName("Test University");
        institute.setCreatedDate(new Date());
        institute.setUpdatedDate(new Date());
        institute.setArchived('N');
        List<AcademicDegree> degrees = new ArrayList<>();
        degrees.add(academicDegree);
        institute.setDegrees(degrees);
    }

    @Test
    @DisplayName("Should validate institute successfully")
    void testValidateInstitute() throws Exception {
        // Given
        InstituteDto dto = new InstituteDto();
        dto.setInstitutionName("  Test University  ");
        List<Long> academicDegreeIds = new ArrayList<>();
        academicDegreeIds.add(1L);
        dto.setAcademicDegreeIds(academicDegreeIds);

        // When
        instituteService.validateInstitute(dto);

        // Then
        assertEquals("Test University", dto.getInstitutionName());
    }

    @Test
    @DisplayName("Should throw exception when institution name is empty")
    void testValidateInstituteWithEmptyName() {
        // Given
        InstituteDto dto = new InstituteDto();
        dto.setInstitutionName("");
        List<Long> academicDegreeIds = new ArrayList<>();
        academicDegreeIds.add(1L);
        dto.setAcademicDegreeIds(academicDegreeIds);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> instituteService.validateInstitute(dto));
        assertEquals("Institute name cannot be null or empty", exception.getMessage());
        verify(exceptionHandlingService, times(1)).handleException(any(IllegalArgumentException.class));
    }

    @Test
    @DisplayName("Should throw exception when institution name is null")
    void testValidateInstituteWithNullName() {
        // Given
        InstituteDto dto = new InstituteDto();
        dto.setInstitutionName(null);
        List<Long> academicDegreeIds = new ArrayList<>();
        academicDegreeIds.add(1L);
        dto.setAcademicDegreeIds(academicDegreeIds);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> instituteService.validateInstitute(dto));
        assertEquals("Institute name cannot be null or empty", exception.getMessage());
        verify(exceptionHandlingService, times(1)).handleException(any(IllegalArgumentException.class));
    }

    @Test
    @DisplayName("Should throw exception when academic degree id is invalid")
    void testValidateInstituteWithInvalidAcademicDegreeId() {
        // Given
        InstituteDto dto = new InstituteDto();
        dto.setInstitutionName("Test University");
        List<Long> academicDegreeIds = new ArrayList<>();
        academicDegreeIds.add(0L);
        dto.setAcademicDegreeIds(academicDegreeIds);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> instituteService.validateInstitute(dto));
        assertEquals("Academic Degree Id cannot be <= 0", exception.getMessage());
        verify(exceptionHandlingService, times(1)).handleException(any(IllegalArgumentException.class));
    }

    @Test
    @DisplayName("Should validate update institute successfully")
    void testValidateUpdateInstitute() throws Exception {
        // Given
        InstituteDto dto = new InstituteDto();
        dto.setInstitutionName("  Updated University  ");
        List<Long> academicDegreeIds = new ArrayList<>();
        academicDegreeIds.add(1L);
        dto.setAcademicDegreeIds(academicDegreeIds);

        // When
        instituteService.validateUpdateInstitute(dto);

        // Then
        assertEquals("Updated University", dto.getInstitutionName());
    }

    @Test
    @DisplayName("Should throw exception when institution name is empty during update")
    void testValidateUpdateInstituteWithEmptyName() {
        // Given
        InstituteDto dto = new InstituteDto();
        dto.setInstitutionName("");
        List<Long> academicDegreeIds = new ArrayList<>();
        academicDegreeIds.add(1L);
        dto.setAcademicDegreeIds(academicDegreeIds);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> instituteService.validateUpdateInstitute(dto));
        assertEquals("Institute name cannot be empty", exception.getMessage());
        verify(exceptionHandlingService, times(1)).handleException(any(IllegalArgumentException.class));
    }

    @Test
    @DisplayName("Should save institute successfully")
    void testSaveInstitute() throws Exception {
        // Given
        when(academicDegreeService.getAcademicDegreeById(1L)).thenReturn(academicDegree);
        when(entityManager.merge(any(Institute.class))).thenReturn(institute);

        // When
        Institute result = instituteService.saveInstitute(validDto);

        // Then
        assertNotNull(result);
        assertEquals("Test University", result.getInstitutionName());
        verify(academicDegreeService, times(1)).getAcademicDegreeById(1L);
        verify(entityManager, times(1)).merge(any(Institute.class));
    }

    @Test
    @DisplayName("Should handle persistence exception when saving institute")
    void testSaveInstituteWithPersistenceException() throws Exception {
        // Given
        when(academicDegreeService.getAcademicDegreeById(1L)).thenReturn(academicDegree);
        when(entityManager.merge(any(Institute.class))).thenThrow(new PersistenceException("Database error"));

        // When & Then
        PersistenceException exception = assertThrows(PersistenceException.class,
                () -> instituteService.saveInstitute(validDto));
        assertEquals("Database error", exception.getMessage());
        verify(exceptionHandlingService, times(1)).handleException(any(PersistenceException.class));
    }

    @Test
    @DisplayName("Should update institute by id successfully")
    void testUpdateInstituteById() throws Exception {
        // Given
        when(academicDegreeService.getAcademicDegreeById(1L)).thenReturn(academicDegree);
        when(entityManager.merge(any(Institute.class))).thenReturn(institute);

        Institute existingInstitute = new Institute();
        existingInstitute.setInstituteId(1L);
        existingInstitute.setInstitutionName("Old University");
        existingInstitute.setCreatedDate(new Date());
        existingInstitute.setUpdatedDate(new Date());
        existingInstitute.setDegrees(new ArrayList<>());

        // When
        Institute result = instituteService.updateInstituteById(existingInstitute, validDto);

        // Then
        assertNotNull(result);
        assertEquals("Test University", result.getInstitutionName());
        verify(academicDegreeService, times(1)).getAcademicDegreeById(1L);
        verify(entityManager, times(1)).merge(any(Institute.class));
    }

    @Test
    @DisplayName("Should handle persistence exception when updating institute")
    void testUpdateInstituteByIdWithPersistenceException() throws Exception {
        // Given
        when(academicDegreeService.getAcademicDegreeById(1L)).thenReturn(academicDegree);
        when(entityManager.merge(any(Institute.class))).thenThrow(new PersistenceException("Database error"));

        Institute existingInstitute = new Institute();
        existingInstitute.setInstituteId(1L);
        existingInstitute.setInstitutionName("Old University");
        existingInstitute.setCreatedDate(new Date());
        existingInstitute.setUpdatedDate(new Date());
        existingInstitute.setDegrees(new ArrayList<>());

        // When & Then
        PersistenceException exception = assertThrows(PersistenceException.class,
                () -> instituteService.updateInstituteById(existingInstitute, validDto));
        assertEquals("Database error", exception.getMessage());
        verify(exceptionHandlingService, times(1)).handleException(any(PersistenceException.class));
    }

    @Test
    @DisplayName("Should get institute by id successfully")
    void testGetInstituteById() throws Exception {
        // Given
        List<Institute> institutes = Arrays.asList(institute);
        when(entityManager.createQuery(eq(Constant.GET_INSTITUTE_BY_ID), eq(Institute.class))).thenReturn(instituteTypedQuery);
        when(instituteTypedQuery.setParameter(eq("instituteId"), eq(1L))).thenReturn(instituteTypedQuery);
        when(instituteTypedQuery.getResultList()).thenReturn(institutes);

        // When
        Institute result = instituteService.getInstituteById(1L);

        // Then
        assertNotNull(result);
        assertEquals("Test University", result.getInstitutionName());
        verify(entityManager, times(1)).createQuery(eq(Constant.GET_INSTITUTE_BY_ID), eq(Institute.class));
        verify(instituteTypedQuery, times(1)).setParameter(eq("instituteId"), eq(1L));
        verify(instituteTypedQuery, times(1)).getResultList();
    }

    @Test
    @DisplayName("Should throw exception when institute not found by id")
    void testGetInstituteByIdNotFound() {
        // Given
        when(entityManager.createQuery(eq(Constant.GET_INSTITUTE_BY_ID), eq(Institute.class))).thenReturn(instituteTypedQuery);
        when(instituteTypedQuery.setParameter(eq("instituteId"), eq(99L))).thenReturn(instituteTypedQuery);
        when(instituteTypedQuery.getResultList()).thenReturn(Collections.emptyList());

        // When & Then
        IndexOutOfBoundsException exception = assertThrows(IndexOutOfBoundsException.class,
                () -> instituteService.getInstituteById(99L));
        assertEquals("Institute not found with given Id", exception.getMessage());
        verify(exceptionHandlingService, times(1)).handleException(any(IndexOutOfBoundsException.class));
    }

    @Test
    @DisplayName("Should filter institutes successfully")
    void testFilterInstitute() throws Exception {
        // Given
        List<Institute> institutes = Arrays.asList(institute);
        when(entityManager.createQuery(anyString(), eq(Institute.class))).thenReturn(instituteTypedQuery);
        when(instituteTypedQuery.setParameter(anyString(), any())).thenReturn(instituteTypedQuery);
        when(instituteTypedQuery.getResultList()).thenReturn(institutes);

        // When
        List<Institute> result = instituteService.filterInstitute(1L, "Test University");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test University", result.get(0).getInstitutionName());
        verify(entityManager, times(1)).createQuery(anyString(), eq(Institute.class));
        verify(instituteTypedQuery, times(2)).setParameter(anyString(), any());
        verify(instituteTypedQuery, times(1)).getResultList();
    }

    @Test
    @DisplayName("Should filter institutes with only id parameter")
    void testFilterInstituteWithIdOnly() throws Exception {
        // Given
        List<Institute> institutes = Arrays.asList(institute);
        when(entityManager.createQuery(anyString(), eq(Institute.class))).thenReturn(instituteTypedQuery);
        when(instituteTypedQuery.setParameter(anyString(), any())).thenReturn(instituteTypedQuery);
        when(instituteTypedQuery.getResultList()).thenReturn(institutes);

        // When
        List<Institute> result = instituteService.filterInstitute(1L, null);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(entityManager, times(1)).createQuery(anyString(), eq(Institute.class));
        verify(instituteTypedQuery, times(1)).setParameter(anyString(), any());
        verify(instituteTypedQuery, times(1)).getResultList();
    }

    @Test
    @DisplayName("Should filter institutes with only name parameter")
    void testFilterInstituteWithNameOnly() throws Exception {
        // Given
        List<Institute> institutes = Arrays.asList(institute);
        when(entityManager.createQuery(anyString(), eq(Institute.class))).thenReturn(instituteTypedQuery);
        when(instituteTypedQuery.setParameter(anyString(), any())).thenReturn(instituteTypedQuery);
        when(instituteTypedQuery.getResultList()).thenReturn(institutes);

        // When
        List<Institute> result = instituteService.filterInstitute(null, "Test University");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(entityManager, times(1)).createQuery(anyString(), eq(Institute.class));
        verify(instituteTypedQuery, times(1)).setParameter(anyString(), any());
        verify(instituteTypedQuery, times(1)).getResultList();
    }

    @Test
    @DisplayName("Should handle persistence exception when filtering institutes")
    void testFilterInstituteWithPersistenceException() {
        // Given
        when(entityManager.createQuery(anyString(), eq(Institute.class))).thenThrow(new PersistenceException("Database error"));

        // When & Then
        PersistenceException exception = assertThrows(PersistenceException.class,
                () -> instituteService.filterInstitute(1L, "Test University"));
        assertEquals("Database error", exception.getMessage());
        verify(exceptionHandlingService, times(1)).handleException(any(PersistenceException.class));
    }

    @Test
    @DisplayName("Should remove institute by id successfully")
    void testRemoveInstituteById() throws Exception {
        // Given
        Institute instituteToRemove = new Institute();
        instituteToRemove.setInstituteId(1L);
        instituteToRemove.setInstitutionName("Test University");
        instituteToRemove.setArchived('N');

        when(entityManager.merge(any(Institute.class))).thenReturn(instituteToRemove);

        // When
        Institute result = instituteService.removeInstituteById(instituteToRemove);

        // Then
        assertNotNull(result);
        assertEquals('Y', result.getArchived());
        verify(entityManager, times(1)).merge(any(Institute.class));
    }

    @Test
    @DisplayName("Should handle exception when removing institute")
    void testRemoveInstituteByIdWithException() {
        // Given
        Institute instituteToRemove = new Institute();
        instituteToRemove.setInstituteId(1L);
        instituteToRemove.setInstitutionName("Test University");
        instituteToRemove.setArchived('N');

        when(entityManager.merge(any(Institute.class))).thenThrow(new RuntimeException("Error removing institute"));

        // When & Then
        Exception exception = assertThrows(Exception.class,
                () -> instituteService.removeInstituteById(instituteToRemove));
        assertEquals("java.lang.RuntimeException: Error removing institute", exception.getMessage());
        verify(exceptionHandlingService, times(1)).handleException(any(Exception.class));
    }
}