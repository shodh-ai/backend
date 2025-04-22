package com.shodhAI.ShodhAI.entity;

import com.shodhAI.ShodhAI.Entity.AcademicDegree;
import com.shodhAI.ShodhAI.Entity.Course;
import com.shodhAI.ShodhAI.Entity.CourseSemesterDegree;
import com.shodhAI.ShodhAI.Entity.Institute;
import com.shodhAI.ShodhAI.Entity.Semester;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AcademicDegreeTest {
    private Long degreeId;
    private String degreeName;
    private String programName;
    private Character archived;
    private Date createdDate;
    private Date updatedDate;
    private List<Semester> semesters;
    private List<CourseSemesterDegree> courseSemesterDegrees;
    private List<Institute> institutes;
    private List<Course> courses;

    @BeforeEach
    void setUp() {
        degreeId = 1L;
        degreeName = "degreeName";
        programName = "programName";
        archived = 'N';
        semesters = new ArrayList<>() {};
        courseSemesterDegrees = new ArrayList<>();
        institutes = new ArrayList<>();
        courses = new ArrayList<>();
        createdDate= new Date();
        updatedDate= new Date();
    }

    @Test
    @DisplayName("testAcademicDegreeConstructor")
    void testAcademicDegreeConstructor(){
        AcademicDegree academicDegreeByConstructor =  AcademicDegree.builder().degreeId(degreeId).degreeName(degreeName).programName(programName).archived(archived).createdDate(createdDate).updatedDate(updatedDate).semesters(semesters).courseSemesterDegrees(courseSemesterDegrees).institutes(institutes).courses(courses).build();

        assertEquals(degreeId, academicDegreeByConstructor.getDegreeId());
        assertEquals(degreeName, academicDegreeByConstructor.getDegreeName());
        assertEquals(programName, academicDegreeByConstructor.getProgramName());
        assertEquals(archived, academicDegreeByConstructor.getArchived());
        assertEquals(createdDate, academicDegreeByConstructor.getCreatedDate());
        assertEquals(updatedDate, academicDegreeByConstructor.getUpdatedDate());
    }

    @Test
    @DisplayName("testAcademicDegreeSettersAndGetters")
    void testAcademicDegreeSettersAndGetters(){
        AcademicDegree academicDegree = getAcademicDegree();
        assertEquals(degreeId, academicDegree.getDegreeId());
        assertEquals(degreeName, academicDegree.getDegreeName());
        assertEquals(programName, academicDegree.getProgramName());
        assertEquals(archived, academicDegree.getArchived());
        assertEquals(courseSemesterDegrees, academicDegree.getCourseSemesterDegrees());
        assertEquals(institutes, academicDegree.getInstitutes());
        assertEquals(semesters, academicDegree.getSemesters());
        assertEquals(courses, academicDegree.getCourses());
        assertEquals(createdDate, academicDegree.getCreatedDate());
        assertEquals(updatedDate, academicDegree.getUpdatedDate());
    }

    private @NotNull AcademicDegree getAcademicDegree() {
        AcademicDegree academicDegree = new AcademicDegree();
        academicDegree.setDegreeId(degreeId);
        academicDegree.setDegreeName(degreeName);
        academicDegree.setProgramName(programName);
        academicDegree.setArchived(archived);
        academicDegree.setCourseSemesterDegrees(courseSemesterDegrees);
        academicDegree.setInstitutes(institutes);
        academicDegree.setSemesters(semesters);
        academicDegree.setCourses(courses);
        academicDegree.setCreatedDate(createdDate);
        academicDegree.setUpdatedDate(updatedDate);
        return academicDegree;
    }
}

