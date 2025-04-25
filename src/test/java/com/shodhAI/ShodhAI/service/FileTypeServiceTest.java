package com.shodhAI.ShodhAI.service;

import com.shodhAI.ShodhAI.Component.Constant;
import com.shodhAI.ShodhAI.Entity.FileType;
import com.shodhAI.ShodhAI.Service.FileTypeService;
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
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FileTypeServiceTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private ExceptionHandlingService exceptionHandlingService;

    @Mock
    private TypedQuery<FileType> fileTypeTypedQuery;

    @InjectMocks
    private FileTypeService fileTypeService;

    private FileType validFileType;
    private List<FileType> fileTypeList;

    @BeforeEach
    void setUp() {
        // Setup valid file type
        validFileType = new FileType();
        validFileType.setFileTypeId(1L);
        validFileType.setFileTypeName("Article");
        validFileType.setArchived('N');

        // Setup file type list
        FileType fileType2 = new FileType();
        fileType2.setFileTypeId(2L);
        fileType2.setFileTypeName("Video");
        fileType2.setArchived('N');

        fileTypeList = Arrays.asList(validFileType, fileType2);
    }

    @Test
    @DisplayName("Should get all file types successfully")
    void testGetAllFileType() throws Exception {
        // Given
        when(entityManager.createQuery(eq(Constant.GET_ALL_FILE_TYPES), eq(FileType.class)))
                .thenReturn(fileTypeTypedQuery);
        when(fileTypeTypedQuery.getResultList()).thenReturn(fileTypeList);

        // When
        List<FileType> result = fileTypeService.getAllFileType();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Article", result.get(0).getFileTypeName());
        assertEquals("Video", result.get(1).getFileTypeName());

        verify(entityManager).createQuery(eq(Constant.GET_ALL_FILE_TYPES), eq(FileType.class));
        verify(fileTypeTypedQuery).getResultList();
    }

    @Test
    @DisplayName("Should handle exception when getting all file types fails")
    void testGetAllFileTypeException() {
        // Given
        when(entityManager.createQuery(eq(Constant.GET_ALL_FILE_TYPES), eq(FileType.class)))
                .thenThrow(new RuntimeException("Database error"));

        // When & Then
        Exception exception = assertThrows(Exception.class, () -> fileTypeService.getAllFileType());
        assertEquals("Database error", exception.getMessage());

        verify(exceptionHandlingService).handleException(any(RuntimeException.class));
    }

    @Test
    @DisplayName("Should get file type by ID successfully")
    void testGetFileTypeById() throws Exception {
        // Given
        when(entityManager.createQuery(eq(Constant.GET_FILE_TYPE_BY_ID), eq(FileType.class)))
                .thenReturn(fileTypeTypedQuery);
        when(fileTypeTypedQuery.setParameter(eq("fileTypeId"), eq(1L))).thenReturn(fileTypeTypedQuery);
        when(fileTypeTypedQuery.getResultList()).thenReturn(Collections.singletonList(validFileType));

        // When
        FileType result = fileTypeService.getFileTypeById(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getFileTypeId());
        assertEquals("Article", result.getFileTypeName());

        verify(entityManager).createQuery(eq(Constant.GET_FILE_TYPE_BY_ID), eq(FileType.class));
        verify(fileTypeTypedQuery).setParameter(eq("fileTypeId"), eq(1L));
        verify(fileTypeTypedQuery).getResultList();
    }

    @Test
    @DisplayName("Should throw exception when file type not found by ID")
    void testGetFileTypeByIdNotFound() {
        // Given
        when(entityManager.createQuery(eq(Constant.GET_FILE_TYPE_BY_ID), eq(FileType.class)))
                .thenReturn(fileTypeTypedQuery);
        when(fileTypeTypedQuery.setParameter(eq("fileTypeId"), eq(99L))).thenReturn(fileTypeTypedQuery);
        when(fileTypeTypedQuery.getResultList()).thenReturn(Collections.emptyList());

        // When & Then
        IndexOutOfBoundsException exception = assertThrows(IndexOutOfBoundsException.class,
                () -> fileTypeService.getFileTypeById(99L));

        verify(exceptionHandlingService).handleException(any(IndexOutOfBoundsException.class));
    }

    @Test
    @DisplayName("Should add new file type successfully")
    void testAddFileType() throws Exception {
        // Given
        FileType newFileType = new FileType();
        newFileType.setFileTypeName("Podcast");

        when(entityManager.createQuery(eq(Constant.GET_ALL_FILE_TYPES), eq(FileType.class)))
                .thenReturn(fileTypeTypedQuery);
        when(fileTypeTypedQuery.getResultList()).thenReturn(fileTypeList);
        doNothing().when(entityManager).persist(any(FileType.class));

        // When
        FileType result = fileTypeService.addFileType(newFileType);

        // Then
        assertNotNull(result);
        assertEquals("Podcast", result.getFileTypeName());

        verify(entityManager).createQuery(eq(Constant.GET_ALL_FILE_TYPES), eq(FileType.class));
        verify(fileTypeTypedQuery).getResultList();
        verify(entityManager).persist(any(FileType.class));
    }

    @Test
    @DisplayName("Should throw exception when adding file type with empty name")
    void testAddFileTypeWithEmptyName() {
        // Given
        FileType newFileType = new FileType();
        newFileType.setFileTypeName("");

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> fileTypeService.addFileType(newFileType));
        assertEquals("File type name cannot be null or empty", exception.getMessage());

        verify(exceptionHandlingService).handleException(any(IllegalArgumentException.class));
    }

    @Test
    @DisplayName("Should throw exception when adding duplicate file type")
    void testAddDuplicateFileType() {
        // Given
        FileType newFileType = new FileType();
        newFileType.setFileTypeName("Article");

        when(entityManager.createQuery(eq(Constant.GET_ALL_FILE_TYPES), eq(FileType.class)))
                .thenReturn(fileTypeTypedQuery);
        when(fileTypeTypedQuery.getResultList()).thenReturn(fileTypeList);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> fileTypeService.addFileType(newFileType));
        assertEquals("File type already exists with name Article", exception.getMessage());

        verify(exceptionHandlingService).handleException(any(IllegalArgumentException.class));
    }

    @Test
    @DisplayName("Should update file type successfully")
    void testUpdateFileType() throws Exception {
        // Given
        FileType updatedFileType = new FileType();
        updatedFileType.setFileTypeName("Updated Article");

        when(entityManager.find(eq(FileType.class), eq(1L))).thenReturn(validFileType);
        when(entityManager.createQuery(eq(Constant.GET_ALL_FILE_TYPES), eq(FileType.class)))
                .thenReturn(fileTypeTypedQuery);
        when(fileTypeTypedQuery.getResultList()).thenReturn(fileTypeList);
        when(entityManager.merge(any(FileType.class))).thenReturn(validFileType);

        // When
        FileType result = fileTypeService.updateFileType(1L, updatedFileType);

        // Then
        assertNotNull(result);
        assertEquals("Updated Article", result.getFileTypeName());

        verify(entityManager).find(eq(FileType.class), eq(1L));
        verify(entityManager).createQuery(eq(Constant.GET_ALL_FILE_TYPES), eq(FileType.class));
        verify(fileTypeTypedQuery).getResultList();
        verify(entityManager).merge(any(FileType.class));
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent file type")
    void testUpdateNonExistentFileType() {
        // Given
        FileType updatedFileType = new FileType();
        updatedFileType.setFileTypeName("Updated Article");

        when(entityManager.find(eq(FileType.class), eq(99L))).thenReturn(null);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> fileTypeService.updateFileType(99L, updatedFileType));
        assertEquals("File type with id 99 not found", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when updating file type with empty name")
    void testUpdateFileTypeWithEmptyName() {
        // Given
        FileType updatedFileType = new FileType();
        updatedFileType.setFileTypeName("");

        when(entityManager.find(eq(FileType.class), eq(1L))).thenReturn(validFileType);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> fileTypeService.updateFileType(1L, updatedFileType));
        assertEquals("File type name cannot be null or empty", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when updating file type with duplicate name")
    void testUpdateFileTypeWithDuplicateName() {
        // Given
        FileType updatedFileType = new FileType();
        updatedFileType.setFileTypeName("Video");

        when(entityManager.find(eq(FileType.class), eq(1L))).thenReturn(validFileType);
        when(entityManager.createQuery(eq(Constant.GET_ALL_FILE_TYPES), eq(FileType.class)))
                .thenReturn(fileTypeTypedQuery);
        when(fileTypeTypedQuery.getResultList()).thenReturn(fileTypeList);

        // When & Then
        DataIntegrityViolationException exception = assertThrows(DataIntegrityViolationException.class,
                () -> fileTypeService.updateFileType(1L, updatedFileType));
        assertEquals("File type already exists with name Video", exception.getMessage());
    }

    @Test
    @DisplayName("Should delete file type successfully")
    void testDeleteFileTypeById() throws Exception {
        // Given
        when(entityManager.find(eq(FileType.class), eq(1L))).thenReturn(validFileType);
        when(entityManager.merge(any(FileType.class))).thenReturn(validFileType);

        // When
        FileType result = fileTypeService.deleteFileTypeById(1L);

        // Then
        assertNotNull(result);
        assertEquals('Y', result.getArchived());

        verify(entityManager).find(eq(FileType.class), eq(1L));
        verify(entityManager).merge(any(FileType.class));
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent file type")
    void testDeleteNonExistentFileType() {
        // Given
        when(entityManager.find(eq(FileType.class), eq(99L))).thenReturn(null);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> fileTypeService.deleteFileTypeById(99L));
        assertEquals("File type with id 99 not found", exception.getMessage());
    }

    @Test
    @DisplayName("Should filter file types successfully")
    void testFileTypeFilter() throws Exception {
        // Given
        String jpql = "SELECT f FROM FileType f WHERE f.archived = 'N' ORDER BY f.fileTypeId ASC";
        when(entityManager.createQuery(eq(jpql), eq(FileType.class))).thenReturn(fileTypeTypedQuery);
        when(fileTypeTypedQuery.getResultList()).thenReturn(fileTypeList);

        // When
        List<FileType> result = fileTypeService.fileTypeFilter();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Article", result.get(0).getFileTypeName());
        assertEquals("Video", result.get(1).getFileTypeName());

        verify(entityManager).createQuery(eq(jpql), eq(FileType.class));
        verify(fileTypeTypedQuery).getResultList();
    }

    @Test
    @DisplayName("Should handle persistence exception when filtering file types")
    void testFileTypeFilterPersistenceException() {
        // Given
        String jpql = "SELECT f FROM FileType f WHERE f.archived = 'N' ORDER BY f.fileTypeId ASC";
        when(entityManager.createQuery(eq(jpql), eq(FileType.class)))
                .thenThrow(new PersistenceException("Database error"));

        // When & Then
        PersistenceException exception = assertThrows(PersistenceException.class,
                () -> fileTypeService.fileTypeFilter());
        assertEquals("Database error", exception.getMessage());

        verify(exceptionHandlingService).handleException(any(PersistenceException.class));
    }
}
