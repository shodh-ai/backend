package com.shodhAI.ShodhAI.entity;

import com.shodhAI.ShodhAI.Entity.Course;
import com.shodhAI.ShodhAI.Entity.Notification;
import com.shodhAI.ShodhAI.Entity.NotificationRecipient;
import com.shodhAI.ShodhAI.Entity.Faculty;
import com.shodhAI.ShodhAI.Entity.NotificationType;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class NotificationTest {
    private Long notificationId;
    private String notificationTitle;
    private String message;
    private Character archived;
    private Boolean isSent;
    private List<NotificationType> notificationTypes;
    private List<NotificationRecipient> recipients;
    private Faculty sender;
    private Course course;
    private Boolean isCourseAnnouncement;

    @BeforeEach
    void setUp() {
        notificationId = 1L;
        notificationTitle = "notificationName";
        message = "notificationMessage";
        archived = 'N';
        course = new Course();
        sender = new Faculty();
        notificationTypes = new ArrayList<>() {};
        recipients = new ArrayList<>();
        isCourseAnnouncement =true;
        isSent =true;
    }

    @Test
    @DisplayName("testNotificationConstructor")
    void testNotificationConstructor(){
        Notification notificationByConstructor =  Notification.builder().id(notificationId).title(notificationTitle).message(message).archived(archived).isSent(isSent).notificationTypes(notificationTypes).recipients(recipients).course(course).isCourseAnnouncement(isCourseAnnouncement).sender(sender).build();

        assertEquals(notificationId, notificationByConstructor.getId());
        assertEquals(notificationTitle, notificationByConstructor.getTitle());
        assertEquals(message, notificationByConstructor.getMessage());
        assertEquals(archived, notificationByConstructor.getArchived());
        assertEquals(isSent, notificationByConstructor.getIsSent());
        assertEquals(isCourseAnnouncement, notificationByConstructor.getIsCourseAnnouncement());
        assertEquals(course, notificationByConstructor.getCourse());
        assertEquals(sender, notificationByConstructor.getSender());
    }

    @Test
    @DisplayName("testNotificationSettersAndGetters")
    void testNotificationSettersAndGetters(){
        Notification notification = getNotification();
        assertEquals(notificationId, notification.getId());
        assertEquals(notificationTitle, notification.getTitle());
        assertEquals(message, notification.getMessage());
        assertEquals(archived, notification.getArchived());
        assertEquals(course, notification.getCourse());
        assertEquals(sender, notification.getSender());
        assertEquals(recipients, notification.getRecipients());
        assertEquals(notificationTypes, notification.getNotificationTypes());
        assertEquals(isCourseAnnouncement, notification.getIsCourseAnnouncement());
        assertEquals(isSent, notification.getIsSent());
    }

    private @NotNull Notification getNotification() {
        Notification notification = new Notification();
        notification.setId(notificationId);
        notification.setTitle(notificationTitle);
        notification.setMessage(message);
        notification.setArchived(archived);
        notification.setCourse(course);
        notification.setSender(sender);
        notification.setRecipients(recipients);
        notification.setNotificationTypes(notificationTypes);
        notification.setIsCourseAnnouncement(isCourseAnnouncement);
        notification.setIsSent(isSent);
        return notification;
    }
}



