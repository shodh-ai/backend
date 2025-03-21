package com.shodhAI.ShodhAI.Controller;

import com.shodhAI.ShodhAI.Entity.Notification;
import com.shodhAI.ShodhAI.Service.ExceptionHandlingService;
import com.shodhAI.ShodhAI.Service.NotificationService;
import com.shodhAI.ShodhAI.Service.ResponseService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;
    @Autowired
    private ExceptionHandlingService exceptionHandlingService;

    @PostMapping(value = "/send-notification")
    public ResponseEntity<?> sendCourseNotification(
            @RequestParam Long facultyId,
            @RequestParam Long courseId,
            @RequestParam(required = false) String title,
            @RequestParam String message,
            @RequestParam String notificationTypeIds) {

        try {
            if (facultyId == null) {
                throw new IllegalArgumentException("Faculty ID cannot be null");
            }
            if (courseId == null) {
                throw new IllegalArgumentException("Course ID cannot be null");
            }
            if (notificationTypeIds == null || notificationTypeIds.trim().isEmpty()) {
                throw new IllegalArgumentException("Notification type IDs cannot be empty");
            }

            List<Long> notificationTypeIdList = Arrays.stream(notificationTypeIds.split(","))
                    .map(String::trim)
                    .map(Long::parseLong)
                    .collect(Collectors.toList());

            // Create notification with multiple notification types
            Notification notification = notificationService.createNotification(
                    title, message, facultyId, notificationTypeIdList, new Date()
            );

            // Send to course students
            notificationService.sendNotificationToCourse(courseId, notification);

            // Refresh notification with recipients (if needed)
            notification = notificationService.getNotificationWithRecipients(notification.getId());

            return ResponseService.generateSuccessResponse("Notification sent successfully",notification,HttpStatus.OK);
        }
        catch (IllegalArgumentException illegalArgumentException)
        {
            exceptionHandlingService.handleException(illegalArgumentException);
            return ResponseService.generateErrorResponse(illegalArgumentException.getMessage(),HttpStatus.BAD_REQUEST);
        }
        catch (Exception e) {
            exceptionHandlingService.handleException(e);
            return ResponseService.generateErrorResponse(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/faculty/{facultyId}")
    public ResponseEntity<?> getNotificationsByFaculty(
            @PathVariable Long facultyId,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int limit) {
        try {
            Map<String, Object> response = notificationService.getNotificationsBySender(facultyId, offset, limit);
            return ResponseService.generateSuccessResponse("Notifications retrieved successfully", response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            exceptionHandlingService.handleException(e);
            return ResponseService.generateErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            exceptionHandlingService.handleException(e);
            return ResponseService.generateErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<?> getNotificationsForStudent(
            @PathVariable Long studentId,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int limit) {
        try {
            Map<String, Object> response = notificationService.getNotificationsForStudent(studentId, offset, limit);
            return ResponseService.generateSuccessResponse("Notifications retrieved successfully", response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            exceptionHandlingService.handleException(e);
            return ResponseService.generateErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            exceptionHandlingService.handleException(e);
            return ResponseService.generateErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/mark-read")
    public ResponseEntity<?> markNotificationAsRead(
            @RequestParam Long notificationId,
            @RequestParam Long studentId) {

        try {
            notificationService.markNotificationAsRead(notificationId, studentId);
            return ResponseEntity.ok("Notification marked as read");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}