package com.shodhAI.ShodhAI.service;

import com.shodhAI.ShodhAI.Component.Constant;
import com.shodhAI.ShodhAI.Entity.NotificationType;
import com.shodhAI.ShodhAI.Service.NotificationTypeService;
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
public class NotificationTypeServiceTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private ExceptionHandlingService exceptionHandlingService;

    @Mock
    private TypedQuery<NotificationType> notificationTypeTypedQuery;

    @InjectMocks
    private NotificationTypeService notificationTypeService;

    private NotificationType validNotificationType;
    private List<NotificationType> notificationTypeList;

    @BeforeEach
    void setUp() {
        // Setup valid notification type
        validNotificationType = new NotificationType();
        validNotificationType.setId(1L);
        validNotificationType.setTypeName("Article");
        validNotificationType.setArchived('N');

        // Setup notification type list
        NotificationType notificationType2 = new NotificationType();
        notificationType2.setId(2L);
        notificationType2.setTypeName("Video");
        notificationType2.setArchived('N');

        notificationTypeList = Arrays.asList(validNotificationType, notificationType2);
    }

    @Test
    @DisplayName("Should get all notification types successfully")
    void testGetAllNotificationType() throws Exception {
        // Given
        when(entityManager.createQuery(eq(Constant.GET_ALL_NOTIFICATION_TYPE), eq(NotificationType.class)))
                .thenReturn(notificationTypeTypedQuery);
        when(notificationTypeTypedQuery.getResultList()).thenReturn(notificationTypeList);

        // When
        List<NotificationType> result = notificationTypeService.getAllNotificationType();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Article", result.get(0).getTypeName());
        assertEquals("Video", result.get(1).getTypeName());

        verify(entityManager).createQuery(eq(Constant.GET_ALL_NOTIFICATION_TYPE), eq(NotificationType.class));
        verify(notificationTypeTypedQuery).getResultList();
    }

    @Test
    @DisplayName("Should handle exception when getting all notification types fails")
    void testGetAllNotificationTypeException() {
        // Given
        when(entityManager.createQuery(eq(Constant.GET_ALL_NOTIFICATION_TYPE), eq(NotificationType.class)))
                .thenThrow(new RuntimeException("Database error"));

        // When & Then
        Exception exception = assertThrows(Exception.class, () -> notificationTypeService.getAllNotificationType());
        assertEquals("Database error", exception.getMessage());

        verify(exceptionHandlingService).handleException(any(RuntimeException.class));
    }

    @Test
    @DisplayName("Should get notification type by ID successfully")
    void testGetNotificationTypeById() throws Exception {
        // Given
        when(entityManager.createQuery(eq(Constant.GET_NOTIFICATION_TYPE_BY_ID), eq(NotificationType.class)))
                .thenReturn(notificationTypeTypedQuery);
        when(notificationTypeTypedQuery.setParameter(eq("notificationTypeId"), eq(1L))).thenReturn(notificationTypeTypedQuery);
        when(notificationTypeTypedQuery.getResultList()).thenReturn(Collections.singletonList(validNotificationType));

        // When
        NotificationType result = notificationTypeService.getNotificationTypeById(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Article", result.getTypeName());

        verify(entityManager).createQuery(eq(Constant.GET_NOTIFICATION_TYPE_BY_ID), eq(NotificationType.class));
        verify(notificationTypeTypedQuery).setParameter(eq("notificationTypeId"), eq(1L));
        verify(notificationTypeTypedQuery).getResultList();
    }

    @Test
    @DisplayName("Should throw exception when notification type not found by ID")
    void testGetNotificationTypeByIdNotFound() {
        // Given
        when(entityManager.createQuery(eq(Constant.GET_NOTIFICATION_TYPE_BY_ID), eq(NotificationType.class)))
                .thenReturn(notificationTypeTypedQuery);
        when(notificationTypeTypedQuery.setParameter(eq("notificationTypeId"), eq(99L))).thenReturn(notificationTypeTypedQuery);
        when(notificationTypeTypedQuery.getResultList()).thenReturn(Collections.emptyList());

        // When & Then
        IndexOutOfBoundsException exception = assertThrows(IndexOutOfBoundsException.class,
                () -> notificationTypeService.getNotificationTypeById(99L));

        verify(exceptionHandlingService).handleException(any(IndexOutOfBoundsException.class));
    }

    @Test
    @DisplayName("Should add new notification type successfully")
    void testAddNotificationType() throws Exception {
        // Given
        NotificationType newNotificationType = new NotificationType();
        newNotificationType.setTypeName("Podcast");

        when(entityManager.createQuery(eq(Constant.GET_ALL_NOTIFICATION_TYPE), eq(NotificationType.class)))
                .thenReturn(notificationTypeTypedQuery);
        when(notificationTypeTypedQuery.getResultList()).thenReturn(notificationTypeList);
        doNothing().when(entityManager).persist(any(NotificationType.class));

        // When
        NotificationType result = notificationTypeService.addNotificationType(newNotificationType);

        // Then
        assertNotNull(result);
        assertEquals("Podcast", result.getTypeName());

        verify(entityManager).createQuery(eq(Constant.GET_ALL_NOTIFICATION_TYPE), eq(NotificationType.class));
        verify(notificationTypeTypedQuery).getResultList();
        verify(entityManager).persist(any(NotificationType.class));
    }

    @Test
    @DisplayName("Should throw exception when adding notification type with empty name")
    void testAddNotificationTypeWithEmptyName() {
        // Given
        NotificationType newNotificationType = new NotificationType();
        newNotificationType.setTypeName("");

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> notificationTypeService.addNotificationType(newNotificationType));
        assertEquals("Notification type name cannot be null or empty", exception.getMessage());

        verify(exceptionHandlingService).handleException(any(IllegalArgumentException.class));
    }

    @Test
    @DisplayName("Should throw exception when adding duplicate notification type")
    void testAddDuplicateNotificationType() {
        // Given
        NotificationType newNotificationType = new NotificationType();
        newNotificationType.setTypeName("Article");

        when(entityManager.createQuery(eq(Constant.GET_ALL_NOTIFICATION_TYPE), eq(NotificationType.class)))
                .thenReturn(notificationTypeTypedQuery);
        when(notificationTypeTypedQuery.getResultList()).thenReturn(notificationTypeList);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> notificationTypeService.addNotificationType(newNotificationType));
        assertEquals("Notification type already exists with name Article", exception.getMessage());

        verify(exceptionHandlingService).handleException(any(IllegalArgumentException.class));
    }

    @Test
    @DisplayName("Should update notification type successfully")
    void testUpdateNotificationType() throws Exception {
        // Given
        NotificationType updatedNotificationType = new NotificationType();
        updatedNotificationType.setTypeName("Updated Article");

        when(entityManager.find(eq(NotificationType.class), eq(1L))).thenReturn(validNotificationType);
        when(entityManager.createQuery(eq(Constant.GET_ALL_NOTIFICATION_TYPE), eq(NotificationType.class)))
                .thenReturn(notificationTypeTypedQuery);
        when(notificationTypeTypedQuery.getResultList()).thenReturn(notificationTypeList);
        when(entityManager.merge(any(NotificationType.class))).thenReturn(validNotificationType);

        // When
        NotificationType result = notificationTypeService.updateNotificationType(1L, updatedNotificationType);

        // Then
        assertNotNull(result);
        assertEquals("Updated Article", result.getTypeName());

        verify(entityManager).find(eq(NotificationType.class), eq(1L));
        verify(entityManager).createQuery(eq(Constant.GET_ALL_NOTIFICATION_TYPE), eq(NotificationType.class));
        verify(notificationTypeTypedQuery).getResultList();
        verify(entityManager).merge(any(NotificationType.class));
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent notification type")
    void testUpdateNonExistentNotificationType() {
        // Given
        NotificationType updatedNotificationType = new NotificationType();
        updatedNotificationType.setTypeName("Updated Article");

        when(entityManager.find(eq(NotificationType.class), eq(99L))).thenReturn(null);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> notificationTypeService.updateNotificationType(99L, updatedNotificationType));
        assertEquals("Notification type with id 99 not found", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when updating notification type with empty name")
    void testUpdateNotificationTypeWithEmptyName() {
        // Given
        NotificationType updatedNotificationType = new NotificationType();
        updatedNotificationType.setTypeName("");

        when(entityManager.find(eq(NotificationType.class), eq(1L))).thenReturn(validNotificationType);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> notificationTypeService.updateNotificationType(1L, updatedNotificationType));
        assertEquals("Notification type name cannot be null or empty", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when updating notification type with duplicate name")
    void testUpdateNotificationTypeWithDuplicateName() {
        // Given
        NotificationType updatedNotificationType = new NotificationType();
        updatedNotificationType.setTypeName("Video");

        when(entityManager.find(eq(NotificationType.class), eq(1L))).thenReturn(validNotificationType);
        when(entityManager.createQuery(eq(Constant.GET_ALL_NOTIFICATION_TYPE), eq(NotificationType.class)))
                .thenReturn(notificationTypeTypedQuery);
        when(notificationTypeTypedQuery.getResultList()).thenReturn(notificationTypeList);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> notificationTypeService.updateNotificationType(1L, updatedNotificationType));
        assertEquals("Notification type already exists with name Video", exception.getMessage());
    }

    @Test
    @DisplayName("Should delete notification type successfully")
    void testDeleteNotificationTypeById() throws Exception {
        // Given
        when(entityManager.find(eq(NotificationType.class), eq(1L))).thenReturn(validNotificationType);
        when(entityManager.merge(any(NotificationType.class))).thenReturn(validNotificationType);

        // When
        NotificationType result = notificationTypeService.deleteNotificationTypeById(1L);

        // Then
        assertNotNull(result);
        assertEquals('Y', result.getArchived());

        verify(entityManager).find(eq(NotificationType.class), eq(1L));
        verify(entityManager).merge(any(NotificationType.class));
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent notification type")
    void testDeleteNonExistentNotificationType() {
        // Given
        when(entityManager.find(eq(NotificationType.class), eq(99L))).thenReturn(null);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> notificationTypeService.deleteNotificationTypeById(99L));
        assertEquals("Notification type with id 99 not found", exception.getMessage());
    }
}
