package com.shodhAI.ShodhAI.Controller;

import com.shodhAI.ShodhAI.Entity.Notification;
import com.shodhAI.ShodhAI.Service.ExceptionHandlingService;
import com.shodhAI.ShodhAI.Service.NotificationService;
import com.shodhAI.ShodhAI.Service.ResponseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;
    @Autowired
    private ExceptionHandlingService exceptionHandlingService;


    @PostMapping(value = "/send-notification" ,consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> sendCourseNotification(
            @RequestParam Long facultyId,
            @RequestParam Long courseId,
            @RequestParam(required = false) String title,
            @RequestParam String message,
            @RequestParam List<Long> notificationTypeIds) {

        try {
            if(facultyId==null )
            {
                throw new IllegalArgumentException("Faculty id cannot be null ");
            }
            if(courseId==null)
            {
                throw new IllegalArgumentException("Course id cannot be null");
            }
            // Create notification with multiple notification types
            Notification notification = notificationService.createNotification(
                    title, message, facultyId, notificationTypeIds, new Date()
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
    public ResponseEntity<?> getNotificationsByFaculty(@PathVariable Long facultyId) {
        try {
            List<Notification> notifications = notificationService.getNotificationsBySender(facultyId);
            return ResponseService.generateSuccessResponse("Notification is retrieved successfully", notifications, HttpStatus.OK);
        }
        catch (IllegalArgumentException illegalArgumentException) {
            exceptionHandlingService.handleException(illegalArgumentException);
            return ResponseService.generateErrorResponse(illegalArgumentException.getMessage(),HttpStatus.BAD_REQUEST);
        }
        catch (Exception e) {
            exceptionHandlingService.handleException(e);
            return ResponseService.generateErrorResponse(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<?> getNotificationsForStudent(@PathVariable Long studentId) {
        try {
            List<Notification> notifications = notificationService.getNotificationsForStudent(studentId);
            return ResponseService.generateSuccessResponse("Notification is retrieved successfully", notifications, HttpStatus.OK);
        } catch (IllegalArgumentException illegalArgumentException) {
            exceptionHandlingService.handleException(illegalArgumentException);
            return ResponseService.generateErrorResponse(illegalArgumentException.getMessage(),HttpStatus.BAD_REQUEST);
        }
        catch (Exception e) {
            exceptionHandlingService.handleException(e);
            return ResponseService.generateErrorResponse(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
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