package com.shodhAI.ShodhAI.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shodhAI.ShodhAI.Component.JwtUtil;
import com.shodhAI.ShodhAI.Controller.NotificationController;
import com.shodhAI.ShodhAI.Entity.Notification;
import com.shodhAI.ShodhAI.Service.ExceptionHandlingService;
import com.shodhAI.ShodhAI.Service.NotificationService;
import com.shodhAI.ShodhAI.Service.RoleService;
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

import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import({TestSecurityConfig.class, TestJwtConfig.class})
@WebMvcTest(NotificationController.class)
@ExtendWith({SpringExtension.class, MockitoExtension.class})
@ContextConfiguration(classes = TestSecurityConfig.class)
public class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RoleService roleService;

    @MockBean
    private StudentService studentService;

    @MockBean
    private NotificationService notificationService;

    @MockBean
    private EntityManager entityManager;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private ExceptionHandlingService exceptionHandlingService;

    @Test
    @DisplayName("testSendCourseNotification")
    void testSendCourseNotification() throws Exception {
        // Sample notification data
        Long facultyId = 1L;
        Long courseId = 1L;
        String title = "Important Announcement";
        String message = "Class is rescheduled to next week";
        String notificationTypeIds = "1,2";

        // Sample response notification
        Notification notification = new Notification();
        notification.setId(1L);
        notification.setTitle("Important Announcement");
        notification.setMessage("Class is rescheduled to next week");

        // Mock service calls
        when(notificationService.createNotification(
                eq(title), eq(message), eq(facultyId), anyList(), any(Date.class)))
                .thenReturn(notification);
        
        doNothing().when(notificationService).sendNotificationToCourse(eq(courseId), any(Notification.class));
        
        when(notificationService.getNotificationWithRecipients(eq(1L)))
                .thenReturn(notification);

        // Perform POST request
        mockMvc.perform(post("/notifications/send-notification")
                        .param("facultyId", facultyId.toString())
                        .param("courseId", courseId.toString())
                        .param("title", title)
                        .param("message", message)
                        .param("notificationTypeIds", notificationTypeIds)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Notification sent successfully"))
                .andExpect(jsonPath("$.data.notification_id").value(1L))
                .andExpect(jsonPath("$.data.title").value("Important Announcement"));

        // Verify service methods were called
        verify(notificationService).createNotification(
                eq(title), eq(message), eq(facultyId), anyList(), any(Date.class));
        verify(notificationService).sendNotificationToCourse(eq(courseId), any(Notification.class));
        verify(notificationService).getNotificationWithRecipients(eq(1L));
    }

    @Test
    @DisplayName("testSendCourseNotificationMissingFacultyId")
    void testSendCourseNotificationMissingFacultyId() throws Exception {
        // Missing facultyId
        Long courseId = 1L;
        String message = "Class is rescheduled";
        String notificationTypeIds = "1,2";

        // Perform POST request with missing facultyId
        mockMvc.perform(post("/notifications/send-notification")
                        .param("courseId", courseId.toString())
                        .param("message", message)
                        .param("notificationTypeIds", notificationTypeIds)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andDo(print())
                .andExpect(status().isBadRequest());

        // Verify no service methods were called
        verify(notificationService, never()).createNotification(
                any(), any(), any(), any(), any());
        verify(notificationService, never()).sendNotificationToCourse(any(), any());
    }

    @Test
    @DisplayName("testSendCourseNotificationMissingCourseId")
    void testSendCourseNotificationMissingCourseId() throws Exception {
        // Missing courseId
        Long facultyId = 1L;
        String message = "Class is rescheduled";
        String notificationTypeIds = "1,2";

        // Mock exception handling for missing courseId
        when(exceptionHandlingService.handleException(any(IllegalArgumentException.class)))
                .thenReturn("Notification type IDs cannot be empty");

        // Perform POST request with missing courseId
        mockMvc.perform(post("/notifications/send-notification")
                        .param("facultyId", facultyId.toString())
                        .param("message", message)
                        .param("notificationTypeIds", notificationTypeIds)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("Missing required parameter: courseId"));
    }

    @Test
    @DisplayName("testSendCourseNotificationEmptyNotificationTypeIds")
    void testSendCourseNotificationEmptyNotificationTypeIds() throws Exception {
        // Empty notificationTypeIds
        Long facultyId = 1L;
        Long courseId = 1L;
        String message = "Class is rescheduled";
        String notificationTypeIds = "";

        // Mock exception handling for empty notificationTypeIds
        when(exceptionHandlingService.handleException(any(IllegalArgumentException.class)))
                .thenReturn("Notification type IDs cannot be empty");

        // Perform POST request with empty notificationTypeIds
        mockMvc.perform(post("/notifications/send-notification")
                        .param("facultyId", facultyId.toString())
                        .param("courseId", courseId.toString())
                        .param("message", message)
                        .param("notificationTypeIds", notificationTypeIds)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("Notification type IDs cannot be empty"));

        // Verify exception handling was called
        verify(exceptionHandlingService).handleException(any(IllegalArgumentException.class));
    }

    @Test
    @DisplayName("testGetNotificationsByFaculty")
    void testGetNotificationsByFaculty() throws Exception {
        // Create sample response map
        Map<String, Object> responseMap = new HashMap<>();
        List<Notification> notifications = new ArrayList<>();
        
        Notification notification1 = new Notification();
        notification1.setId(1L);
        notification1.setTitle("Announcement 1");
        notification1.setMessage("Message 1");
        
        Notification notification2 = new Notification();
        notification2.setId(2L);
        notification2.setTitle("Announcement 2");
        notification2.setMessage("Message 2");
        
        notifications.add(notification1);
        notifications.add(notification2);
        
        responseMap.put("notifications", notifications);
        responseMap.put("totalItems", 2);
        responseMap.put("totalPages", 1);
        responseMap.put("currentPage", 0);

        // Mock service call
        when(notificationService.getNotificationsBySender(eq(1L), eq(0), eq(10)))
                .thenReturn(responseMap);

        // Perform GET request
        mockMvc.perform(get("/notifications/faculty/1")
                        .param("offset", "0")
                        .param("limit", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Notifications retrieved successfully"))
                .andExpect(jsonPath("$.data.totalItems").value(2))
                .andExpect(jsonPath("$.data.notifications.length()").value(2));

        // Verify service method was called
        verify(notificationService).getNotificationsBySender(eq(1L), eq(0), eq(10));
    }

    @Test
    @DisplayName("testGetNotificationsByFacultyInvalidInput")
    void testGetNotificationsByFacultyInvalidInput() throws Exception {
        // Mock service call with exception
        when(notificationService.getNotificationsBySender(eq(-1L), eq(0), eq(10)))
                .thenThrow(new IllegalArgumentException("Faculty ID cannot be negative"));

        when(exceptionHandlingService.handleException(any(IllegalArgumentException.class)))
                .thenReturn("Notification type IDs cannot be empty");

        // Perform GET request with invalid faculty ID
        mockMvc.perform(get("/notifications/faculty/-1")
                        .param("offset", "0")
                        .param("limit", "10"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("Faculty ID cannot be negative"));

        // Verify service method and exception handling were called
        verify(notificationService).getNotificationsBySender(eq(-1L), eq(0), eq(10));
        verify(exceptionHandlingService).handleException(any(IllegalArgumentException.class));
    }

    @Test
    @DisplayName("testGetNotificationsForStudent")
    void testGetNotificationsForStudent() throws Exception {
        // Create sample response map
        Map<String, Object> responseMap = new HashMap<>();
        List<Map<String, Object>> notifications = new ArrayList<>();
        
        Map<String, Object> notification1 = new HashMap<>();
        notification1.put("id", 1L);
        notification1.put("title", "Student Announcement 1");
        notification1.put("message", "Student Message 1");
        notification1.put("isRead", false);
        
        Map<String, Object> notification2 = new HashMap<>();
        notification2.put("id", 2L);
        notification2.put("title", "Student Announcement 2");
        notification2.put("message", "Student Message 2");
        notification2.put("isRead", true);
        
        notifications.add(notification1);
        notifications.add(notification2);
        
        responseMap.put("notifications", notifications);
        responseMap.put("totalItems", 2);
        responseMap.put("totalPages", 1);
        responseMap.put("currentPage", 0);

        // Mock service call
        when(notificationService.getNotificationsForStudent(eq(1L), eq(0), eq(10)))
                .thenReturn(responseMap);

        // Perform GET request
        mockMvc.perform(get("/notifications/student/1")
                        .param("offset", "0")
                        .param("limit", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Notifications retrieved successfully"))
                .andExpect(jsonPath("$.data.totalItems").value(2))
                .andExpect(jsonPath("$.data.notifications.length()").value(2));

        // Verify service method was called
        verify(notificationService).getNotificationsForStudent(eq(1L), eq(0), eq(10));
    }

    @Test
    @DisplayName("testGetNotificationsForStudentInvalidInput")
    void testGetNotificationsForStudentInvalidInput() throws Exception {
        // Mock service call with exception
        when(notificationService.getNotificationsForStudent(eq(-1L), eq(0), eq(10)))
                .thenThrow(new IllegalArgumentException("Student ID cannot be negative"));

        when(exceptionHandlingService.handleException(any(IllegalArgumentException.class)))
                .thenReturn("Notification type IDs cannot be empty");

        // Perform GET request with invalid student ID
        mockMvc.perform(get("/notifications/student/-1")
                        .param("offset", "0")
                        .param("limit", "10"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("Student ID cannot be negative"));

        // Verify service method and exception handling were called
        verify(notificationService).getNotificationsForStudent(eq(-1L), eq(0), eq(10));
        verify(exceptionHandlingService).handleException(any(IllegalArgumentException.class));
    }

    @Test
    @DisplayName("testMarkNotificationAsRead")
    void testMarkNotificationAsRead() throws Exception {
        // Mock service call
        doNothing().when(notificationService).markNotificationAsRead(eq(1L), eq(1L));

        // Perform POST request
        mockMvc.perform(post("/notifications/mark-read")
                        .param("notificationId", "1")
                        .param("studentId", "1")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Notification marked as read"));

        // Verify service method was called
        verify(notificationService).markNotificationAsRead(eq(1L), eq(1L));
    }

    @Test
    @DisplayName("testMarkNotificationAsReadException")
    void testMarkNotificationAsReadException() throws Exception {
        // Mock service call with exception
        doThrow(new RuntimeException("Failed to mark notification as read"))
                .when(notificationService).markNotificationAsRead(eq(999L), eq(1L));

        // Perform POST request
        mockMvc.perform(post("/notifications/mark-read")
                        .param("notificationId", "999")
                        .param("studentId", "1")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$").value("Failed to mark notification as read"));

        // Verify service method was called
        verify(notificationService).markNotificationAsRead(eq(999L), eq(1L));
    }

    @Test
    @DisplayName("testSendCourseNotificationInvalidNotificationTypeIds")
    void testSendCourseNotificationInvalidNotificationTypeIds() throws Exception {
        // Invalid notificationTypeIds format
        Long facultyId = 1L;
        Long courseId = 1L;
        String message = "Class is rescheduled";
        String notificationTypeIds = "1,abc"; // Contains non-numeric value

        when(exceptionHandlingService.handleException(any(IllegalArgumentException.class)))
                .thenReturn("Notification type IDs cannot be empty");

        // Perform POST request with invalid notificationTypeIds
        mockMvc.perform(post("/notifications/send-notification")
                        .param("facultyId", facultyId.toString())
                        .param("courseId", courseId.toString())
                        .param("message", message)
                        .param("notificationTypeIds", notificationTypeIds)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andDo(print())
                .andExpect(status().isBadRequest());

        // Verify exception handling was called
        verify(exceptionHandlingService).handleException(any(Exception.class));
    }
}