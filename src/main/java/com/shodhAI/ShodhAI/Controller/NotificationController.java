package com.shodhAI.ShodhAI.Controller;

import com.shodhAI.ShodhAI.Entity.Notification;
import com.shodhAI.ShodhAI.Service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

    @PostMapping("/send-notification")
    public ResponseEntity<?> sendCourseNotification(
            @RequestParam Long facultyId,
            @RequestParam Long courseId,
            @RequestParam String title,
            @RequestParam String message,
            @RequestParam List<Long> notificationTypeIds) {

        try {
            // Create notification with multiple notification types
            Notification notification = notificationService.createNotification(
                    title, message, facultyId, notificationTypeIds, new Date()
            );

            // Send to course students
            notificationService.sendNotificationToCourse(courseId, notification);

            // Refresh notification with recipients (if needed)
            notification = notificationService.getNotificationWithRecipients(notification.getId());

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Notification sent successfully");
            response.put("notification", notification);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/send-quick-notification")
    public ResponseEntity<?> sendQuickNotification(
            @RequestParam Long facultyId,
            @RequestParam Long courseId,
            @RequestParam String notificationText) {

        try {
            // Parse the notification text
            String title = "Course Announcement";
            String message = notificationText;

            // Find the ALL notification type (assuming it has ID 4)
            List<Long> notificationTypeIds = List.of(4L);

            // Create notification with ALL channel
            Notification notification = notificationService.createNotification(
                    title, message, facultyId, notificationTypeIds, new Date()
            );

            // Send to course students
            notificationService.sendNotificationToCourse(courseId, notification);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Quick notification sent successfully");
            response.put("notification", notification);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/send-to-students")
    public ResponseEntity<?> sendNotificationToStudents(
            @RequestParam Long facultyId,
            @RequestParam List<Long> studentIds,
            @RequestParam String title,
            @RequestParam String message,
            @RequestParam List<Long> notificationTypeIds) {

        try {
            // Create notification with multiple notification types
            Notification notification = notificationService.createNotification(
                    title, message, facultyId, notificationTypeIds, new Date()
            );

            // Send to specific students
            notificationService.sendNotificationToStudents(studentIds, notification);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Notification sent successfully to " + studentIds.size() + " students");
            response.put("notification", notification);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/send-course-announcement")
    public ResponseEntity<?> sendCourseAnnouncement(
            @RequestParam Long facultyId,
            @RequestParam Long courseId,
            @RequestParam String title,
            @RequestParam String message,
            @RequestParam List<Long> notificationTypeIds) {

        try {
            // Create notification with multiple notification types
            Notification notification = notificationService.createNotification(
                    title, message, facultyId, notificationTypeIds, new Date()
            );

            // Send as course announcement
            notificationService.sendCourseAnnouncement(courseId, notification);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Course announcement sent successfully");
            response.put("notification", notification);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/faculty/{facultyId}")
    public ResponseEntity<?> getNotificationsByFaculty(@PathVariable Long facultyId) {
        try {
            List<Notification> notifications = notificationService.getNotificationsBySender(facultyId);
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<?> getNotificationsForStudent(@PathVariable Long studentId) {
        try {
            List<Notification> notifications = notificationService.getNotificationsForStudent(studentId);
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
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