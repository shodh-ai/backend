package com.shodhAI.ShodhAI.entity;

import com.shodhAI.ShodhAI.Entity.AcademicDegree;
import com.shodhAI.ShodhAI.Entity.Course;
import com.shodhAI.ShodhAI.Entity.Module;
import com.shodhAI.ShodhAI.Entity.CourseSemesterDegree;
import com.shodhAI.ShodhAI.Entity.Faculty;
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

public class CourseTest {
    private Long courseId;
    private String courseTitle;
    private String courseDescription;
    private String courseDuration;
    private Character archived;
    private Date createdDate;
    private Date updatedDate;
    private List<Notification> notifications;
    private List<CourseSemesterDegree> courseSemesterDegrees;
    private List<Student> students;
    private AcademicDegree academicDegree;
    private List<Faculty> facultyMembers ;
    private List<Module> modules;

    @BeforeEach
    void setUp() {
        courseId = 1L;
        courseTitle = "courseName";
        courseDescription = "courseDescription";
        courseDuration = "courseDuration";
        archived = 'N';
        academicDegree= new AcademicDegree();
        facultyMembers = new ArrayList<>() {};
        notifications = new ArrayList<>() {};
        courseSemesterDegrees = new ArrayList<>();
        students = new ArrayList<>();
        modules = new ArrayList<>();
        createdDate= new Date();
        updatedDate= new Date();
    }

    @Test
    @DisplayName("testCourseConstructor")
    void testCourseConstructor(){
        Course courseByConstructor =  Course.builder().courseId(courseId).courseTitle(courseTitle).courseDescription(courseDescription).archived(archived).courseDuration(courseDuration).createdDate(createdDate).updatedDate(updatedDate).notifications(notifications).courseSemesterDegrees(courseSemesterDegrees).students(students).academicDegree(academicDegree).facultyMembers(facultyMembers).modules(modules).build();

        assertEquals(courseId, courseByConstructor.getCourseId());
        assertEquals(courseTitle, courseByConstructor.getCourseTitle());
        assertEquals(courseDescription, courseByConstructor.getCourseDescription());
        assertEquals(courseDuration, courseByConstructor.getCourseDuration());
        assertEquals(archived, courseByConstructor.getArchived());
        assertEquals(createdDate, courseByConstructor.getCreatedDate());
        assertEquals(updatedDate, courseByConstructor.getUpdatedDate());
        assertEquals(academicDegree, courseByConstructor.getAcademicDegree());
    }

    @Test
    @DisplayName("testCourseSettersAndGetters")
    void testCourseSettersAndGetters(){
        Course course = getCourse();
        assertEquals(courseId, course.getCourseId());
        assertEquals(courseTitle, course.getCourseTitle());
        assertEquals(courseDescription, course.getCourseDescription());
        assertEquals(courseDuration, course.getCourseDuration());
        assertEquals(archived, course.getArchived());
        assertEquals(courseSemesterDegrees, course.getCourseSemesterDegrees());
        assertEquals(modules, course.getModules());
        assertEquals(facultyMembers, course.getFacultyMembers());
        assertEquals(students, course.getStudents());
        assertEquals(notifications, course.getNotifications());
        assertEquals(modules, course.getModules());
        assertEquals(createdDate, course.getCreatedDate());
        assertEquals(updatedDate, course.getUpdatedDate());
    }

    private @NotNull Course getCourse() {
        Course course = new Course();
        course.setCourseId(courseId);
        course.setCourseTitle(courseTitle);
        course.setCourseDescription(courseDescription);
        course.setCourseDuration(courseDuration);
        course.setArchived(archived);
        course.setCourseSemesterDegrees(courseSemesterDegrees);
        course.setModules(modules);
        course.setFacultyMembers(facultyMembers);
        course.setStudents(students);
        course.setNotifications(notifications);
        course.setCreatedDate(createdDate);
        course.setUpdatedDate(updatedDate);
        return course;
    }
}

