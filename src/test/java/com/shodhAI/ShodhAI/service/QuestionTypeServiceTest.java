package com.shodhAI.ShodhAI.service;

import com.shodhAI.ShodhAI.Component.Constant;
import com.shodhAI.ShodhAI.Entity.QuestionType;
import com.shodhAI.ShodhAI.Service.QuestionTypeService;
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
public class QuestionTypeServiceTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private ExceptionHandlingService exceptionHandlingService;

    @Mock
    private TypedQuery<QuestionType> questionTypeTypedQuery;

    @InjectMocks
    private QuestionTypeService questionTypeService;

    private QuestionType validQuestionType;
    private List<QuestionType> questionTypeList;

    @BeforeEach
    void setUp() {
        // Setup valid question type
        validQuestionType = new QuestionType();
        validQuestionType.setQuestionTypeId(1L);
        validQuestionType.setQuestionType("Article");
        validQuestionType.setCreatedDate(new Date());
        validQuestionType.setArchived('N');

        // Setup question type list
        QuestionType questionType2 = new QuestionType();
        questionType2.setQuestionTypeId(2L);
        questionType2.setQuestionType("Video");
        questionType2.setCreatedDate(new Date());
        questionType2.setArchived('N');

        questionTypeList = Arrays.asList(validQuestionType, questionType2);
    }

    @Test
    @DisplayName("Should get all question types successfully")
    void testGetAllQuestionType() throws Exception {
        // Given
        when(entityManager.createQuery(eq(Constant.GET_ALL_QUESTION_TYPE), eq(QuestionType.class)))
                .thenReturn(questionTypeTypedQuery);
        when(questionTypeTypedQuery.getResultList()).thenReturn(questionTypeList);

        // When
        List<QuestionType> result = questionTypeService.getAllQuestionTypes();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Article", result.get(0).getQuestionType());
        assertEquals("Video", result.get(1).getQuestionType());

        verify(entityManager).createQuery(eq(Constant.GET_ALL_QUESTION_TYPE), eq(QuestionType.class));
        verify(questionTypeTypedQuery).getResultList();
    }

    @Test
    @DisplayName("Should handle exception when getting all question types fails")
    void testGetAllQuestionTypeException() {
        // Given
        when(entityManager.createQuery(eq(Constant.GET_ALL_QUESTION_TYPE), eq(QuestionType.class)))
                .thenThrow(new RuntimeException("Database error"));

        // When & Then
        Exception exception = assertThrows(Exception.class, () -> questionTypeService.getAllQuestionTypes());
        assertEquals("java.lang.RuntimeException: Database error", exception.getMessage());

        verify(exceptionHandlingService).handleException(any(RuntimeException.class));
    }

    @Test
    @DisplayName("Should get question type by ID successfully")
    void testGetQuestionTypeById() throws Exception {
        // Given
        when(entityManager.createQuery(eq(Constant.GET_QUESTION_TYPE_BY_ID), eq(QuestionType.class)))
                .thenReturn(questionTypeTypedQuery);
        when(questionTypeTypedQuery.setParameter(eq("questionTypeId"), eq(1L))).thenReturn(questionTypeTypedQuery);
        when(questionTypeTypedQuery.getResultList()).thenReturn(Collections.singletonList(validQuestionType));

        // When
        QuestionType result = questionTypeService.getQuestionTypeById(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getQuestionTypeId());
        assertEquals("Article", result.getQuestionType());

        verify(entityManager).createQuery(eq(Constant.GET_QUESTION_TYPE_BY_ID), eq(QuestionType.class));
        verify(questionTypeTypedQuery).setParameter(eq("questionTypeId"), eq(1L));
        verify(questionTypeTypedQuery).getResultList();
    }

    @Test
    @DisplayName("Should throw exception when question type not found by ID")
    void testGetQuestionTypeByIdNotFound() {
        // Given
        when(entityManager.createQuery(eq(Constant.GET_QUESTION_TYPE_BY_ID), eq(QuestionType.class)))
                .thenReturn(questionTypeTypedQuery);
        when(questionTypeTypedQuery.setParameter(eq("questionTypeId"), eq(99L))).thenReturn(questionTypeTypedQuery);
        when(questionTypeTypedQuery.getResultList()).thenReturn(Collections.emptyList());

        // When & Then
        IndexOutOfBoundsException exception = assertThrows(IndexOutOfBoundsException.class,
                () -> questionTypeService.getQuestionTypeById(99L));

        verify(exceptionHandlingService).handleException(any(IndexOutOfBoundsException.class));
    }

    @Test
    @DisplayName("Should add new question type successfully")
    void testAddQuestionType() throws Exception {
        // Given
        QuestionType newQuestionType = new QuestionType();
        newQuestionType.setQuestionType("Podcast");

        when(entityManager.createQuery(eq(Constant.GET_ALL_QUESTION_TYPE), eq(QuestionType.class)))
                .thenReturn(questionTypeTypedQuery);
        when(questionTypeTypedQuery.getResultList()).thenReturn(questionTypeList);
        doNothing().when(entityManager).persist(any(QuestionType.class));

        // When
        QuestionType result = questionTypeService.addQuestionType(newQuestionType);

        // Then
        assertNotNull(result);
        assertEquals("Podcast", result.getQuestionType());
        assertNotNull(result.getCreatedDate());

        verify(entityManager).createQuery(eq(Constant.GET_ALL_QUESTION_TYPE), eq(QuestionType.class));
        verify(questionTypeTypedQuery).getResultList();
        verify(entityManager).persist(any(QuestionType.class));
    }

    @Test
    @DisplayName("Should throw exception when adding question type with empty name")
    void testAddQuestionTypeWithEmptyName() {
        // Given
        QuestionType newQuestionType = new QuestionType();
        newQuestionType.setQuestionType("");

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> questionTypeService.addQuestionType(newQuestionType));
        assertEquals("Question type name cannot be null or empty", exception.getMessage());

        verify(exceptionHandlingService).handleException(any(IllegalArgumentException.class));
    }

    @Test
    @DisplayName("Should throw exception when adding duplicate question type")
    void testAddDuplicateQuestionType() {
        // Given
        QuestionType newQuestionType = new QuestionType();
        newQuestionType.setQuestionType("Article");

        when(entityManager.createQuery(eq(Constant.GET_ALL_QUESTION_TYPE), eq(QuestionType.class)))
                .thenReturn(questionTypeTypedQuery);
        when(questionTypeTypedQuery.getResultList()).thenReturn(questionTypeList);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> questionTypeService.addQuestionType(newQuestionType));
        assertEquals("Question type already exists with name Article", exception.getMessage());

        verify(exceptionHandlingService).handleException(any(IllegalArgumentException.class));
    }

    @Test
    @DisplayName("Should update question type successfully")
    void testUpdateQuestionType() throws Exception {
        // Given
        QuestionType updatedQuestionType = new QuestionType();
        updatedQuestionType.setQuestionType("Updated Article");

        when(entityManager.find(eq(QuestionType.class), eq(1L))).thenReturn(validQuestionType);
        when(entityManager.createQuery(eq(Constant.GET_ALL_QUESTION_TYPE), eq(QuestionType.class)))
                .thenReturn(questionTypeTypedQuery);
        when(questionTypeTypedQuery.getResultList()).thenReturn(questionTypeList);
        when(entityManager.merge(any(QuestionType.class))).thenReturn(validQuestionType);

        // When
        QuestionType result = questionTypeService.updateQuestionType(1L, updatedQuestionType);

        // Then
        assertNotNull(result);
        assertEquals("Updated Article", result.getQuestionType());
        assertNotNull(result.getUpdatedDate());

        verify(entityManager).find(eq(QuestionType.class), eq(1L));
        verify(entityManager).createQuery(eq(Constant.GET_ALL_QUESTION_TYPE), eq(QuestionType.class));
        verify(questionTypeTypedQuery).getResultList();
        verify(entityManager).merge(any(QuestionType.class));
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent question type")
    void testUpdateNonExistentQuestionType() {
        // Given
        QuestionType updatedQuestionType = new QuestionType();
        updatedQuestionType.setQuestionType("Updated Article");

        when(entityManager.find(eq(QuestionType.class), eq(99L))).thenReturn(null);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> questionTypeService.updateQuestionType(99L, updatedQuestionType));
        assertEquals("Question type with id 99 not found", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when updating question type with empty name")
    void testUpdateQuestionTypeWithEmptyName() {
        // Given
        QuestionType updatedQuestionType = new QuestionType();
        updatedQuestionType.setQuestionType("");

        when(entityManager.find(eq(QuestionType.class), eq(1L))).thenReturn(validQuestionType);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> questionTypeService.updateQuestionType(1L, updatedQuestionType));
        assertEquals("Question type name cannot be null or empty", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when updating question type with duplicate name")
    void testUpdateQuestionTypeWithDuplicateName() {
        // Given
        QuestionType updatedQuestionType = new QuestionType();
        updatedQuestionType.setQuestionType("Video");

        when(entityManager.find(eq(QuestionType.class), eq(1L))).thenReturn(validQuestionType);
        when(entityManager.createQuery(eq(Constant.GET_ALL_QUESTION_TYPE), eq(QuestionType.class)))
                .thenReturn(questionTypeTypedQuery);
        when(questionTypeTypedQuery.getResultList()).thenReturn(questionTypeList);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> questionTypeService.updateQuestionType(1L, updatedQuestionType));
        assertEquals("Question type already exists with name Video", exception.getMessage());
    }

    @Test
    @DisplayName("Should delete question type successfully")
    void testDeleteQuestionTypeById() throws Exception {
        // Given
        when(entityManager.find(eq(QuestionType.class), eq(1L))).thenReturn(validQuestionType);
        when(entityManager.merge(any(QuestionType.class))).thenReturn(validQuestionType);

        // When
        QuestionType result = questionTypeService.deleteQuestionTypeById(1L);

        // Then
        assertNotNull(result);
        assertEquals('Y', result.getArchived());

        verify(entityManager).find(eq(QuestionType.class), eq(1L));
        verify(entityManager).merge(any(QuestionType.class));
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent question type")
    void testDeleteNonExistentQuestionType() {
        // Given
        when(entityManager.find(eq(QuestionType.class), eq(99L))).thenReturn(null);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> questionTypeService.deleteQuestionTypeById(99L));
        assertEquals("Question type with id 99 not found", exception.getMessage());
    }

    @Test
    @DisplayName("Should filter question types successfully")
    void testQuestionTypeFilter() throws Exception {
        // Given
        String jpql = "SELECT f FROM QuestionType f WHERE f.archived = 'N' ORDER BY f.questionTypeId ASC";
        when(entityManager.createQuery(eq(jpql), eq(QuestionType.class))).thenReturn(questionTypeTypedQuery);
        when(questionTypeTypedQuery.getResultList()).thenReturn(questionTypeList);

        // When
        List<QuestionType> result = questionTypeService.questionTypeFilter();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Article", result.get(0).getQuestionType());
        assertEquals("Video", result.get(1).getQuestionType());

        verify(entityManager).createQuery(eq(jpql), eq(QuestionType.class));
        verify(questionTypeTypedQuery).getResultList();
    }

    @Test
    @DisplayName("Should handle persistence exception when filtering question types")
    void testQuestionTypeFilterPersistenceException() {
        // Given
        String jpql = "SELECT f FROM QuestionType f WHERE f.archived = 'N' ORDER BY f.questionTypeId ASC";
        when(entityManager.createQuery(eq(jpql), eq(QuestionType.class)))
                .thenThrow(new PersistenceException("Database error"));

        // When & Then
        PersistenceException exception = assertThrows(PersistenceException.class,
                () -> questionTypeService.questionTypeFilter());
        assertEquals("Database error", exception.getMessage());

        verify(exceptionHandlingService).handleException(any(PersistenceException.class));
    }
}
