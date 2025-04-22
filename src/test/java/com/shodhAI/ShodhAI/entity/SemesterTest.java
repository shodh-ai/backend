package com.shodhAI.ShodhAI.entity;

import com.shodhAI.ShodhAI.Entity.AcademicDegree;
import com.shodhAI.ShodhAI.Entity.CourseSemesterDegree;
import com.shodhAI.ShodhAI.Entity.Semester;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SemesterTest {
    private Long semesterId;
    private String semesterName;
    private Character archived;
    private Date startDate;
    private Date endDate;
    private List<CourseSemesterDegree> courseSemesterDegrees;
    private List<AcademicDegree> academicDegrees;


    @BeforeEach
    void setUp() {
        semesterId = 1L;
        semesterName = "semesterName";
        archived = 'N';
        courseSemesterDegrees = new ArrayList<>();
        academicDegrees = new ArrayList<>();
        startDate= new Date();
        endDate= new Date();
    }

    @Test
    @DisplayName("testSemesterConstructor")
    void testSemesterConstructor(){
        Semester semesterByConstructor =  Semester.builder().semesterId(semesterId).semesterName(semesterName).archived(archived).startDate(startDate).endDate(endDate).courseSemesterDegrees(courseSemesterDegrees).academicDegrees(academicDegrees).build();

        assertEquals(semesterId, semesterByConstructor.getSemesterId());
        assertEquals(semesterName, semesterByConstructor.getSemesterName());
        assertEquals(archived, semesterByConstructor.getArchived());
        assertEquals(startDate, semesterByConstructor.getStartDate());
        assertEquals(endDate, semesterByConstructor.getEndDate());
    }

    @Test
    @DisplayName("testSemesterSettersAndGetters")
    void testSemesterSettersAndGetters(){
        Semester semester = getSemester();
        assertEquals(semesterId, semester.getSemesterId());
        assertEquals(semesterName, semester.getSemesterName());
        assertEquals(archived, semester.getArchived());
        assertEquals(courseSemesterDegrees, semester.getCourseSemesterDegrees());
        assertEquals(academicDegrees, semester.getAcademicDegrees());
        assertEquals(startDate, semester.getStartDate());
        assertEquals(endDate, semester.getEndDate());
    }

    private @NotNull Semester getSemester() {
        Semester semester = new Semester();
        semester.setSemesterId(semesterId);
        semester.setSemesterName(semesterName);
        semester.setArchived(archived);
        semester.setCourseSemesterDegrees(courseSemesterDegrees);
        semester.setAcademicDegrees(academicDegrees);
        semester.setStartDate(startDate);
        semester.setEndDate(endDate);
        return semester;
    }
}


