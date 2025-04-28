package com.shodhAI.ShodhAI.entity;

import com.shodhAI.ShodhAI.Entity.Notification;
import com.shodhAI.ShodhAI.Entity.NotificationRecipient;
import com.shodhAI.ShodhAI.Entity.Student;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NotificationRecipientTest {
    private Long notificationRecipientId;
    private Character archived;
    private Date readDate;
    private Boolean readStatus;
    private Student recipient;
    private Notification notification;

    @BeforeEach
    void setUp() {
        notificationRecipientId = 1L;
        recipient = new Student();
        notification = new Notification();
        archived = 'N';
        readDate = new Date();
        readStatus =true;
    }

    @Test
    @DisplayName("testNotificationRecipientConstructor")
    void testNotificationRecipientConstructor(){
        NotificationRecipient notificationRecipientByConstructor =  NotificationRecipient.builder().id(notificationRecipientId).recipient(recipient).notification(notification).archived(archived).readDate(readDate).readStatus(readStatus).build();

        assertEquals(notificationRecipientId, notificationRecipientByConstructor.getId());
        assertEquals(notification, notificationRecipientByConstructor.getNotification());
        assertEquals(recipient, notificationRecipientByConstructor.getRecipient());
        assertEquals(archived, notificationRecipientByConstructor.getArchived());
        assertEquals(readDate, notificationRecipientByConstructor.getReadDate());
        assertEquals(readStatus, notificationRecipientByConstructor.getReadStatus());
    }

    @Test
    @DisplayName("testNotificationRecipientSettersAndGetters")
    void testNotificationRecipientSettersAndGetters(){
        NotificationRecipient notificationRecipient = getNotificationRecipient();
        assertEquals(notificationRecipientId, notificationRecipient.getId());
        assertEquals(archived, notificationRecipient.getArchived());
        assertEquals(recipient, notificationRecipient.getRecipient());
        assertEquals(notification, notificationRecipient.getNotification());
        assertEquals(readDate, notificationRecipient.getReadDate());
        assertEquals(readStatus, notificationRecipient.getReadStatus());
    }

    private @NotNull NotificationRecipient getNotificationRecipient() {
        NotificationRecipient notificationRecipient = new NotificationRecipient();
        notificationRecipient.setId(notificationRecipientId);
        notificationRecipient.setArchived(archived);
        notificationRecipient.setRecipient(recipient);
        notificationRecipient.setNotification(notification);
        notificationRecipient.setReadDate(readDate);
        notificationRecipient.setReadStatus(readStatus);
        return notificationRecipient;
    }
}

