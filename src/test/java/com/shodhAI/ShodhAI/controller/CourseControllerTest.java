package com.shodhAI.ShodhAI.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shodhAI.ShodhAI.Component.JwtUtil;
import com.shodhAI.ShodhAI.Controller.CourseController;
import com.shodhAI.ShodhAI.Dto.CourseDto;
import com.shodhAI.ShodhAI.Entity.Course;
import com.shodhAI.ShodhAI.Service.CourseService;
import com.shodhAI.ShodhAI.Service.ExceptionHandlingService;
import com.shodhAI.ShodhAI.Service.RoleService;
import com.shodhAI.ShodhAI.Service.StudentService;
import com.shodhAI.ShodhAI.configuration.TestJwtConfig;
import com.shodhAI.ShodhAI.configuration.TestSecurityConfig;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import({TestSecurityConfig.class, TestJwtConfig.class})
@WebMvcTest(CourseController.class)
@ExtendWith({SpringExtension.class, MockitoExtension.class})
@ContextConfiguration(classes = TestSecurityConfig.class)
public class CourseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CourseService courseService;

    @MockBean
    private RoleService roleService;

    @MockBean
    private StudentService studentService;

    @MockBean
    private EntityManager entityManager;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private ExceptionHandlingService exceptionHandlingService;

    @Test
    @DisplayName("test_addCourse_success")
    void testAddCourse() throws Exception {
        // Create a sample CourseDto
        CourseDto courseDto = new CourseDto();
        courseDto.setCourseTitle("Introduction to Computer Science");
        courseDto.setCourseDescription("CS101");

        Course course = new Course();
        course.setCourseId(1L);
        course.setCourseTitle("Introduction to Computer Science");
        course.setCourseDescription("CS101");

        // Mock service calls
        doNothing().when(courseService).validateCourse(any(CourseDto.class));
        when(courseService.saveCourse(any(CourseDto.class))).thenReturn(course);

        // Perform POST request
        mockMvc.perform(post("/course/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(courseDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Course Created Successfully"))
                .andExpect(jsonPath("$.data.courseId").value(1L))
                .andExpect(jsonPath("$.data.title").value("Introduction to Computer Science"));

        // Verify service methods were called
        verify(courseService).validateCourse(any(CourseDto.class));
        verify(courseService).saveCourse(any(CourseDto.class));
    }

    @Test
    @DisplayName("test_addCourse_validationError")
    void testAddCourseValidationError() throws Exception {
        // Create an invalid CourseDto
        CourseDto courseDto = new CourseDto();
        courseDto.setCourseTitle(""); // Invalid empty name

        // Mock validation exception
        doThrow(new IllegalArgumentException("Course name cannot be empty"))
                .when(courseService).validateCourse(any(CourseDto.class));

        // Perform POST request
        mockMvc.perform(post("/course/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(courseDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("Illegal Exception Caught: Course name cannot be empty"));

        // Verify service methods were called correctly
        verify(courseService).validateCourse(any(CourseDto.class));
        verify(courseService, never()).saveCourse(any(CourseDto.class));
        verify(exceptionHandlingService).handleException(any(IllegalArgumentException.class));
    }

    @Test
    @DisplayName("test_getCourseById_success")
    void testGetCourseById() throws Exception {
        // Create sample course
        Course course = new Course();
        course.setCourseId(1L);
        course.setCourseTitle("Introduction to Computer Science");
        course.setCourseDescription("CS101");

        // Mock service call
        when(courseService.getCourseById(1L)).thenReturn(course);

        // Perform GET request
        mockMvc.perform(get("/course/get-course-by-id/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Course Retrieved Successfully"))
                .andExpect(jsonPath("$.data.courseId").value(1))
                .andExpect(jsonPath("$.data.title").value("Introduction to Computer Science"));

        // Verify service method was called
        verify(courseService).getCourseById(1L);
    }

    @Test
    @DisplayName("test_getCourseById_notFound")
    void testGetCourseByIdNotFound() throws Exception {
        // Mock not found scenario
        when(courseService.getCourseById(999L)).thenReturn(null);

        // Perform GET request
        mockMvc.perform(get("/course/get-course-by-id/999"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Data not present in the DB"));

        // Verify service method was called
        verify(courseService).getCourseById(999L);
    }

    @Test
    @DisplayName("test_updateCourse_success")
    void testUpdateCourse() throws Exception {
        // Create sample DTO and entity
        CourseDto courseDto = new CourseDto();
        courseDto.setCourseTitle("Updated Computer Science");
        courseDto.setCourseDescription("CS102");

        Course updatedCourse = new Course();
        updatedCourse.setCourseId(1L);
        updatedCourse.setCourseTitle("Updated Computer Science");
        updatedCourse.setCourseDescription("CS102");

        // Mock service calls
        when(courseService.updateCourse(eq(1L), any(CourseDto.class))).thenReturn(updatedCourse);

        // Perform PATCH request
        mockMvc.perform(patch("/course/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(courseDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Course updated Successfully"))
                .andExpect(jsonPath("$.data.courseId").value(1))
                .andExpect(jsonPath("$.data.title").value("Updated Computer Science"));

        // Verify service methods were called
        verify(courseService).updateCourse(eq(1L), any(CourseDto.class));
    }

    @Test
    @DisplayName("test_updateCourse_notFound")
    void testUpdateCourseNotFound() throws Exception {
        // Create sample DTO
        CourseDto courseDto = new CourseDto();
        courseDto.setCourseTitle("Updated Computer Science");

        // Mock exception for non-existent course
        when(courseService.updateCourse(eq(999L), any(CourseDto.class)))
                .thenThrow(new IllegalArgumentException("Course with id 999 not found"));

        // Perform PATCH request
        mockMvc.perform(patch("/course/update/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(courseDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("Illegal Exception Caught: Course with id 999 not found"));

        // Verify service method was called
        verify(courseService).updateCourse(eq(999L), any(CourseDto.class));
        verify(exceptionHandlingService).handleException(any(IllegalArgumentException.class));
    }

    @Test
    @DisplayName("test_deleteCourse_success")
    void testDeleteCourse() throws Exception {
        // Create sample course
        Course course = new Course();
        course.setCourseId(1L);
        course.setCourseTitle("Introduction to Computer Science");
        course.setCourseDescription("CS101");

        // Mock service calls
        when(courseService.getCourseById(1L)).thenReturn(course);
        when(courseService.deleteCourseById(1L)).thenReturn(course);

        // Perform DELETE request
        mockMvc.perform(delete("/course/delete/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Course is successfully deleted"))
                .andExpect(jsonPath("$.data.courseId").value(1));

        // Verify service methods were called
        verify(courseService).getCourseById(1L);
        verify(courseService).deleteCourseById(1L);
    }

    @Test
    @DisplayName("test_deleteCourse_notFound")
    void testDeleteCourseNotFound() throws Exception {
        // Mock not found scenario
        when(courseService.getCourseById(999L)).thenReturn(null);

        // Perform DELETE request
        mockMvc.perform(delete("/course/delete/999"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("Course with id 999 not found"));

        // Verify service method was called but delete was not
        verify(courseService).getCourseById(999L);
        verify(courseService, never()).deleteCourseById(999L);
    }

    @Test
    @DisplayName("test_getFilterCourse_success")
    void testGetFilterCourse() throws Exception {
        // Create sample courses
        List<Course> courses = new ArrayList<>();
        Course course1 = new Course();
        course1.setCourseId(1L);
        course1.setCourseTitle("Introduction to Computer Science");
        course1.setCourseDescription("CS101");

        Course course2 = new Course();
        course2.setCourseId(2L);
        course2.setCourseTitle("Data Structures");
        course2.setCourseDescription("CS102");

        courses.add(course1);
        courses.add(course2);

        // Mock JWT token extraction
        when(jwtUtil.extractRoleId("mock-token")).thenReturn(1L);
        when(jwtUtil.extractId("mock-token")).thenReturn(1L);

        // Mock service call
        when(courseService.courseFilter(eq(1L), eq(1L), eq(1L), eq(1L), eq(1L))).thenReturn(courses);

        // Perform GET request
        mockMvc.perform(get("/course/get-filter-course")
                        .param("courseId", "1")
                        .param("semesterId", "1")
                        .param("degreeId", "1")
                        .param("offset", "0")
                        .param("limit", "10")
                        .header("Authorization", "Bearer mock-token"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Courses Retrieved Successfully"))
                .andExpect(jsonPath("$.data.totalItems").value(2))
                .andExpect(jsonPath("$.data.currentPage").value(0))
                .andExpect(jsonPath("$.data.courses.length()").value(2));

        // Verify service methods were called
        verify(jwtUtil).extractRoleId("mock-token");
        verify(jwtUtil).extractId("mock-token");
        verify(courseService).courseFilter(eq(1L), eq(1L), eq(1L), eq(1L), eq(1L));
    }

    @Test
    @DisplayName("test_getFilterCourse_emptyResult")
    void testGetFilterCourseEmptyResult() throws Exception {
        // Create empty list
        List<Course> emptyCourses = new ArrayList<>();

        // Mock JWT token extraction
        when(jwtUtil.extractRoleId("mock-token")).thenReturn(1L);
        when(jwtUtil.extractId("mock-token")).thenReturn(1L);

        // Mock service call
        when(courseService.courseFilter(any(), eq(1L), eq(1L), any(), any())).thenReturn(emptyCourses);

        // Perform GET request
        mockMvc.perform(get("/course/get-filter-course")
                        .param("offset", "0")
                        .param("limit", "10")
                        .header("Authorization", "Bearer mock-token"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("No courses found with the given criteria"))
                .andExpect(jsonPath("$.data").isEmpty());

        // Verify service methods were called
        verify(jwtUtil).extractRoleId("mock-token");
        verify(jwtUtil).extractId("mock-token");
        verify(courseService).courseFilter(any(), eq(1L), eq(1L), any(), any());
    }

    @Test
    @DisplayName("test_getFilterCourse_invalidPagination")
    void testGetFilterCourseInvalidPagination() throws Exception {
        // Perform GET request with negative offset
        mockMvc.perform(get("/course/get-filter-course")
                        .param("offset", "-1")
                        .param("limit", "10")
                        .header("Authorization", "Bearer mock-token"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("Offset for pagination cannot be a negative number"));

        // Verify no service methods were called
        verify(courseService, never()).courseFilter(any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("test_getFilterCourse_pageOutOfRange")
    void testGetFilterCoursePageOutOfRange() throws Exception {
        // Create sample courses list with only 5 items
        List<Course> courses = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            Course course = new Course();
            course.setCourseId((long) i);
            course.setCourseTitle("Course " + i);
            courses.add(course);
        }

        // Mock JWT token extraction
        when(jwtUtil.extractRoleId("mock-token")).thenReturn(1L);
        when(jwtUtil.extractId("mock-token")).thenReturn(1L);

        // Mock service call
        when(courseService.courseFilter(any(), eq(1L), eq(1L), any(), any())).thenReturn(courses);

        // Perform GET request with offset beyond available pages
        mockMvc.perform(get("/course/get-filter-course")
                        .param("offset", "2") // Page 2 (assuming 5 items per page would be page 0)
                        .param("limit", "5")
                        .header("Authorization", "Bearer mock-token"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("No more courses available"));

        // Verify service methods were called
        verify(jwtUtil).extractRoleId("mock-token");
        verify(jwtUtil).extractId("mock-token");
        verify(courseService).courseFilter(any(), eq(1L), eq(1L), any(), any());
    }

    @Test
    @DisplayName("test_getFilterCourse_zeroLimit")
    void testGetFilterCourseZeroLimit() throws Exception {
        // Perform GET request with zero limit
        mockMvc.perform(get("/course/get-filter-course")
                        .param("offset", "0")
                        .param("limit", "0")
                        .header("Authorization", "Bearer mock-token"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("Limit for pagination cannot be a negative number or 0"));

        // Verify no service methods were called
        verify(courseService, never()).courseFilter(any(), any(), any(), any(), any());
    }
}