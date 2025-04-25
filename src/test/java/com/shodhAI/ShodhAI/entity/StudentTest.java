package com.shodhAI.ShodhAI.entity;
import com.shodhAI.ShodhAI.Entity.AcademicDegree;
import com.shodhAI.ShodhAI.Entity.Accuracy;
import com.shodhAI.ShodhAI.Entity.Course;
import com.shodhAI.ShodhAI.Entity.CriticalThinking;
import com.shodhAI.ShodhAI.Entity.Faculty;
import com.shodhAI.ShodhAI.Entity.Memory;
import com.shodhAI.ShodhAI.Entity.Student;
import com.shodhAI.ShodhAI.Entity.TimeSpent;
import com.shodhAI.ShodhAI.Entity.Understanding;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class StudentTest {
    private Long studentId;
    private String firstName;
    private String lastName;
    private String collegeEmail;
    private String personalEmail;
    private String profilePictureUrl;
    private String password;
    private String userName;
    private Date dateOfBirth;
    private Character archived;
    private Date createdDate;
    private Date updatedDate;
    private List<Faculty> faculties;
    private List<Course> courses;
    private AcademicDegree academicDegree;
    private Double marksObtained;
    private Double totalMarks;
    private Double marksImprovement;
    private Accuracy accuracy;
    private CriticalThinking criticalThinking;
    private Understanding understanding;
    private Memory memory;
    private TimeSpent timeSpent;


    @BeforeEach
    void setUp() {
        studentId = 1L;
        firstName = "studentName";
        lastName = "studentLastName";
        collegeEmail = "simran@gmail.com";
        archived = 'N';
        personalEmail= "simran12@gmail.com";
        password= "123456";
        profilePictureUrl= "profileUrl";
        userName= "simran19";
        dateOfBirth= new Date();
        faculties = new ArrayList<>();
        courses= new ArrayList<>();
        createdDate= new Date();
        updatedDate= new Date();
        marksObtained= 70D;
        totalMarks= 100D;
        marksImprovement= 20D;
        academicDegree= new AcademicDegree();
        accuracy= new Accuracy();
        criticalThinking= new CriticalThinking();
        understanding= new Understanding();
        memory= new Memory();
        timeSpent= new TimeSpent();
    }

    @Test
    @DisplayName("testStudentConstructor")
    void testStudentConstructor(){
        Student studentByConstructor =  Student.builder().id(studentId).firstName(firstName).lastName(lastName).archived(archived).collegeEmail(collegeEmail).personalEmail(personalEmail).dateOfBirth(dateOfBirth).profilePictureUrl(profilePictureUrl).password(password).userName(userName).createdDate(createdDate).updatedDate(updatedDate).facultyMembers(faculties).marksObtained(marksObtained).totalMarks(totalMarks).marksImprovement(marksImprovement).academicDegree(academicDegree).accuracy(accuracy).criticalThinking(criticalThinking).understanding(understanding).memory(memory).timeSpent(timeSpent).build();

        assertEquals(studentId, studentByConstructor.getId());
        assertEquals(firstName, studentByConstructor.getFirstName());
        assertEquals(lastName, studentByConstructor.getLastName());
        assertEquals(collegeEmail, studentByConstructor.getCollegeEmail());
        assertEquals(personalEmail, studentByConstructor.getPersonalEmail());
        assertEquals(password, studentByConstructor.getPassword());
        assertEquals(profilePictureUrl, studentByConstructor.getProfilePictureUrl());
        assertEquals(userName, studentByConstructor.getUserName());
        assertEquals(dateOfBirth, studentByConstructor.getDateOfBirth());
        assertEquals(archived, studentByConstructor.getArchived());
        assertEquals(createdDate, studentByConstructor.getCreatedDate());
        assertEquals(updatedDate, studentByConstructor.getUpdatedDate());
        assertEquals(academicDegree, studentByConstructor.getAcademicDegree());
        assertEquals(accuracy, studentByConstructor.getAccuracy());
        assertEquals(criticalThinking, studentByConstructor.getCriticalThinking());
        assertEquals(understanding, studentByConstructor.getUnderstanding());
        assertEquals(memory, studentByConstructor.getMemory());
        assertEquals(timeSpent, studentByConstructor.getTimeSpent());
    }

    @Test
    @DisplayName("testStudentSettersAndGetters")
    void testStudentSettersAndGetters(){
        Student student = getStudent();
        assertEquals(studentId, student.getId());
        assertEquals(firstName, student.getFirstName());
        assertEquals(lastName, student.getLastName());
        assertEquals(collegeEmail, student.getCollegeEmail());
        assertEquals(personalEmail, student.getPersonalEmail());
        assertEquals(password, student.getPassword());
        assertEquals(profilePictureUrl, student.getProfilePictureUrl());
        assertEquals(userName, student.getUserName());
        assertEquals(dateOfBirth, student.getDateOfBirth());
        assertEquals(archived, student.getArchived());
        assertEquals(courses, student.getCourses());
        assertEquals(createdDate, student.getCreatedDate());
        assertEquals(updatedDate, student.getUpdatedDate());
        assertEquals(academicDegree, student.getAcademicDegree());
        assertEquals(accuracy, student.getAccuracy());
        assertEquals(criticalThinking, student.getCriticalThinking());
        assertEquals(understanding, student.getUnderstanding());
        assertEquals(memory, student.getMemory());
        assertEquals(timeSpent, student.getTimeSpent());
    }

    private @NotNull Student getStudent() {
        Student student = new Student();
        student.setId(studentId);
        student.setFirstName(firstName);
        student.setLastName(lastName);
        student.setCollegeEmail(collegeEmail);
        student.setPersonalEmail(personalEmail);
        student.setPassword(password);
        student.setProfilePictureUrl(profilePictureUrl);
        student.setUserName(userName);
        student.setDateOfBirth(dateOfBirth);
        student.setArchived(archived);
        student.setCourses(courses);
        student.setCreatedDate(createdDate);
        student.setUpdatedDate(updatedDate);
        student.setAccuracy(accuracy);
        student.setAcademicDegree(academicDegree);
        student.setCriticalThinking(criticalThinking);
        student.setUnderstanding(understanding);
        student.setMemory(memory);
        student.setTimeSpent(timeSpent);
        return student;
    }
}


