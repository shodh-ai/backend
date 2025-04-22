package com.shodhAI.ShodhAI.entity;

import com.shodhAI.ShodhAI.Entity.NotificationType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class NotificationTypeTest {
    private Long notificationTypeId;
    private String notificationTypeName;
    private Character archived;
    private Date createdDate;
    private Date updatedDate;

    @BeforeEach
    void setUp() {
        notificationTypeId = 1L;
        notificationTypeName = "notificationTypeName";
        archived = 'N';
        createdDate= new Date();
        updatedDate= new Date();
    }

    @Test
    @DisplayName("testNotificationTypeConstructor")
    void testNotificationTypeConstructor(){
        NotificationType notificationTypeByConstructor =  NotificationType.builder().id(notificationTypeId).typeName(notificationTypeName).archived(archived).build();

        assertEquals(notificationTypeId, notificationTypeByConstructor.getId());
        assertEquals(notificationTypeName, notificationTypeByConstructor.getTypeName());
        assertEquals(archived, notificationTypeByConstructor.getArchived());
    }

    @Test
    @DisplayName("testNotificationTypeSettersAndGetters")
    void testNotificationTypeSettersAndGetters(){
        NotificationType notificationType = new NotificationType();
        notificationType.setId(notificationTypeId);
        notificationType.setTypeName(notificationTypeName);
        notificationType.setArchived(archived);
        assertEquals(notificationTypeId, notificationType.getId());
        assertEquals(notificationTypeName, notificationType.getTypeName());
        assertEquals(archived, notificationType.getArchived());
    }
}




