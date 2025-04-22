package com.shodhAI.ShodhAI.service;

import com.shodhAI.ShodhAI.Component.Constant;
import com.shodhAI.ShodhAI.Entity.Gender;
import com.shodhAI.ShodhAI.Service.GenderService;
import com.shodhAI.ShodhAI.Service.ExceptionHandlingService;
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

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GenderServiceTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private ExceptionHandlingService exceptionHandlingService;

    @Mock
    private TypedQuery<Gender> genderTypedQuery;

    @InjectMocks
    private GenderService genderService;

    private Gender validGender;
    private List<Gender> genderList;

    @BeforeEach
    void setUp() {
        // Setup valid gender
        validGender = new Gender();
        validGender.setGenderId(1L);
        validGender.setGenderName("Female");
        validGender.setGenderSymbol('F');

        // Setup gender list
        Gender gender2 = new Gender();
        gender2.setGenderId(2L);
        gender2.setGenderName("Male");
        gender2.setGenderSymbol('M');

        genderList = Arrays.asList(validGender, gender2);
    }

    @Test
    @DisplayName("Should get all genders successfully")
    void testGetAllGender() throws Exception {
        // Given
        when(entityManager.createQuery(eq(Constant.GET_ALL_GENDERS), eq(Gender.class)))
                .thenReturn(genderTypedQuery);
        when(genderTypedQuery.getResultList()).thenReturn(genderList);

        // When
        List<Gender> result = genderService.getAllGender();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Female", result.get(0).getGenderName());
        assertEquals("Male", result.get(1).getGenderName());

        verify(entityManager).createQuery(eq(Constant.GET_ALL_GENDERS), eq(Gender.class));
        verify(genderTypedQuery).getResultList();
    }

    @Test
    @DisplayName("Should handle exception when getting all genders fails")
    void testGetAllGenderException() {
        // Given
        when(entityManager.createQuery(eq(Constant.GET_ALL_GENDERS), eq(Gender.class)))
                .thenThrow(new RuntimeException("Database error"));

        // When & Then
        Exception exception = assertThrows(Exception.class, () -> genderService.getAllGender());
        assertEquals("Database error", exception.getMessage());

        verify(exceptionHandlingService).handleException(any(RuntimeException.class));
    }

    @Test
    @DisplayName("Should get gender by ID successfully")
    void testGetGenderById() throws Exception {
        // Given
        when(entityManager.createQuery(anyString(), eq(Gender.class)))
                .thenReturn(genderTypedQuery);
        when(genderTypedQuery.setParameter(eq("genderId"), eq(1L)))
                .thenReturn(genderTypedQuery);
        when(genderTypedQuery.getResultList())
                .thenReturn(Collections.singletonList(validGender));

        // When
        Gender result = genderService.getGenderById(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getGenderId());
        assertEquals("Female", result.getGenderName());

        verify(entityManager).createQuery(anyString(), eq(Gender.class));
        verify(genderTypedQuery).setParameter("genderId", 1L);
    }


    @Test
    @DisplayName("Should return null when gender not found by ID")
    void testGetGenderByIdNotFound() throws Exception {
        // Given
        when(entityManager.createQuery(eq(Constant.GET_GENDER_BY_ID), eq(Gender.class)))
                .thenReturn(genderTypedQuery);
        when(genderTypedQuery.setParameter(eq("genderId"), eq(99L))).thenReturn(genderTypedQuery);
        when(genderTypedQuery.getResultList()).thenReturn(Collections.emptyList());

        // When
        Gender result = genderService.getGenderById(99L);

        // Then
        assertNull(result);
    }
}
