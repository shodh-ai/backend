package com.shodhAI.ShodhAI.entity;

import com.shodhAI.ShodhAI.Entity.Role;
import com.shodhAI.ShodhAI.Entity.Topic;
import com.shodhAI.ShodhAI.Entity.UserModuleProgress;
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

public class  UserTopicProgressTest {
    private Long userTopicProgressId;
    private Role role;
    private Topic topic;
    private UserModuleProgress userModuleProgress;
    private List<UserSubTopicProgress> subTopicProgress;
    private Boolean isCompleted;
    private Date createdDate;
    private Date updatedDate;

    @BeforeEach
    void setUp() {
        userTopicProgressId = 1L;
        role = new Role();
        topic = new Topic();
        isCompleted = true;
        createdDate= new Date();
        userModuleProgress = new UserModuleProgress();
        updatedDate= new Date();
        subTopicProgress = new ArrayList<>();
    }

    @Test
    @DisplayName("testUserTopicProgressConstructor")
    void testUserTopicProgressConstructor(){
        UserTopicProgress userTopicProgressByConstructor =  UserTopicProgress.builder().id(userTopicProgressId).role(role).topic(topic).createdDate(createdDate).updatedDate(updatedDate).isCompleted(isCompleted).userModuleProgress(userModuleProgress).build();

        assertEquals(userTopicProgressId, userTopicProgressByConstructor.getId());
        assertEquals(role, userTopicProgressByConstructor.getRole());
        assertEquals(topic, userTopicProgressByConstructor.getTopic());
        assertEquals(isCompleted, userTopicProgressByConstructor.isCompleted());
        assertEquals(userModuleProgress, userTopicProgressByConstructor.getUserModuleProgress());
        assertEquals(createdDate, userTopicProgressByConstructor.getCreatedDate());
        assertEquals(updatedDate, userTopicProgressByConstructor.getUpdatedDate());
    }

    @Test
    @DisplayName("testUserTopicProgressSettersAndGetters")
    void testUserTopicProgressSettersAndGetters(){
        UserTopicProgress userTopicProgress = getUserTopicProgress();
        assertEquals(userTopicProgressId, userTopicProgress.getId());
        assertEquals(this.role, userTopicProgress.getRole());
        assertEquals(topic, userTopicProgress.getTopic());
        assertEquals(userModuleProgress, userTopicProgress.getUserModuleProgress());
        assertEquals(isCompleted, userTopicProgress.isCompleted());
        assertEquals(createdDate, userTopicProgress.getCreatedDate());
        assertEquals(updatedDate, userTopicProgress.getUpdatedDate());
    }

    private @NotNull UserTopicProgress getUserTopicProgress() {
        UserTopicProgress userTopicProgress = new UserTopicProgress();
        userTopicProgress.setId(userTopicProgressId);
        userTopicProgress.setRole(this.role);
        userTopicProgress.setTopic(topic);
        userTopicProgress.setUserModuleProgress(userModuleProgress);
        userTopicProgress.setCompleted(isCompleted);
        userTopicProgress.setCreatedDate(createdDate);
        userTopicProgress.setUpdatedDate(updatedDate);
        return userTopicProgress;
    }
}

