package com.shodhAI.ShodhAI.service;

import com.shodhAI.ShodhAI.Component.Constant;
import com.shodhAI.ShodhAI.Entity.TopicType;
import com.shodhAI.ShodhAI.Service.TopicTypeService;
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
public class TopicTypeServiceTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private ExceptionHandlingService exceptionHandlingService;

    @Mock
    private TypedQuery<TopicType> topicTypeTypedQuery;

    @InjectMocks
    private TopicTypeService topicTypeService;

    private TopicType validTopicType;
    private List<TopicType> topicTypeList;

    @BeforeEach
    void setUp() {
        // Setup valid topic type
        validTopicType = new TopicType();
        validTopicType.setTopicTypeId(1L);
        validTopicType.setTopicTypeName("Article");
        validTopicType.setCreatedDate(new Date());
        validTopicType.setArchived('N');

        // Setup topic type list
        TopicType topicType2 = new TopicType();
        topicType2.setTopicTypeId(2L);
        topicType2.setTopicTypeName("Video");
        topicType2.setCreatedDate(new Date());
        topicType2.setArchived('N');

        topicTypeList = Arrays.asList(validTopicType, topicType2);
    }

    @Test
    @DisplayName("Should get all topic types successfully")
    void testGetAllTopicType() throws Exception {
        // Given
        when(entityManager.createQuery(eq(Constant.GET_ALL_TOPIC_TYPE), eq(TopicType.class)))
                .thenReturn(topicTypeTypedQuery);
        when(topicTypeTypedQuery.getResultList()).thenReturn(topicTypeList);

        // When
        List<TopicType> result = topicTypeService.getAllTopicType();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Article", result.get(0).getTopicTypeName());
        assertEquals("Video", result.get(1).getTopicTypeName());

        verify(entityManager).createQuery(eq(Constant.GET_ALL_TOPIC_TYPE), eq(TopicType.class));
        verify(topicTypeTypedQuery).getResultList();
    }

    @Test
    @DisplayName("Should handle exception when getting all topic types fails")
    void testGetAllTopicTypeException() {
        // Given
        when(entityManager.createQuery(eq(Constant.GET_ALL_TOPIC_TYPE), eq(TopicType.class)))
                .thenThrow(new RuntimeException("Database error"));

        // When & Then
        Exception exception = assertThrows(Exception.class, () -> topicTypeService.getAllTopicType());
        assertEquals("Database error", exception.getMessage());

        verify(exceptionHandlingService).handleException(any(RuntimeException.class));
    }

    @Test
    @DisplayName("Should get topic type by ID successfully")
    void testGetTopicTypeById() throws Exception {
        // Given
        when(entityManager.createQuery(eq(Constant.GET_TOPIC_TYPE_BY_ID), eq(TopicType.class)))
                .thenReturn(topicTypeTypedQuery);
        when(topicTypeTypedQuery.setParameter(eq("topicTypeId"), eq(1L))).thenReturn(topicTypeTypedQuery);
        when(topicTypeTypedQuery.getResultList()).thenReturn(Collections.singletonList(validTopicType));

        // When
        TopicType result = topicTypeService.getTopicTypeById(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getTopicTypeId());
        assertEquals("Article", result.getTopicTypeName());

        verify(entityManager).createQuery(eq(Constant.GET_TOPIC_TYPE_BY_ID), eq(TopicType.class));
        verify(topicTypeTypedQuery).setParameter(eq("topicTypeId"), eq(1L));
        verify(topicTypeTypedQuery).getResultList();
    }

    @Test
    @DisplayName("Should throw exception when topic type not found by ID")
    void testGetTopicTypeByIdNotFound() {
        // Given
        when(entityManager.createQuery(eq(Constant.GET_TOPIC_TYPE_BY_ID), eq(TopicType.class)))
                .thenReturn(topicTypeTypedQuery);
        when(topicTypeTypedQuery.setParameter(eq("topicTypeId"), eq(99L))).thenReturn(topicTypeTypedQuery);
        when(topicTypeTypedQuery.getResultList()).thenReturn(Collections.emptyList());

        // When & Then
        IndexOutOfBoundsException exception = assertThrows(IndexOutOfBoundsException.class,
                () -> topicTypeService.getTopicTypeById(99L));

        verify(exceptionHandlingService).handleException(any(IndexOutOfBoundsException.class));
    }

    @Test
    @DisplayName("Should add new topic type successfully")
    void testAddTopicType() throws Exception {
        // Given
        TopicType newTopicType = new TopicType();
        newTopicType.setTopicTypeName("Podcast");

        when(entityManager.createQuery(eq(Constant.GET_ALL_TOPIC_TYPE), eq(TopicType.class)))
                .thenReturn(topicTypeTypedQuery);
        when(topicTypeTypedQuery.getResultList()).thenReturn(topicTypeList);
        doNothing().when(entityManager).persist(any(TopicType.class));

        // When
        TopicType result = topicTypeService.addTopicType(newTopicType);

        // Then
        assertNotNull(result);
        assertEquals("Podcast", result.getTopicTypeName());
        assertNotNull(result.getCreatedDate());

        verify(entityManager).createQuery(eq(Constant.GET_ALL_TOPIC_TYPE), eq(TopicType.class));
        verify(topicTypeTypedQuery).getResultList();
        verify(entityManager).persist(any(TopicType.class));
    }

    @Test
    @DisplayName("Should throw exception when adding topic type with empty name")
    void testAddTopicTypeWithEmptyName() {
        // Given
        TopicType newTopicType = new TopicType();
        newTopicType.setTopicTypeName("");

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> topicTypeService.addTopicType(newTopicType));
        assertEquals("Topic type name cannot be null or empty", exception.getMessage());

        verify(exceptionHandlingService).handleException(any(IllegalArgumentException.class));
    }

    @Test
    @DisplayName("Should throw exception when adding duplicate topic type")
    void testAddDuplicateTopicType() {
        // Given
        TopicType newTopicType = new TopicType();
        newTopicType.setTopicTypeName("Article");

        when(entityManager.createQuery(eq(Constant.GET_ALL_TOPIC_TYPE), eq(TopicType.class)))
                .thenReturn(topicTypeTypedQuery);
        when(topicTypeTypedQuery.getResultList()).thenReturn(topicTypeList);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> topicTypeService.addTopicType(newTopicType));
        assertEquals("Topic type already exists with name Article", exception.getMessage());

        verify(exceptionHandlingService).handleException(any(IllegalArgumentException.class));
    }

    @Test
    @DisplayName("Should update topic type successfully")
    void testUpdateTopicType() throws Exception {
        // Given
        TopicType updatedTopicType = new TopicType();
        updatedTopicType.setTopicTypeName("Updated Article");

        when(entityManager.find(eq(TopicType.class), eq(1L))).thenReturn(validTopicType);
        when(entityManager.createQuery(eq(Constant.GET_ALL_TOPIC_TYPE), eq(TopicType.class)))
                .thenReturn(topicTypeTypedQuery);
        when(topicTypeTypedQuery.getResultList()).thenReturn(topicTypeList);
        when(entityManager.merge(any(TopicType.class))).thenReturn(validTopicType);

        // When
        TopicType result = topicTypeService.updateTopicType(1L, updatedTopicType);

        // Then
        assertNotNull(result);
        assertEquals("Updated Article", result.getTopicTypeName());
        assertNotNull(result.getUpdatedDate());

        verify(entityManager).find(eq(TopicType.class), eq(1L));
        verify(entityManager).createQuery(eq(Constant.GET_ALL_TOPIC_TYPE), eq(TopicType.class));
        verify(topicTypeTypedQuery).getResultList();
        verify(entityManager).merge(any(TopicType.class));
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent topic type")
    void testUpdateNonExistentTopicType() {
        // Given
        TopicType updatedTopicType = new TopicType();
        updatedTopicType.setTopicTypeName("Updated Article");

        when(entityManager.find(eq(TopicType.class), eq(99L))).thenReturn(null);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> topicTypeService.updateTopicType(99L, updatedTopicType));
        assertEquals("Topic type with id 99 not found", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when updating topic type with empty name")
    void testUpdateTopicTypeWithEmptyName() {
        // Given
        TopicType updatedTopicType = new TopicType();
        updatedTopicType.setTopicTypeName("");

        when(entityManager.find(eq(TopicType.class), eq(1L))).thenReturn(validTopicType);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> topicTypeService.updateTopicType(1L, updatedTopicType));
        assertEquals("Topic type name cannot be null or empty", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when updating topic type with duplicate name")
    void testUpdateTopicTypeWithDuplicateName() {
        // Given
        TopicType updatedTopicType = new TopicType();
        updatedTopicType.setTopicTypeName("Video");

        when(entityManager.find(eq(TopicType.class), eq(1L))).thenReturn(validTopicType);
        when(entityManager.createQuery(eq(Constant.GET_ALL_TOPIC_TYPE), eq(TopicType.class)))
                .thenReturn(topicTypeTypedQuery);
        when(topicTypeTypedQuery.getResultList()).thenReturn(topicTypeList);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> topicTypeService.updateTopicType(1L, updatedTopicType));
        assertEquals("Topic type already exists with name Video", exception.getMessage());
    }

    @Test
    @DisplayName("Should delete topic type successfully")
    void testDeleteTopicTypeById() throws Exception {
        // Given
        when(entityManager.find(eq(TopicType.class), eq(1L))).thenReturn(validTopicType);
        when(entityManager.merge(any(TopicType.class))).thenReturn(validTopicType);

        // When
        TopicType result = topicTypeService.deleteTopicTypeById(1L);

        // Then
        assertNotNull(result);
        assertEquals('Y', result.getArchived());

        verify(entityManager).find(eq(TopicType.class), eq(1L));
        verify(entityManager).merge(any(TopicType.class));
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent topic type")
    void testDeleteNonExistentTopicType() {
        // Given
        when(entityManager.find(eq(TopicType.class), eq(99L))).thenReturn(null);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> topicTypeService.deleteTopicTypeById(99L));
        assertEquals("Topic type with id 99 not found", exception.getMessage());
    }

    @Test
    @DisplayName("Should filter topic types successfully")
    void testTopicTypeFilter() throws Exception {
        // Given
        String jpql = "SELECT f FROM TopicType f WHERE f.archived = 'N' ORDER BY f.topicTypeId ASC";
        when(entityManager.createQuery(eq(jpql), eq(TopicType.class))).thenReturn(topicTypeTypedQuery);
        when(topicTypeTypedQuery.getResultList()).thenReturn(topicTypeList);

        // When
        List<TopicType> result = topicTypeService.topicTypeFilter();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Article", result.get(0).getTopicTypeName());
        assertEquals("Video", result.get(1).getTopicTypeName());

        verify(entityManager).createQuery(eq(jpql), eq(TopicType.class));
        verify(topicTypeTypedQuery).getResultList();
    }

    @Test
    @DisplayName("Should handle persistence exception when filtering topic types")
    void testTopicTypeFilterPersistenceException() {
        // Given
        String jpql = "SELECT f FROM TopicType f WHERE f.archived = 'N' ORDER BY f.topicTypeId ASC";
        when(entityManager.createQuery(eq(jpql), eq(TopicType.class)))
                .thenThrow(new PersistenceException("Database error"));

        // When & Then
        PersistenceException exception = assertThrows(PersistenceException.class,
                () -> topicTypeService.topicTypeFilter());
        assertEquals("Database error", exception.getMessage());

        verify(exceptionHandlingService).handleException(any(PersistenceException.class));
    }
}
