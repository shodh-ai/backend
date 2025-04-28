package com.shodhAI.ShodhAI.entity;

import com.shodhAI.ShodhAI.Entity.Cohort;
import com.shodhAI.ShodhAI.Entity.Course;
import com.shodhAI.ShodhAI.Entity.Student;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CohortTest {
    private Long cohortId;
    private String cohortTitle;
    private String cohortDescription;
    private Character archived;
    private Date createdDate;
    private Date updatedDate;
    private List<Student> students;
    private Course course;

    @BeforeEach
    void setUp() {
        cohortId = 1L;
        cohortTitle = "cohortName";
        cohortDescription = "programName";
        archived = 'N';
        students = new ArrayList<>() {};
        course = new Course();
        createdDate= new Date();
        updatedDate= new Date();
    }

    @Test
    @DisplayName("testCohortConstructor")
    void testCohortConstructor(){
        Cohort cohortByConstructor =  Cohort.builder().cohortId(cohortId).cohortTitle(cohortTitle).cohortDescription(cohortDescription).archived(archived).createdDate(createdDate).updatedDate(updatedDate).students(students).course(course).build();

        assertEquals(cohortId, cohortByConstructor.getCohortId());
        assertEquals(cohortTitle, cohortByConstructor.getCohortTitle());
        assertEquals(cohortDescription, cohortByConstructor.getCohortDescription());
        assertEquals(archived, cohortByConstructor.getArchived());
        assertEquals(createdDate, cohortByConstructor.getCreatedDate());
        assertEquals(updatedDate, cohortByConstructor.getUpdatedDate());
        assertEquals(course, cohortByConstructor.getCourse());
    }

    @Test
    @DisplayName("testCohortSettersAndGetters")
    void testCohortSettersAndGetters(){
        Cohort cohort = getCohort();
        assertEquals(cohortId, cohort.getCohortId());
        assertEquals(cohortTitle, cohort.getCohortTitle());
        assertEquals(cohortDescription, cohort.getCohortDescription());
        assertEquals(archived, cohort.getArchived());
        assertEquals(students, cohort.getStudents());
        assertEquals(course, cohort.getCourse());
        assertEquals(createdDate, cohort.getCreatedDate());
        assertEquals(updatedDate, cohort.getUpdatedDate());
    }

    private @NotNull Cohort getCohort() {
        Cohort cohort = new Cohort();
        cohort.setCohortId(cohortId);
        cohort.setCohortTitle(cohortTitle);
        cohort.setCohortDescription(cohortDescription);
        cohort.setArchived(archived);
        cohort.setCreatedDate(createdDate);
        cohort.setUpdatedDate(updatedDate);
        cohort.setStudents(students);
        cohort.setCourse(course);
        return cohort;
    }
}

