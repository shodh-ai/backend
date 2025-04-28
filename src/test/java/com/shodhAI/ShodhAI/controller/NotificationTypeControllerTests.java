package com.shodhAI.ShodhAI.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shodhAI.ShodhAI.Controller.NotificationTypeController;
import com.shodhAI.ShodhAI.Entity.NotificationType;
import com.shodhAI.ShodhAI.Service.NotificationTypeService;
import com.shodhAI.ShodhAI.Service.ExceptionHandlingService;
import com.shodhAI.ShodhAI.Service.RoleService;
import com.shodhAI.ShodhAI.Service.SanitizerService;
import com.shodhAI.ShodhAI.Service.StudentService;
import com.shodhAI.ShodhAI.configuration.TestJwtConfig;
import com.shodhAI.ShodhAI.configuration.TestSecurityConfig;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import({TestSecurityConfig.class, TestJwtConfig.class})
@WebMvcTest(NotificationTypeController.class)
@ExtendWith({SpringExtension.class, MockitoExtension.class})
@ContextConfiguration(classes = TestSecurityConfig.class)
public class NotificationTypeControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SanitizerService sanitizerService;

    @MockBean
    private NotificationTypeService notificationTypeService;

    @MockBean
    private EntityManager entityManager;

    @MockBean
    private ExceptionHandlingService exceptionHandlingService;

    @MockBean
    private RoleService roleService;

    @MockBean
    private StudentService studentService;

    @Test
    @DisplayName("testGetAllNotificationTypes")
    void testGetAllNotificationTypes() throws Exception {
        // Create sample notification types
        List<NotificationType> notificationTypes = new ArrayList<>();
        NotificationType type1 = new NotificationType();
        type1.setId(1L);
        type1.setTypeName("Article");

        NotificationType type2 = new NotificationType();
        type2.setId(2L);
        type2.setTypeName("Video");

        notificationTypes.add(type1);
        notificationTypes.add(type2);

        // Mock service call
        when(notificationTypeService.notificationTypeFilter()).thenReturn(notificationTypes);

        // Perform GET request
        mockMvc.perform(get("/notification-type/get-filter-notification-types")
                        .param("offset", "0")
                        .param("limit", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Notification Types Retrieved Successfully"))
                .andExpect(jsonPath("$.data.totalItems").value(2))
                .andExpect(jsonPath("$.data.currentPage").value(0));

        // Verify service method was called
        verify(notificationTypeService).notificationTypeFilter();
    }

    @Test
    @DisplayName("testGetAllNotificationTypesEmptyList")
    void testGetAllNotificationTypesEmptyList() throws Exception {
        // Mock empty list response
        when(notificationTypeService.notificationTypeFilter()).thenReturn(new ArrayList<>());

        // Perform GET request
        mockMvc.perform(get("/notification-type/get-filter-notification-types")
                        .param("offset", "0")
                        .param("limit", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("No notification types found"));

        // Verify service method was called
        verify(notificationTypeService).notificationTypeFilter();
    }

    @Test
    @DisplayName("testGetNotificationTypeById")
    void testGetNotificationTypeById() throws Exception {
        // Create sample notification type
        NotificationType notificationType = new NotificationType();
        notificationType.setId(1L);
        notificationType.setTypeName("Article");

        // Mock service call
        when(notificationTypeService.getNotificationTypeById(1L)).thenReturn(notificationType);

        // Perform GET request
        mockMvc.perform(get("/notification-type/get-notification-type-by-id/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Notification Type Retrieved Successfully"))
                .andExpect(jsonPath("$.data.notification_type_id").value(1L));


        // Verify service method was called
        verify(notificationTypeService).getNotificationTypeById(1L);
    }

    @Test
    @DisplayName("testGetNotificationTypeByIdNotFound")
    void testGetNotificationTypeByIdNotFound() throws Exception {
        // Mock not found scenario
        when(notificationTypeService.getNotificationTypeById(999L)).thenReturn(null);

        // Perform GET request
        mockMvc.perform(get("/notification-type/get-notification-type-by-id/999"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Data not present in the DB"));
        // Verify service method was called
        verify(notificationTypeService).getNotificationTypeById(999L);
    }

    @Test
    @DisplayName("testAddNotificationType")
    void testAddNotificationType() throws Exception {
        // Create a sample NotificationType
        NotificationType notificationType = new NotificationType();
        notificationType.setTypeName("Article");

        NotificationType savedNotificationType = new NotificationType();
        savedNotificationType.setId(1L);
        savedNotificationType.setTypeName("Article");

        // Mock service call
        when(notificationTypeService.addNotificationType(any(NotificationType.class))).thenReturn(savedNotificationType);

        // Perform POST request
        mockMvc.perform(post("/notification-type/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(notificationType)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.message").value("Notification type is successfully added"))
                .andExpect(jsonPath("$.data.notification_type_id").value(1L));

        // Verify service method was called
        verify(notificationTypeService).addNotificationType(any(NotificationType.class));
    }

    @Test
    @DisplayName("testAddNotificationTypeIllegalArgumentException")
    void testAddNotificationTypeIllegalArgumentException() throws Exception {
        // Create a sample NotificationType
        NotificationType notificationType = new NotificationType();
        notificationType.setTypeName(""); // Invalid name

        // Mock service exception
        when(notificationTypeService.addNotificationType(any(NotificationType.class)))
                .thenThrow(new IllegalArgumentException("Notification type name cannot be empty"));

        // Perform POST request
        mockMvc.perform(post("/notification-type/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(notificationType)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("Notification type name cannot be empty"));

        // Verify exception handling
        verify(notificationTypeService).addNotificationType(any(NotificationType.class));
        verify(exceptionHandlingService).handleException(any(IllegalArgumentException.class));
    }

    @Test
    @DisplayName("testUpdateNotificationType")
    void testUpdateNotificationType() throws Exception {
        // Create a sample NotificationType for update
        NotificationType notificationType = new NotificationType();
        notificationType.setTypeName("Updated Article");

        NotificationType existingNotificationType = new NotificationType();
        existingNotificationType.setId(1L);
        existingNotificationType.setTypeName("Article");

        NotificationType updatedNotificationType = new NotificationType();
        updatedNotificationType.setId(1L);
        updatedNotificationType.setTypeName("Updated Article");

        // Mock service calls
        when(notificationTypeService.getNotificationTypeById(1L)).thenReturn(existingNotificationType);
        when(notificationTypeService.updateNotificationType(eq(1L), any(NotificationType.class))).thenReturn(updatedNotificationType);

        // Perform PATCH request
        mockMvc.perform(patch("/notification-type/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(notificationType)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Notification type is updated successfully "))
                .andExpect(jsonPath("$.data.notification_type_id").value(1L))
                .andExpect(jsonPath("$.data.type_name").value("Updated Article"));

        // Verify service methods were called
        verify(notificationTypeService).getNotificationTypeById(1L);
        verify(notificationTypeService).updateNotificationType(eq(1L), any(NotificationType.class));
    }

    @Test
    @DisplayName("testUpdateNotificationTypeNotFound")
    void testUpdateNotificationTypeNotFound() throws Exception {
        // Create a sample NotificationType for update
        NotificationType notificationType = new NotificationType();
        notificationType.setTypeName("Updated Article");

        // Mock service call - Notification type not found
        when(notificationTypeService.getNotificationTypeById(999L)).thenReturn(null);

        // Perform PATCH request
        mockMvc.perform(patch("/notification-type/update/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(notificationType)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Data not present in the DB"));

        // Verify service method was called
        verify(notificationTypeService).getNotificationTypeById(999L);
        verify(notificationTypeService, never()).updateNotificationType(eq(999L), any(NotificationType.class));
    }

    @Test
    @DisplayName("testDeleteNotificationType")
    void testDeleteNotificationType() throws Exception {
        // Create sample notification type
        NotificationType notificationType = new NotificationType();
        notificationType.setId(1L);
        notificationType.setTypeName("Article");

        // Mock service calls
        when(notificationTypeService.getNotificationTypeById(1L)).thenReturn(notificationType);
        when(notificationTypeService.deleteNotificationTypeById(1L)).thenReturn(notificationType);

        // Perform DELETE request
        mockMvc.perform(delete("/notification-type/delete/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Notification type is archived successfully"))
                .andExpect(jsonPath("$.data.notification_type_id").value(1L));

        // Verify service methods were called
        verify(notificationTypeService).getNotificationTypeById(1L);
        verify(notificationTypeService).deleteNotificationTypeById(1L);
    }

    @Test
    @DisplayName("testDeleteNotificationTypeNotFound")
    void testDeleteNotificationTypeNotFound() throws Exception {
        // Mock not found scenario
        when(notificationTypeService.getNotificationTypeById(999L)).thenReturn(null);

        // Perform DELETE request
        mockMvc.perform(delete("/notification-type/delete/999"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Data not present in the DB"));

        // Verify service methods called
        verify(notificationTypeService).getNotificationTypeById(999L);
        verify(notificationTypeService, never()).deleteNotificationTypeById(999L);
    }

    @Test
    @DisplayName("testGetFilterNotificationTypes")
    void testGetFilterNotificationTypes() throws Exception {
        // Create sample notification types
        List<NotificationType> notificationTypes = new ArrayList<>();
        NotificationType type1 = new NotificationType();
        type1.setId(1L);
        type1.setTypeName("Article");

        NotificationType type2 = new NotificationType();
        type2.setId(2L);
        type2.setTypeName("Video");

        notificationTypes.add(type1);
        notificationTypes.add(type2);

        // Mock service call
        when(notificationTypeService.notificationTypeFilter()).thenReturn(notificationTypes);

        // Perform GET request
        mockMvc.perform(get("/notification-type/get-filter-notification-types")
                        .param("offset", "0")
                        .param("limit", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Notification Types Retrieved Successfully"))
                .andExpect(jsonPath("$.data.totalItems").value(2))
                .andExpect(jsonPath("$.data.currentPage").value(0));

        // Verify service method was called
        verify(notificationTypeService).notificationTypeFilter();
    }

    @Test
    @DisplayName("testGetFilterNotificationTypesEmptyList")
    void testGetFilterNotificationTypesEmptyList() throws Exception {
        // Mock empty list response
        when(notificationTypeService.notificationTypeFilter()).thenReturn(new ArrayList<>());

        // Perform GET request
        mockMvc.perform(get("/notification-type/get-filter-notification-types")
                        .param("offset", "0")
                        .param("limit", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("No notification types found"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isEmpty());

        // Verify service method was called
        verify(notificationTypeService).notificationTypeFilter();
    }
}
