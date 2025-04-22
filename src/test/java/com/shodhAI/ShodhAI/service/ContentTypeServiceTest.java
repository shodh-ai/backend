package com.shodhAI.ShodhAI.service;

import com.shodhAI.ShodhAI.Component.Constant;
import com.shodhAI.ShodhAI.Entity.ContentType;
import com.shodhAI.ShodhAI.Service.ContentTypeService;
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
public class ContentTypeServiceTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private ExceptionHandlingService exceptionHandlingService;

    @Mock
    private TypedQuery<ContentType> contentTypeTypedQuery;

    @InjectMocks
    private ContentTypeService contentTypeService;

    private ContentType validContentType;
    private List<ContentType> contentTypeList;

    @BeforeEach
    void setUp() {
        // Setup valid content type
        validContentType = new ContentType();
        validContentType.setContentTypeId(1L);
        validContentType.setContentTypeName("Article");
        validContentType.setCreatedDate(new Date());
        validContentType.setArchived('N');

        // Setup content type list
        ContentType contentType2 = new ContentType();
        contentType2.setContentTypeId(2L);
        contentType2.setContentTypeName("Video");
        contentType2.setCreatedDate(new Date());
        contentType2.setArchived('N');

        contentTypeList = Arrays.asList(validContentType, contentType2);
    }

    @Test
    @DisplayName("Should get all content types successfully")
    void testGetAllContentType() throws Exception {
        // Given
        when(entityManager.createQuery(eq(Constant.GET_ALL_CONTENT_TYPE), eq(ContentType.class)))
                .thenReturn(contentTypeTypedQuery);
        when(contentTypeTypedQuery.getResultList()).thenReturn(contentTypeList);

        // When
        List<ContentType> result = contentTypeService.getAllContentType();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Article", result.get(0).getContentTypeName());
        assertEquals("Video", result.get(1).getContentTypeName());

        verify(entityManager).createQuery(eq(Constant.GET_ALL_CONTENT_TYPE), eq(ContentType.class));
        verify(contentTypeTypedQuery).getResultList();
    }

    @Test
    @DisplayName("Should handle exception when getting all content types fails")
    void testGetAllContentTypeException() {
        // Given
        when(entityManager.createQuery(eq(Constant.GET_ALL_CONTENT_TYPE), eq(ContentType.class)))
                .thenThrow(new RuntimeException("Database error"));

        // When & Then
        Exception exception = assertThrows(Exception.class, () -> contentTypeService.getAllContentType());
        assertEquals("Database error", exception.getMessage());

        verify(exceptionHandlingService).handleException(any(RuntimeException.class));
    }

    @Test
    @DisplayName("Should get content type by ID successfully")
    void testGetContentTypeById() throws Exception {
        // Given
        when(entityManager.createQuery(eq(Constant.GET_CONTENT_TYPE_BY_ID), eq(ContentType.class)))
                .thenReturn(contentTypeTypedQuery);
        when(contentTypeTypedQuery.setParameter(eq("contentTypeId"), eq(1L))).thenReturn(contentTypeTypedQuery);
        when(contentTypeTypedQuery.getResultList()).thenReturn(Collections.singletonList(validContentType));

        // When
        ContentType result = contentTypeService.getContentTypeById(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getContentTypeId());
        assertEquals("Article", result.getContentTypeName());

        verify(entityManager).createQuery(eq(Constant.GET_CONTENT_TYPE_BY_ID), eq(ContentType.class));
        verify(contentTypeTypedQuery).setParameter(eq("contentTypeId"), eq(1L));
        verify(contentTypeTypedQuery).getResultList();
    }

    @Test
    @DisplayName("Should throw exception when content type not found by ID")
    void testGetContentTypeByIdNotFound() {
        // Given
        when(entityManager.createQuery(eq(Constant.GET_CONTENT_TYPE_BY_ID), eq(ContentType.class)))
                .thenReturn(contentTypeTypedQuery);
        when(contentTypeTypedQuery.setParameter(eq("contentTypeId"), eq(99L))).thenReturn(contentTypeTypedQuery);
        when(contentTypeTypedQuery.getResultList()).thenReturn(Collections.emptyList());

        // When & Then
        IndexOutOfBoundsException exception = assertThrows(IndexOutOfBoundsException.class,
                () -> contentTypeService.getContentTypeById(99L));

        verify(exceptionHandlingService).handleException(any(IndexOutOfBoundsException.class));
    }

    @Test
    @DisplayName("Should add new content type successfully")
    void testAddContentType() throws Exception {
        // Given
        ContentType newContentType = new ContentType();
        newContentType.setContentTypeName("Podcast");

        when(entityManager.createQuery(eq(Constant.GET_ALL_CONTENT_TYPE), eq(ContentType.class)))
                .thenReturn(contentTypeTypedQuery);
        when(contentTypeTypedQuery.getResultList()).thenReturn(contentTypeList);
        doNothing().when(entityManager).persist(any(ContentType.class));

        // When
        ContentType result = contentTypeService.addContentType(newContentType);

        // Then
        assertNotNull(result);
        assertEquals("Podcast", result.getContentTypeName());
        assertNotNull(result.getCreatedDate());

        verify(entityManager).createQuery(eq(Constant.GET_ALL_CONTENT_TYPE), eq(ContentType.class));
        verify(contentTypeTypedQuery).getResultList();
        verify(entityManager).persist(any(ContentType.class));
    }

    @Test
    @DisplayName("Should throw exception when adding content type with empty name")
    void testAddContentTypeWithEmptyName() {
        // Given
        ContentType newContentType = new ContentType();
        newContentType.setContentTypeName("");

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> contentTypeService.addContentType(newContentType));
        assertEquals("Content type name cannot be null or empty", exception.getMessage());

        verify(exceptionHandlingService).handleException(any(IllegalArgumentException.class));
    }

    @Test
    @DisplayName("Should throw exception when adding duplicate content type")
    void testAddDuplicateContentType() {
        // Given
        ContentType newContentType = new ContentType();
        newContentType.setContentTypeName("Article");

        when(entityManager.createQuery(eq(Constant.GET_ALL_CONTENT_TYPE), eq(ContentType.class)))
                .thenReturn(contentTypeTypedQuery);
        when(contentTypeTypedQuery.getResultList()).thenReturn(contentTypeList);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> contentTypeService.addContentType(newContentType));
        assertEquals("Content type already exists with name Article", exception.getMessage());

        verify(exceptionHandlingService).handleException(any(IllegalArgumentException.class));
    }

    @Test
    @DisplayName("Should update content type successfully")
    void testUpdateContentType() throws Exception {
        // Given
        ContentType updatedContentType = new ContentType();
        updatedContentType.setContentTypeName("Updated Article");

        when(entityManager.find(eq(ContentType.class), eq(1L))).thenReturn(validContentType);
        when(entityManager.createQuery(eq(Constant.GET_ALL_CONTENT_TYPE), eq(ContentType.class)))
                .thenReturn(contentTypeTypedQuery);
        when(contentTypeTypedQuery.getResultList()).thenReturn(contentTypeList);
        when(entityManager.merge(any(ContentType.class))).thenReturn(validContentType);

        // When
        ContentType result = contentTypeService.updateContentType(1L, updatedContentType);

        // Then
        assertNotNull(result);
        assertEquals("Updated Article", result.getContentTypeName());
        assertNotNull(result.getUpdatedDate());

        verify(entityManager).find(eq(ContentType.class), eq(1L));
        verify(entityManager).createQuery(eq(Constant.GET_ALL_CONTENT_TYPE), eq(ContentType.class));
        verify(contentTypeTypedQuery).getResultList();
        verify(entityManager).merge(any(ContentType.class));
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent content type")
    void testUpdateNonExistentContentType() {
        // Given
        ContentType updatedContentType = new ContentType();
        updatedContentType.setContentTypeName("Updated Article");

        when(entityManager.find(eq(ContentType.class), eq(99L))).thenReturn(null);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> contentTypeService.updateContentType(99L, updatedContentType));
        assertEquals("Content type with id 99 not found", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when updating content type with empty name")
    void testUpdateContentTypeWithEmptyName() {
        // Given
        ContentType updatedContentType = new ContentType();
        updatedContentType.setContentTypeName("");

        when(entityManager.find(eq(ContentType.class), eq(1L))).thenReturn(validContentType);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> contentTypeService.updateContentType(1L, updatedContentType));
        assertEquals("Content type name cannot be null or empty", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when updating content type with duplicate name")
    void testUpdateContentTypeWithDuplicateName() {
        // Given
        ContentType updatedContentType = new ContentType();
        updatedContentType.setContentTypeName("Video");

        when(entityManager.find(eq(ContentType.class), eq(1L))).thenReturn(validContentType);
        when(entityManager.createQuery(eq(Constant.GET_ALL_CONTENT_TYPE), eq(ContentType.class)))
                .thenReturn(contentTypeTypedQuery);
        when(contentTypeTypedQuery.getResultList()).thenReturn(contentTypeList);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> contentTypeService.updateContentType(1L, updatedContentType));
        assertEquals("Content type already exists with name Video", exception.getMessage());
    }

    @Test
    @DisplayName("Should delete content type successfully")
    void testDeleteContentTypeById() throws Exception {
        // Given
        when(entityManager.find(eq(ContentType.class), eq(1L))).thenReturn(validContentType);
        when(entityManager.merge(any(ContentType.class))).thenReturn(validContentType);

        // When
        ContentType result = contentTypeService.deleteContentTypeById(1L);

        // Then
        assertNotNull(result);
        assertEquals('Y', result.getArchived());

        verify(entityManager).find(eq(ContentType.class), eq(1L));
        verify(entityManager).merge(any(ContentType.class));
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent content type")
    void testDeleteNonExistentContentType() {
        // Given
        when(entityManager.find(eq(ContentType.class), eq(99L))).thenReturn(null);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> contentTypeService.deleteContentTypeById(99L));
        assertEquals("Content type with id 99 not found", exception.getMessage());
    }

    @Test
    @DisplayName("Should filter content types successfully")
    void testContentTypeFilter() throws Exception {
        // Given
        String jpql = "SELECT f FROM ContentType f WHERE f.archived = 'N' ORDER BY f.contentTypeId ASC";
        when(entityManager.createQuery(eq(jpql), eq(ContentType.class))).thenReturn(contentTypeTypedQuery);
        when(contentTypeTypedQuery.getResultList()).thenReturn(contentTypeList);

        // When
        List<ContentType> result = contentTypeService.contentTypeFilter();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Article", result.get(0).getContentTypeName());
        assertEquals("Video", result.get(1).getContentTypeName());

        verify(entityManager).createQuery(eq(jpql), eq(ContentType.class));
        verify(contentTypeTypedQuery).getResultList();
    }

    @Test
    @DisplayName("Should handle persistence exception when filtering content types")
    void testContentTypeFilterPersistenceException() {
        // Given
        String jpql = "SELECT f FROM ContentType f WHERE f.archived = 'N' ORDER BY f.contentTypeId ASC";
        when(entityManager.createQuery(eq(jpql), eq(ContentType.class)))
                .thenThrow(new PersistenceException("Database error"));

        // When & Then
        PersistenceException exception = assertThrows(PersistenceException.class,
                () -> contentTypeService.contentTypeFilter());
        assertEquals("Database error", exception.getMessage());

        verify(exceptionHandlingService).handleException(any(PersistenceException.class));
    }
}
