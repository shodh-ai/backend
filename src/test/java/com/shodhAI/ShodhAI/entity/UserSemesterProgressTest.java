package com.shodhAI.ShodhAI.entity;

import com.shodhAI.ShodhAI.Entity.Role;
import com.shodhAI.ShodhAI.Entity.Semester;
import com.shodhAI.ShodhAI.Entity.UserSemesterProgress;
import com.shodhAI.ShodhAI.Entity.UserCourseProgress;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class  UserSemesterProgressTest {
    private Long userSemesterProgressId;
    private Role role;
    private Semester semester;
    private Boolean isCompleted;
    private Date createdDate;
    private Date updatedDate;
    private List<UserCourseProgress> courseProgress;

    @BeforeEach
    void setUp() {
        userSemesterProgressId = 1L;
        role = new Role();
        semester = new Semester();
        isCompleted = true;
        createdDate= new Date();
        updatedDate= new Date();
        courseProgress = new ArrayList<>();
    }

    @Test
    @DisplayName("testUserSemesterProgressConstructor")
    void testUserSemesterProgressConstructor(){
        UserSemesterProgress userSemesterProgressByConstructor =  UserSemesterProgress.builder().id(userSemesterProgressId).role(role).semester(semester).createdDate(createdDate).updatedDate(updatedDate).isCompleted(isCompleted).courseProgress(courseProgress).build();

        assertEquals(userSemesterProgressId, userSemesterProgressByConstructor.getId());
        assertEquals(role, userSemesterProgressByConstructor.getRole());
        assertEquals(semester, userSemesterProgressByConstructor.getSemester());
        assertEquals(isCompleted, userSemesterProgressByConstructor.isCompleted());
        assertEquals(courseProgress, userSemesterProgressByConstructor.getCourseProgress());
        assertEquals(createdDate, userSemesterProgressByConstructor.getCreatedDate());
        assertEquals(updatedDate, userSemesterProgressByConstructor.getUpdatedDate());
    }

    @Test
    @DisplayName("testUserSemesterProgressSettersAndGetters")
    void testUserSemesterProgressSettersAndGetters(){
        UserSemesterProgress userSemesterProgress = getUserSemesterProgress();
        assertEquals(userSemesterProgressId, userSemesterProgress.getId());
        assertEquals(this.role, userSemesterProgress.getRole());
        assertEquals(semester, userSemesterProgress.getSemester());
        assertEquals(isCompleted, userSemesterProgress.isCompleted());
        assertEquals(createdDate, userSemesterProgress.getCreatedDate());
        assertEquals(updatedDate, userSemesterProgress.getUpdatedDate());
    }

    private @NotNull UserSemesterProgress getUserSemesterProgress() {
        UserSemesterProgress userSemesterProgress = new UserSemesterProgress();
        userSemesterProgress.setId(userSemesterProgressId);
        userSemesterProgress.setRole(this.role);
        userSemesterProgress.setSemester(semester);
        userSemesterProgress.setCompleted(isCompleted);
        userSemesterProgress.setCreatedDate(createdDate);
        userSemesterProgress.setUpdatedDate(updatedDate);
        return userSemesterProgress;
    }
}

