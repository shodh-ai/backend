package com.shodhAI.ShodhAI.entity;

import com.shodhAI.ShodhAI.Entity.Role;
import com.shodhAI.ShodhAI.Entity.Topic;
import com.shodhAI.ShodhAI.Entity.UserSubComponentProgress;
import com.shodhAI.ShodhAI.Entity.UserSubTopicProgress;
import com.shodhAI.ShodhAI.Entity.UserTopicProgress;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class  UserSubTopicProgressTest {
    private Long userSubTopicProgressId;
    private Role role;
    private Topic topic;
    private UserTopicProgress userTopicProgress;
    private List<UserSubComponentProgress> subComponentProgress;
    private Boolean isCompleted;
    private Date createdDate;
    private Date updatedDate;

    @BeforeEach
    void setUp() {
        userSubTopicProgressId = 1L;
        role = new Role();
        topic = new Topic();
        isCompleted = true;
        createdDate= new Date();
        userTopicProgress = new UserTopicProgress();
        updatedDate= new Date();
        subComponentProgress= new ArrayList<>();
    }

    @Test
    @DisplayName("testUserSubTopicProgressConstructor")
    void testUserSubTopicProgressConstructor(){
        UserSubTopicProgress userSubTopicProgressByConstructor =  UserSubTopicProgress.builder().id(userSubTopicProgressId).role(role).subTopic(topic).createdDate(createdDate).updatedDate(updatedDate).isCompleted(isCompleted).userTopicProgress(userTopicProgress).build();

        assertEquals(userSubTopicProgressId, userSubTopicProgressByConstructor.getId());
        assertEquals(role, userSubTopicProgressByConstructor.getRole());
        assertEquals(topic, userSubTopicProgressByConstructor.getSubTopic());
        assertEquals(isCompleted, userSubTopicProgressByConstructor.isCompleted());
        assertEquals(userTopicProgress, userSubTopicProgressByConstructor.getUserTopicProgress());
        assertEquals(createdDate, userSubTopicProgressByConstructor.getCreatedDate());
        assertEquals(updatedDate, userSubTopicProgressByConstructor.getUpdatedDate());
    }

    @Test
    @DisplayName("testUserSubTopicProgressSettersAndGetters")
    void testUserSubTopicProgressSettersAndGetters(){
        UserSubTopicProgress userSubTopicProgress = getUserSubTopicProgress();
        assertEquals(userSubTopicProgressId, userSubTopicProgress.getId());
        assertEquals(this.role, userSubTopicProgress.getRole());
        assertEquals(topic, userSubTopicProgress.getSubTopic());
        assertEquals(userTopicProgress, userSubTopicProgress.getUserTopicProgress());
        assertEquals(isCompleted, userSubTopicProgress.isCompleted());
        assertEquals(createdDate, userSubTopicProgress.getCreatedDate());
        assertEquals(updatedDate, userSubTopicProgress.getUpdatedDate());
    }

    private @NotNull UserSubTopicProgress getUserSubTopicProgress() {
        UserSubTopicProgress userSubTopicProgress = new UserSubTopicProgress();
        userSubTopicProgress.setId(userSubTopicProgressId);
        userSubTopicProgress.setRole(this.role);
        userSubTopicProgress.setSubTopic(topic);
        userSubTopicProgress.setUserTopicProgress(userTopicProgress);
        userSubTopicProgress.setCompleted(isCompleted);
        userSubTopicProgress.setCreatedDate(createdDate);
        userSubTopicProgress.setUpdatedDate(updatedDate);
        return userSubTopicProgress;
    }
}

