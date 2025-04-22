package com.shodhAI.ShodhAI.entity;

import com.shodhAI.ShodhAI.Entity.Course;
import com.shodhAI.ShodhAI.Entity.CourseSemesterDegree;
import com.shodhAI.ShodhAI.Entity.Faculty;
import com.shodhAI.ShodhAI.Entity.Institute;
import com.shodhAI.ShodhAI.Entity.Notification;
import com.shodhAI.ShodhAI.Entity.Student;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class FacultyTest {
    private Long facultyId;
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
    private List<Student> students;
    private List<Notification> notifications;
    private List<Course> courses;


    @BeforeEach
    void setUp() {
        facultyId = 1L;
        firstName = "facultyName";
        lastName = "facultyLastName";
        collegeEmail = "simran@gmail.com";
        archived = 'N';
        personalEmail= "simran12@gmail.com";
        password= "123456";
        profilePictureUrl= "profileUrl";
        userName= "simran19";
        dateOfBirth= new Date();
        notifications= new ArrayList<>();
        students= new ArrayList<>();
        courses= new ArrayList<>();
        createdDate= new Date();
        updatedDate= new Date();
    }

    @Test
    @DisplayName("testFacultyConstructor")
    void testFacultyConstructor(){
        Faculty facultyByConstructor =  Faculty.builder().id(facultyId).firstName(firstName).lastName(lastName).archived(archived).collegeEmail(collegeEmail).personalEmail(personalEmail).dateOfBirth(dateOfBirth).profilePictureUrl(profilePictureUrl).password(password).userName(userName).createdDate(createdDate).updatedDate(updatedDate).students(students).notifications(notifications).courses(courses).build();

        assertEquals(facultyId, facultyByConstructor.getId());
        assertEquals(firstName, facultyByConstructor.getFirstName());
        assertEquals(lastName, facultyByConstructor.getLastName());
        assertEquals(collegeEmail, facultyByConstructor.getCollegeEmail());
        assertEquals(personalEmail, facultyByConstructor.getPersonalEmail());
        assertEquals(password, facultyByConstructor.getPassword());
        assertEquals(profilePictureUrl, facultyByConstructor.getProfilePictureUrl());
        assertEquals(userName, facultyByConstructor.getUserName());
        assertEquals(dateOfBirth, facultyByConstructor.getDateOfBirth());
        assertEquals(archived, facultyByConstructor.getArchived());
        assertEquals(createdDate, facultyByConstructor.getCreatedDate());
        assertEquals(updatedDate, facultyByConstructor.getUpdatedDate());
    }

    @Test
    @DisplayName("testFacultySettersAndGetters")
    void testFacultySettersAndGetters(){
        Faculty faculty = getFaculty();
        assertEquals(facultyId, faculty.getId());
        assertEquals(firstName, faculty.getFirstName());
        assertEquals(lastName, faculty.getLastName());
        assertEquals(collegeEmail, faculty.getCollegeEmail());
        assertEquals(personalEmail, faculty.getPersonalEmail());
        assertEquals(password, faculty.getPassword());
        assertEquals(profilePictureUrl, faculty.getProfilePictureUrl());
        assertEquals(userName, faculty.getUserName());
        assertEquals(dateOfBirth, faculty.getDateOfBirth());
        assertEquals(archived, faculty.getArchived());
        assertEquals(courses, faculty.getCourses());
        assertEquals(createdDate, faculty.getCreatedDate());
        assertEquals(updatedDate, faculty.getUpdatedDate());
    }

    private @NotNull Faculty getFaculty() {
        Faculty faculty = new Faculty();
        faculty.setId(facultyId);
        faculty.setFirstName(firstName);
        faculty.setLastName(lastName);
        faculty.setCollegeEmail(collegeEmail);
        faculty.setPersonalEmail(personalEmail);
        faculty.setPassword(password);
        faculty.setProfilePictureUrl(profilePictureUrl);
        faculty.setUserName(userName);
        faculty.setDateOfBirth(dateOfBirth);
        faculty.setArchived(archived);
        faculty.setCourses(courses);
        faculty.setCreatedDate(createdDate);
        faculty.setUpdatedDate(updatedDate);
        return faculty;
    }
}


