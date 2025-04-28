package com.shodhAI.ShodhAI.entity;

import com.shodhAI.ShodhAI.Entity.Role;
import com.shodhAI.ShodhAI.Entity.Topic;
import com.shodhAI.ShodhAI.Entity.UserSubComponentProgress;
import com.shodhAI.ShodhAI.Entity.UserSubTopicProgress;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class  UserSubComponentProgressTest {
    private Long userSubComponentProgressId;
    private Role role;
    private Topic topic;
    private String subComponentName;
    private UserSubTopicProgress userSubTopicProgress;
    private Boolean isCompleted;
    private Date createdDate;
    private Date updatedDate;

    @BeforeEach
    void setUp() {
        userSubComponentProgressId = 1L;
        role = new Role();
        topic = new Topic();
        isCompleted = true;
        createdDate= new Date();
        subComponentName= "sub component name";
        userSubTopicProgress= new UserSubTopicProgress();
        updatedDate= new Date();
    }

    @Test
    @DisplayName("testUserSubComponentProgressConstructor")
    void testUserSubComponentProgressConstructor(){
        UserSubComponentProgress userSubComponentProgressByConstructor =  UserSubComponentProgress.builder().id(userSubComponentProgressId).role(role).subTopic(topic).createdDate(createdDate).updatedDate(updatedDate).isCompleted(isCompleted).subComponentName(subComponentName).userSubTopicProgress(userSubTopicProgress).build();

        assertEquals(userSubComponentProgressId, userSubComponentProgressByConstructor.getId());
        assertEquals(role, userSubComponentProgressByConstructor.getRole());
        assertEquals(topic, userSubComponentProgressByConstructor.getSubTopic());
        assertEquals(isCompleted, userSubComponentProgressByConstructor.isCompleted());
        assertEquals(subComponentName, userSubComponentProgressByConstructor.getSubComponentName());
        assertEquals(userSubTopicProgress, userSubComponentProgressByConstructor.getUserSubTopicProgress());
        assertEquals(createdDate, userSubComponentProgressByConstructor.getCreatedDate());
        assertEquals(updatedDate, userSubComponentProgressByConstructor.getUpdatedDate());
    }

    @Test
    @DisplayName("testUserSubComponentProgressSettersAndGetters")
    void testUserSubComponentProgressSettersAndGetters(){
        UserSubComponentProgress userSubComponentProgress = getUserSubComponentProgress();
        assertEquals(userSubComponentProgressId, userSubComponentProgress.getId());
        assertEquals(this.role, userSubComponentProgress.getRole());
        assertEquals(topic, userSubComponentProgress.getSubTopic());
        assertEquals(subComponentName, userSubComponentProgress.getSubComponentName());
        assertEquals(userSubTopicProgress, userSubComponentProgress.getUserSubTopicProgress());
        assertEquals(isCompleted, userSubComponentProgress.isCompleted());
        assertEquals(createdDate, userSubComponentProgress.getCreatedDate());
        assertEquals(updatedDate, userSubComponentProgress.getUpdatedDate());
    }

    private @NotNull UserSubComponentProgress getUserSubComponentProgress() {
        UserSubComponentProgress userSubComponentProgress = new UserSubComponentProgress();
        userSubComponentProgress.setId(userSubComponentProgressId);
        userSubComponentProgress.setRole(this.role);
        userSubComponentProgress.setSubTopic(topic);
        userSubComponentProgress.setSubComponentName(subComponentName);
        userSubComponentProgress.setUserSubTopicProgress(userSubTopicProgress);
        userSubComponentProgress.setCompleted(isCompleted);
        userSubComponentProgress.setCreatedDate(createdDate);
        userSubComponentProgress.setUpdatedDate(updatedDate);
        return userSubComponentProgress;
    }
}

