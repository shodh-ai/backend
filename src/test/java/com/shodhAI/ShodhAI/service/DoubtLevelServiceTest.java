package com.shodhAI.ShodhAI.service;

import com.shodhAI.ShodhAI.Component.Constant;
import com.shodhAI.ShodhAI.Entity.DoubtLevel;
import com.shodhAI.ShodhAI.Service.DoubtService;
import com.shodhAI.ShodhAI.Service.ExceptionHandlingService;
import jakarta.persistence.EntityManager;
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
public class DoubtLevelServiceTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private ExceptionHandlingService exceptionHandlingService;

    @Mock
    private TypedQuery<DoubtLevel> doubtLevelTypedQuery;

    @InjectMocks
    private DoubtService doubtLevelService;

    private DoubtLevel validDoubtLevel;
    private List<DoubtLevel> doubtLevelList;

    @BeforeEach
    void setUp() {
        // Setup valid doubt level
        validDoubtLevel = new DoubtLevel();
        validDoubtLevel.setDoubtLevelId(1L);
        validDoubtLevel.setDoubtLevel("Article");
        validDoubtLevel.setCreatedDate(new Date());
        validDoubtLevel.setArchived('N');

        // Setup doubt level list
        DoubtLevel doubtLevel2 = new DoubtLevel();
        doubtLevel2.setDoubtLevelId(2L);
        doubtLevel2.setDoubtLevel("Video");
        doubtLevel2.setCreatedDate(new Date());
        doubtLevel2.setArchived('N');

        doubtLevelList = Arrays.asList(validDoubtLevel, doubtLevel2);
    }

    @Test
    @DisplayName("Should get all doubt levels successfully")
    void testGetAllDoubtLevel() throws Exception {
        // Given
        when(entityManager.createQuery(eq(Constant.GET_ALL_DOUBT_LEVEL), eq(DoubtLevel.class)))
                .thenReturn(doubtLevelTypedQuery);
        when(doubtLevelTypedQuery.getResultList()).thenReturn(doubtLevelList);

        // When
        List<DoubtLevel> result = doubtLevelService.getAllDoubtLevels();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Article", result.get(0).getDoubtLevel());
        assertEquals("Video", result.get(1).getDoubtLevel());

        verify(entityManager).createQuery(eq(Constant.GET_ALL_DOUBT_LEVEL), eq(DoubtLevel.class));
        verify(doubtLevelTypedQuery).getResultList();
    }

    @Test
    @DisplayName("Should handle exception when getting all doubt levels fails")
    void testGetAllDoubtLevelException() {
        // Given
        when(entityManager.createQuery(eq(Constant.GET_ALL_DOUBT_LEVEL), eq(DoubtLevel.class)))
                .thenThrow(new RuntimeException("Database error"));

        // When & Then
        Exception exception = assertThrows(Exception.class, () -> doubtLevelService.getAllDoubtLevels());
        assertEquals("java.lang.RuntimeException: Database error", exception.getMessage());

        verify(exceptionHandlingService).handleException(any(RuntimeException.class));
    }

    @Test
    @DisplayName("Should get doubt level by ID successfully")
    void testGetDoubtLevelById() throws Exception {
        // Given
        when(entityManager.createQuery(eq(Constant.GET_DOUBT_LEVEL_BY_ID), eq(DoubtLevel.class)))
                .thenReturn(doubtLevelTypedQuery);
        when(doubtLevelTypedQuery.setParameter(eq("doubtLevelId"), eq(1L))).thenReturn(doubtLevelTypedQuery);
        when(doubtLevelTypedQuery.getResultList()).thenReturn(Collections.singletonList(validDoubtLevel));

        // When
        DoubtLevel result = doubtLevelService.getDoubtLevelById(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getDoubtLevelId());
        assertEquals("Article", result.getDoubtLevel());

        verify(entityManager).createQuery(eq(Constant.GET_DOUBT_LEVEL_BY_ID), eq(DoubtLevel.class));
        verify(doubtLevelTypedQuery).setParameter(eq("doubtLevelId"), eq(1L));
        verify(doubtLevelTypedQuery).getResultList();
    }

    @Test
    @DisplayName("Should throw exception when doubt level not found by ID")
    void testGetDoubtLevelByIdNotFound() {
        // Given
        when(entityManager.createQuery(eq(Constant.GET_DOUBT_LEVEL_BY_ID), eq(DoubtLevel.class)))
                .thenReturn(doubtLevelTypedQuery);
        when(doubtLevelTypedQuery.setParameter(eq("doubtLevelId"), eq(99L))).thenReturn(doubtLevelTypedQuery);
        when(doubtLevelTypedQuery.getResultList()).thenReturn(Collections.emptyList());

        // When & Then
        IndexOutOfBoundsException exception = assertThrows(IndexOutOfBoundsException.class,
                () -> doubtLevelService.getDoubtLevelById(99L));

        verify(exceptionHandlingService).handleException(any(IndexOutOfBoundsException.class));
    }

    @Test
    @DisplayName("Should delete doubt level successfully")
    void testDeleteDoubtLevelById() throws Exception {
        // Given
        when(entityManager.find(eq(DoubtLevel.class), eq(1L))).thenReturn(validDoubtLevel);
        when(entityManager.merge(any(DoubtLevel.class))).thenReturn(validDoubtLevel);

        // When
        DoubtLevel result = doubtLevelService.deleteDoubtLevelById(1L);

        // Then
        assertNotNull(result);
        assertEquals('Y', result.getArchived());

        verify(entityManager).find(eq(DoubtLevel.class), eq(1L));
        verify(entityManager).merge(any(DoubtLevel.class));
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent doubt level")
    void testDeleteNonExistentDoubtLevel() {
        // Given
        when(entityManager.find(eq(DoubtLevel.class), eq(99L))).thenReturn(null);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> doubtLevelService.deleteDoubtLevelById(99L));
        assertEquals("Doubt level with id 99 not found", exception.getMessage());
    }
}
