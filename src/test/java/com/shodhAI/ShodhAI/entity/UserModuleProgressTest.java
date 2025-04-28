package com.shodhAI.ShodhAI.entity;

import com.shodhAI.ShodhAI.Entity.Course;
import com.shodhAI.ShodhAI.Entity.Module;
import com.shodhAI.ShodhAI.Entity.Role;
import com.shodhAI.ShodhAI.Entity.UserModuleProgress;
import com.shodhAI.ShodhAI.Entity.UserCourseProgress;
import com.shodhAI.ShodhAI.Entity.UserTopicProgress;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class  UserModuleProgressTest {
    private Long userModuleProgressId;
    private Role role;
    private Module module;
    private Boolean isCompleted;
    private UserCourseProgress userCourseProgress;
    private Date createdDate;
    private Date updatedDate;
    private List<UserTopicProgress> topicProgress;

    @BeforeEach
    void setUp() {
        userModuleProgressId = 1L;
        role = new Role();
        module = new Module();
        isCompleted = true;
        createdDate= new Date();
        updatedDate= new Date();
        userCourseProgress= new UserCourseProgress();
        topicProgress= new ArrayList<>();
    }

    @Test
    @DisplayName("testUserModuleProgressConstructor")
    void testUserModuleProgressConstructor(){
        UserModuleProgress userModuleProgressByConstructor =  UserModuleProgress.builder().id(userModuleProgressId).role(role).module(module).createdDate(createdDate).updatedDate(updatedDate).isCompleted(isCompleted).userCourseProgress(userCourseProgress).topicProgress(topicProgress).build();

        assertEquals(userModuleProgressId, userModuleProgressByConstructor.getId());
        assertEquals(role, userModuleProgressByConstructor.getRole());
        assertEquals(module, userModuleProgressByConstructor.getModule());
        assertEquals(isCompleted, userModuleProgressByConstructor.isCompleted());
        assertEquals(userCourseProgress, userModuleProgressByConstructor.getUserCourseProgress());
        assertEquals(topicProgress, userModuleProgressByConstructor.getTopicProgress());
        assertEquals(createdDate, userModuleProgressByConstructor.getCreatedDate());
        assertEquals(updatedDate, userModuleProgressByConstructor.getUpdatedDate());
    }

    @Test
    @DisplayName("testUserModuleProgressSettersAndGetters")
    void testUserModuleProgressSettersAndGetters(){
        UserModuleProgress userModuleProgress = getUserModuleProgress();
        assertEquals(userModuleProgressId, userModuleProgress.getId());
        assertEquals(this.role, userModuleProgress.getRole());
        assertEquals(module, userModuleProgress.getModule());
        assertEquals(isCompleted, userModuleProgress.isCompleted());
        assertEquals(userCourseProgress, userModuleProgress.getUserCourseProgress());
        assertEquals(createdDate, userModuleProgress.getCreatedDate());
        assertEquals(updatedDate, userModuleProgress.getUpdatedDate());
    }

    private @NotNull UserModuleProgress getUserModuleProgress() {
        UserModuleProgress userModuleProgress = new UserModuleProgress();
        userModuleProgress.setId(userModuleProgressId);
        userModuleProgress.setRole(this.role);
        userModuleProgress.setModule(module);
        userModuleProgress.setModule(module);
        userModuleProgress.setCompleted(isCompleted);
        userModuleProgress.setCreatedDate(createdDate);
        userModuleProgress.setUserCourseProgress(userCourseProgress);
        userModuleProgress.setUpdatedDate(updatedDate);
        return userModuleProgress;
    }
}

