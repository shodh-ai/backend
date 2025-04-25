package com.shodhAI.ShodhAI.service;

import com.shodhAI.ShodhAI.Component.Constant;
import com.shodhAI.ShodhAI.Entity.PriorityLevel;
import com.shodhAI.ShodhAI.Service.PriorityLevelService;
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
public class PriorityLevelServiceTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private ExceptionHandlingService exceptionHandlingService;

    @Mock
    private TypedQuery<PriorityLevel> priorityLevelTypedQuery;

    @InjectMocks
    private PriorityLevelService priorityLevelService;

    private PriorityLevel validPriorityLevel;
    private List<PriorityLevel> priorityLevelList;

    @BeforeEach
    void setUp() {
        // Setup valid priority level
        validPriorityLevel = new PriorityLevel();
        validPriorityLevel.setPriorityLevelId(1L);
        validPriorityLevel.setPriorityLevel("Article");
        validPriorityLevel.setCreatedDate(new Date());
        validPriorityLevel.setArchived('N');

        // Setup priority level list
        PriorityLevel priorityLevel2 = new PriorityLevel();
        priorityLevel2.setPriorityLevelId(2L);
        priorityLevel2.setPriorityLevel("Video");
        priorityLevel2.setCreatedDate(new Date());
        priorityLevel2.setArchived('N');

        priorityLevelList = Arrays.asList(validPriorityLevel, priorityLevel2);
    }

    @Test
    @DisplayName("Should get all priority levels successfully")
    void testGetAllPriorityLevel() throws Exception {
        // Given
        when(entityManager.createQuery(eq(Constant.GET_ALL_PRIORITY_LEVEL), eq(PriorityLevel.class)))
                .thenReturn(priorityLevelTypedQuery);
        when(priorityLevelTypedQuery.getResultList()).thenReturn(priorityLevelList);

        // When
        List<PriorityLevel> result = priorityLevelService.getAllPriorityLevels();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Article", result.get(0).getPriorityLevel());
        assertEquals("Video", result.get(1).getPriorityLevel());

        verify(entityManager).createQuery(eq(Constant.GET_ALL_PRIORITY_LEVEL), eq(PriorityLevel.class));
        verify(priorityLevelTypedQuery).getResultList();
    }

    @Test
    @DisplayName("Should handle exception when getting all priority levels fails")
    void testGetAllPriorityLevelException() {
        // Given
        when(entityManager.createQuery(eq(Constant.GET_ALL_PRIORITY_LEVEL), eq(PriorityLevel.class)))
                .thenThrow(new RuntimeException("Database error"));

        // When & Then
        Exception exception = assertThrows(Exception.class, () -> priorityLevelService.getAllPriorityLevels());
        assertEquals("java.lang.RuntimeException: Database error", exception.getMessage());

        verify(exceptionHandlingService).handleException(any(RuntimeException.class));
    }

    @Test
    @DisplayName("Should get priority level by ID successfully")
    void testGetPriorityLevelById() throws Exception {
        // Given
        when(entityManager.createQuery(eq(Constant.GET_PRIORITY_LEVEL_BY_ID), eq(PriorityLevel.class)))
                .thenReturn(priorityLevelTypedQuery);
        when(priorityLevelTypedQuery.setParameter(eq("priorityLevelId"), eq(1L))).thenReturn(priorityLevelTypedQuery);
        when(priorityLevelTypedQuery.getResultList()).thenReturn(Collections.singletonList(validPriorityLevel));

        // When
        PriorityLevel result = priorityLevelService.getPriorityLevelById(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getPriorityLevelId());
        assertEquals("Article", result.getPriorityLevel());

        verify(entityManager).createQuery(eq(Constant.GET_PRIORITY_LEVEL_BY_ID), eq(PriorityLevel.class));
        verify(priorityLevelTypedQuery).setParameter(eq("priorityLevelId"), eq(1L));
        verify(priorityLevelTypedQuery).getResultList();
    }

    @Test
    @DisplayName("Should throw exception when priority level not found by ID")
    void testGetPriorityLevelByIdNotFound() {
        // Given
        when(entityManager.createQuery(eq(Constant.GET_PRIORITY_LEVEL_BY_ID), eq(PriorityLevel.class)))
                .thenReturn(priorityLevelTypedQuery);
        when(priorityLevelTypedQuery.setParameter(eq("priorityLevelId"), eq(99L))).thenReturn(priorityLevelTypedQuery);
        when(priorityLevelTypedQuery.getResultList()).thenReturn(Collections.emptyList());

        // When & Then
        IndexOutOfBoundsException exception = assertThrows(IndexOutOfBoundsException.class,
                () -> priorityLevelService.getPriorityLevelById(99L));

        verify(exceptionHandlingService).handleException(any(IndexOutOfBoundsException.class));
    }

    @Test
    @DisplayName("Should delete priority level successfully")
    void testDeletePriorityLevelById() throws Exception {
        // Given
        when(entityManager.find(eq(PriorityLevel.class), eq(1L))).thenReturn(validPriorityLevel);
        when(entityManager.merge(any(PriorityLevel.class))).thenReturn(validPriorityLevel);

        // When
        PriorityLevel result = priorityLevelService.deletePriorityLevelById(1L);

        // Then
        assertNotNull(result);
        assertEquals('Y', result.getArchived());

        verify(entityManager).find(eq(PriorityLevel.class), eq(1L));
        verify(entityManager).merge(any(PriorityLevel.class));
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent priority level")
    void testDeleteNonExistentPriorityLevel() {
        // Given
        when(entityManager.find(eq(PriorityLevel.class), eq(99L))).thenReturn(null);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> priorityLevelService.deletePriorityLevelById(99L));
        assertEquals("Priority Level with id 99 not found", exception.getMessage());
    }
}
