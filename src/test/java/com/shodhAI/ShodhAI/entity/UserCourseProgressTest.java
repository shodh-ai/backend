package com.shodhAI.ShodhAI.entity;

import com.shodhAI.ShodhAI.Entity.Course;
import com.shodhAI.ShodhAI.Entity.Role;
import com.shodhAI.ShodhAI.Entity.UserCourseProgress;
import com.shodhAI.ShodhAI.Entity.UserModuleProgress;
import com.shodhAI.ShodhAI.Entity.UserSemesterProgress;
import org.checkerframework.checker.units.qual.C;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class  UserCourseProgressTest {
    private Long userCourseProgressId;
    private Role role;
    private Course course;
    private List<UserModuleProgress> moduleProgress;
    private Boolean isCompleted;
    private UserSemesterProgress userSemesterProgress;
    private Date createdDate;
    private Date updatedDate;

    @BeforeEach
    void setUp() {
        userCourseProgressId = 1L;
        role = new Role();
        course = new Course();
        isCompleted = true;
        createdDate= new Date();
        updatedDate= new Date();
        userSemesterProgress= new UserSemesterProgress();
    }

    @Test
    @DisplayName("testUserCourseProgressConstructor")
    void testUserCourseProgressConstructor(){
        UserCourseProgress userCourseProgressByConstructor =  UserCourseProgress.builder().id(userCourseProgressId).role(role).course(course).createdDate(createdDate).updatedDate(updatedDate).isCompleted(isCompleted).moduleProgress(moduleProgress).userSemesterProgress(userSemesterProgress).build();

        assertEquals(userCourseProgressId, userCourseProgressByConstructor.getId());
        assertEquals(role, userCourseProgressByConstructor.getRole());
        assertEquals(course, userCourseProgressByConstructor.getCourse());
        assertEquals(isCompleted, userCourseProgressByConstructor.isCompleted());
        assertEquals(userSemesterProgress, userCourseProgressByConstructor.getUserSemesterProgress());
        assertEquals(moduleProgress, userCourseProgressByConstructor.getModuleProgress());
        assertEquals(createdDate, userCourseProgressByConstructor.getCreatedDate());
        assertEquals(updatedDate, userCourseProgressByConstructor.getUpdatedDate());
    }

    @Test
    @DisplayName("testUserCourseProgressSettersAndGetters")
    void testUserCourseProgressSettersAndGetters(){
        UserCourseProgress userCourseProgress = getUserCourseProgress();
        assertEquals(userCourseProgressId, userCourseProgress.getId());
        assertEquals(this.role, userCourseProgress.getRole());
        assertEquals(course, userCourseProgress.getCourse());
        assertEquals(isCompleted, userCourseProgress.isCompleted());
        assertEquals(moduleProgress, userCourseProgress.getModuleProgress());
        assertEquals(userSemesterProgress, userCourseProgress.getUserSemesterProgress());
        assertEquals(createdDate, userCourseProgress.getCreatedDate());
        assertEquals(updatedDate, userCourseProgress.getUpdatedDate());
    }

    private @NotNull UserCourseProgress getUserCourseProgress() {
        UserCourseProgress userCourseProgress = new UserCourseProgress();
        userCourseProgress.setId(userCourseProgressId);
        userCourseProgress.setRole(this.role);
        userCourseProgress.setCourse(course);
        userCourseProgress.setUserSemesterProgress(userSemesterProgress);
        userCourseProgress.setCompleted(isCompleted);
        userCourseProgress.setCreatedDate(createdDate);
        userCourseProgress.setUpdatedDate(updatedDate);
        return userCourseProgress;
    }
}

