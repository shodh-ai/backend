package com.shodhAI.ShodhAI.service;

import com.shodhAI.ShodhAI.Component.Constant;
import com.shodhAI.ShodhAI.Dto.CourseDto;
import com.shodhAI.ShodhAI.Dto.CourseSemesterDegreeDto;
import com.shodhAI.ShodhAI.Entity.*;
import com.shodhAI.ShodhAI.Service.AcademicDegreeService;
import com.shodhAI.ShodhAI.Service.CourseService;
import com.shodhAI.ShodhAI.Service.ExceptionHandlingService;
import com.shodhAI.ShodhAI.Service.RoleService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CourseServiceTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private AcademicDegreeService academicDegreeService;

    @Mock
    private ExceptionHandlingService exceptionHandlingService;

    @Mock
    private RoleService roleService;

    @Mock
    private TypedQuery<Course> courseTypedQuery;

    @Mock
    private TypedQuery<CourseSemesterDegree> courseSemesterDegreeTypedQuery;

    @Mock
    private TypedQuery<Long> longTypedQuery;

    @Mock
    private TypedQuery<Student> studentTypedQuery;

    @Mock
    private TypedQuery<Faculty> facultyTypedQuery;

    @InjectMocks
    private CourseService courseService;

    private CourseDto validCourseDto;
    private Course course;
    private AcademicDegree academicDegree;

    @BeforeEach
    void setUp() {
        // Setup valid CourseDto
        validCourseDto = new CourseDto();
        validCourseDto.setCourseTitle("Introduction to Programming");
        validCourseDto.setCourseDescription("Basic programming concepts");
        validCourseDto.setCourseDuration("4 months");
        validCourseDto.setStartDate(new Date());
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 4);
        validCourseDto.setEndDate(calendar.getTime());
        validCourseDto.setAcademicDegreeId(1L);

        // Setup AcademicDegree
        academicDegree = new AcademicDegree();
        academicDegree.setDegreeId(1L);
        academicDegree.setDegreeName("BSc");
        academicDegree.setInstitutionName("University");
        academicDegree.setProgramName("CS");

        // Setup Course
        course = new Course();
        course.setCourseId(1L);
        course.setCourseTitle("Introduction to Programming");
        course.setCourseDescription("Basic programming concepts");
        course.setCourseDuration("4 months");
        course.setStartDate(validCourseDto.getStartDate());
        course.setEndDate(validCourseDto.getEndDate());
        course.setAcademicDegree(academicDegree);
        course.setCreatedDate(new Date());
        course.setUpdatedDate(new Date());
        course.setArchived('N');
        course.setStudents(new ArrayList<>());
        course.setFacultyMembers(new ArrayList<>());
        course.setCourseSemesterDegrees(new ArrayList<>());
    }

    @Test
    @DisplayName("Should validate course successfully")
    void testValidateCourse() throws Exception {
        // Given
        CourseDto dto = new CourseDto();
        dto.setCourseTitle("  Introduction to Programming  ");
        dto.setCourseDescription("  Basic programming concepts  ");
        dto.setCourseDuration("  4 months  ");
        dto.setStartDate(validCourseDto.getStartDate());
        dto.setEndDate(validCourseDto.getEndDate());
        dto.setAcademicDegreeId(1L);

        // When
        courseService.validateCourse(dto);

        // Then
        assertEquals("Introduction to Programming", dto.getCourseTitle());
        assertEquals("Basic programming concepts", dto.getCourseDescription());
        assertEquals("4 months", dto.getCourseDuration());
    }

    @Test
    @DisplayName("Should throw exception when course title is empty")
    void testValidateCourseWithEmptyTitle() {
        // Given
        CourseDto dto = new CourseDto();
        dto.setCourseTitle("");
        dto.setCourseDescription("Basic programming concepts");
        dto.setCourseDuration("4 months");

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> courseService.validateCourse(dto));
        assertEquals("Course title cannot be null or empty", exception.getMessage());
        verify(exceptionHandlingService, times(1)).handleException(any(IllegalArgumentException.class));
    }

    @Test
    @DisplayName("Should throw exception when course description is empty")
    void testValidateCourseWithEmptyDescription() {
        // Given
        CourseDto dto = new CourseDto();
        dto.setCourseTitle("Introduction to Programming");
        dto.setCourseDescription("");
        dto.setCourseDuration("4 months");

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> courseService.validateCourse(dto));
        assertEquals("Course Description cannot be empty", exception.getMessage());
        verify(exceptionHandlingService, times(1)).handleException(any(IllegalArgumentException.class));
    }

    @Test
    @DisplayName("Should throw exception when course duration is empty")
    void testValidateCourseWithEmptyDuration() {
        // Given
        CourseDto dto = new CourseDto();
        dto.setCourseTitle("Introduction to Programming");
        dto.setCourseDescription("Basic programming concepts");
        dto.setCourseDuration("");

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> courseService.validateCourse(dto));
        assertEquals("Course Duration cannot be null or empty", exception.getMessage());
        verify(exceptionHandlingService, times(1)).handleException(any(IllegalArgumentException.class));
    }

    @Test
    @DisplayName("Should throw exception when start date is after end date")
    void testValidateCourseWithInvalidDates() {
        // Given
        CourseDto dto = new CourseDto();
        dto.setCourseTitle("Introduction to Programming");
        dto.setCourseDescription("Basic programming concepts");
        dto.setCourseDuration("4 months");
        dto.setEndDate(new Date()); // Now

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 10);
        dto.setStartDate(calendar.getTime()); // 10 days later

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> courseService.validateCourse(dto));
        assertEquals("Course Start date must be before of end date", exception.getMessage());
        verify(exceptionHandlingService, times(1)).handleException(any(IllegalArgumentException.class));
    }

    @Test
    @DisplayName("Should throw exception when only end date is provided")
    void testValidateCourseWithOnlyEndDate() {
        // Given
        CourseDto dto = new CourseDto();
        dto.setCourseTitle("Introduction to Programming");
        dto.setCourseDescription("Basic programming concepts");
        dto.setCourseDuration("4 months");
        dto.setEndDate(new Date());
        dto.setStartDate(null);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> courseService.validateCourse(dto));
        assertEquals("Course Start date cannot be null if Course End date is passed", exception.getMessage());
        verify(exceptionHandlingService, times(1)).handleException(any(IllegalArgumentException.class));
    }

    @Test
    @DisplayName("Should throw exception when academic degree id is invalid")
    void testValidateCourseWithInvalidAcademicDegreeId() {
        // Given
        CourseDto dto = new CourseDto();
        dto.setCourseTitle("Introduction to Programming");
        dto.setCourseDescription("Basic programming concepts");
        dto.setCourseDuration("4 months");
        dto.setAcademicDegreeId(0L);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> courseService.validateCourse(dto));
        assertEquals("Academic Degree Id cannot be <= 0", exception.getMessage());
        verify(exceptionHandlingService, times(1)).handleException(any(IllegalArgumentException.class));
    }

    @Test
    @DisplayName("Should save course successfully")
    void testSaveCourse() throws Exception {
        // Given
        when(academicDegreeService.getAcademicDegreeById(1L)).thenReturn(academicDegree);
        when(entityManager.createQuery(anyString(), eq(Long.class))).thenReturn(longTypedQuery);
        when(longTypedQuery.getSingleResult()).thenReturn(0L);
        when(entityManager.merge(any(Course.class))).thenReturn(course);

        // When
        Course result = courseService.saveCourse(validCourseDto);

        // Then
        assertNotNull(result);
        assertEquals("Introduction to Programming", result.getCourseTitle());
        assertEquals("Basic programming concepts", result.getCourseDescription());
        assertEquals("4 months", result.getCourseDuration());
        assertEquals(1L, result.getCourseId());

        verify(academicDegreeService, times(1)).getAcademicDegreeById(1L);
        verify(entityManager, times(1)).createQuery(anyString(), eq(Long.class));
        verify(longTypedQuery, times(1)).getSingleResult();
        verify(entityManager, times(1)).merge(any(Course.class));
    }

    @Test
    @DisplayName("Should handle persistence exception when saving course")
    void testSaveCourseWithPersistenceException() throws Exception {
        // Given
        when(academicDegreeService.getAcademicDegreeById(1L)).thenReturn(academicDegree);
        when(entityManager.createQuery(anyString(), eq(Long.class))).thenReturn(longTypedQuery);
        when(longTypedQuery.getSingleResult()).thenReturn(0L);
        when(entityManager.merge(any(Course.class))).thenThrow(new PersistenceException("Database error"));

        // When & Then
        PersistenceException exception = assertThrows(PersistenceException.class,
                () -> courseService.saveCourse(validCourseDto));
        assertEquals("Database error", exception.getMessage());
        verify(exceptionHandlingService, times(1)).handleException(any(PersistenceException.class));
    }

    @Test
    @DisplayName("Should get all courses")
    void testGetAllCourse() throws Exception {
        // Given
        List<Course> courses = Arrays.asList(course);
        when(entityManager.createQuery(Constant.GET_ALL_COURSES, Course.class)).thenReturn(courseTypedQuery);
        when(courseTypedQuery.getResultList()).thenReturn(courses);

        // When
        List<Course> result = courseService.getAllCourse();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Introduction to Programming", result.get(0).getCourseTitle());
        verify(entityManager, times(1)).createQuery(Constant.GET_ALL_COURSES, Course.class);
        verify(courseTypedQuery, times(1)).getResultList();
    }

    @Test
    @DisplayName("Should get course by id")
    void testGetCourseById() throws Exception {
        // Given
        List<Course> courses = Arrays.asList(course);
        when(entityManager.createQuery(Constant.GET_COURSE_BY_ID, Course.class)).thenReturn(courseTypedQuery);
        when(courseTypedQuery.setParameter(eq("courseId"), eq(1L))).thenReturn(courseTypedQuery);
        when(courseTypedQuery.getResultList()).thenReturn(courses);

        // When
        Course result = courseService.getCourseById(1L);

        // Then
        assertNotNull(result);
        assertEquals("Introduction to Programming", result.getCourseTitle());
        verify(entityManager, times(1)).createQuery(Constant.GET_COURSE_BY_ID, Course.class);
        verify(courseTypedQuery, times(1)).setParameter(eq("courseId"), eq(1L));
        verify(courseTypedQuery, times(1)).getResultList();
    }

    @Test
    @DisplayName("Should throw exception when course not found by id")
    void testGetCourseByIdNotFound() {
        // Given
        when(entityManager.createQuery(Constant.GET_COURSE_BY_ID, Course.class)).thenReturn(courseTypedQuery);
        when(courseTypedQuery.setParameter(eq("courseId"), eq(99L))).thenReturn(courseTypedQuery);
        when(courseTypedQuery.getResultList()).thenReturn(Collections.emptyList());

        // When & Then
        IndexOutOfBoundsException exception = assertThrows(IndexOutOfBoundsException.class,
                () -> courseService.getCourseById(99L));
        assertEquals("Course not found with given Id", exception.getMessage());
        verify(exceptionHandlingService, times(1)).handleException(any(IndexOutOfBoundsException.class));
    }

    @Test
    @DisplayName("Should update course successfully")
    void testUpdateCourse() throws Exception {
        // Given
        CourseDto updateDto = new CourseDto();
        updateDto.setCourseTitle("Updated Programming Course");
        updateDto.setCourseDescription("Updated description");
        updateDto.setCourseDuration("6 months");

        when(entityManager.find(Course.class, 1L)).thenReturn(course);
        when(entityManager.merge(any(Course.class))).thenReturn(course);

        // When
        Course result = courseService.updateCourse(1L, updateDto);

        // Then
        assertNotNull(result);
        verify(entityManager, times(1)).find(Course.class, 1L);
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent course")
    void testUpdateNonExistentCourse() {
        // Given
        CourseDto updateDto = new CourseDto();
        updateDto.setCourseTitle("Updated Programming Course");
        when(entityManager.find(Course.class, 99L)).thenReturn(null);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> courseService.updateCourse(99L, updateDto));
        assertEquals("Course with id 99 does not found", exception.getMessage());
        verify(exceptionHandlingService, times(1)).handleException(any(IllegalArgumentException.class));
    }

    @Test
    @DisplayName("Should update course with semester degree associations")
    void testUpdateCourseWithSemesterDegreeAssociations() throws Exception {
        // Given
        CourseDto updateDto = new CourseDto();
        updateDto.setCourseTitle("Updated Programming Course");

        List<CourseSemesterDegreeDto> associations = new ArrayList<>();
        CourseSemesterDegreeDto associationDto = new CourseSemesterDegreeDto();
        associationDto.setSemesterId(1L);
        associationDto.setAcademicDegreeId(1L);
        associations.add(associationDto);
        updateDto.setCourseSemesterDegreeAssociations(associations);

        Semester semester = new Semester();
        semester.setSemesterId(1L);
        List<AcademicDegree> semesterDegrees = new ArrayList<>();
        semesterDegrees.add(academicDegree);
        semester.setAcademicDegrees(semesterDegrees);

        when(entityManager.find(Course.class, 1L)).thenReturn(course);
        when(entityManager.createQuery(anyString(), eq(CourseSemesterDegree.class)))
                .thenReturn(courseSemesterDegreeTypedQuery);
        when(courseSemesterDegreeTypedQuery.setParameter(anyString(), any())).thenReturn(courseSemesterDegreeTypedQuery);
        when(courseSemesterDegreeTypedQuery.getResultList()).thenReturn(new ArrayList<>());
        when(entityManager.find(Semester.class, 1L)).thenReturn(semester);
        when(entityManager.find(AcademicDegree.class, 1L)).thenReturn(academicDegree);
        when(entityManager.createQuery(anyString(), eq(Long.class))).thenReturn(longTypedQuery);
        when(longTypedQuery.getSingleResult()).thenReturn(0L);
        when(entityManager.merge(any(Course.class))).thenReturn(course);

        // When
        Course result = courseService.updateCourse(1L, updateDto);

        // Then
        assertNotNull(result);
        verify(entityManager, times(1)).find(Course.class, 1L);
        verify(entityManager, times(1)).find(Semester.class, 1L);
        verify(entityManager, times(1)).find(AcademicDegree.class, 1L);
    }

    @Test
    @DisplayName("Should filter courses successfully")
    void testCourseFilter() throws Exception {
        // Given
        Role role = new Role();
        role.setRoleName("ROLE_ADMIN");

        List<Course> courses = Arrays.asList(course);
        when(roleService.getRoleById(anyLong())).thenReturn(role);
        when(entityManager.createQuery(anyString(), eq(Course.class))).thenReturn(courseTypedQuery);
        when(courseTypedQuery.setParameter(anyString(), any())).thenReturn(courseTypedQuery);
        when(courseTypedQuery.getResultList()).thenReturn(courses);

        // When
        List<Course> result = courseService.courseFilter(1L, 1L, 1L, 1L, 1L);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(roleService, times(1)).getRoleById(anyLong());
        verify(entityManager, times(1)).createQuery(anyString(), eq(Course.class));
        verify(courseTypedQuery, times(1)).getResultList();
    }

    @Test
    @DisplayName("Should filter courses with USER role")
    void testCourseFilterWithUserRole() throws Exception {
        // Given
        Role role = new Role();
        role.setRoleName(Constant.ROLE_USER);

        Student student = new Student();
        student.setId(1L);
        student.setAcademicDegree(academicDegree);

        List<Course> courses = Arrays.asList(course);
        when(roleService.getRoleById(anyLong())).thenReturn(role);
        when(entityManager.find(Student.class, 1L)).thenReturn(student);
        when(entityManager.createQuery(anyString(), eq(Course.class))).thenReturn(courseTypedQuery);
        when(courseTypedQuery.setParameter(anyString(), any())).thenReturn(courseTypedQuery);
        when(courseTypedQuery.getResultList()).thenReturn(courses);

        // When
        List<Course> result = courseService.courseFilter(1L, 1L, 1L, 1L, null);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(roleService, times(1)).getRoleById(anyLong());
        verify(entityManager, times(1)).find(Student.class, 1L);
        verify(entityManager, times(1)).createQuery(anyString(), eq(Course.class));
        verify(courseTypedQuery, times(1)).getResultList();
    }

    @Test
    @DisplayName("Should find course count")
    void testFindCourseCount() throws Exception {
        // Given
        when(entityManager.createQuery(anyString(), eq(Long.class))).thenReturn(longTypedQuery);
        when(longTypedQuery.getSingleResult()).thenReturn(10L);

        // When
        long result = courseService.findCourseCount();

        // Then
        assertEquals(10L, result);
        verify(entityManager, times(1)).createQuery(anyString(), eq(Long.class));
        verify(longTypedQuery, times(1)).getSingleResult();
    }

    @Test
    @DisplayName("Should return 0 when no courses found")
    void testFindCourseCountEmpty() throws Exception {
        // Given
        when(entityManager.createQuery(anyString(), eq(Long.class))).thenReturn(longTypedQuery);
        when(longTypedQuery.getSingleResult()).thenReturn(null);

        // When
        long result = courseService.findCourseCount();

        // Then
        assertEquals(0L, result);
        verify(entityManager, times(1)).createQuery(anyString(), eq(Long.class));
        verify(longTypedQuery, times(1)).getSingleResult();
    }

    @Test
    @DisplayName("Should find course semester degree count")
    void testFindCourseDegreeSemesterCount() throws Exception {
        // Given
        when(entityManager.createQuery(anyString(), eq(Long.class))).thenReturn(longTypedQuery);
        when(longTypedQuery.getSingleResult()).thenReturn(10L);

        // When
        long result = courseService.findCourseDegreeSemesterCount();

        // Then
        assertEquals(10L, result);
        verify(entityManager, times(1)).createQuery(anyString(), eq(Long.class));
        verify(longTypedQuery, times(1)).getSingleResult();
    }

    @Test
    @DisplayName("Should return 0 when no course semester degree associations found")
    void testFindCourseDegreeSemesterCountEmpty() throws Exception {
        // Given
        when(entityManager.createQuery(anyString(), eq(Long.class))).thenReturn(longTypedQuery);
        when(longTypedQuery.getSingleResult()).thenReturn(null);

        // When
        long result = courseService.findCourseDegreeSemesterCount();

        // Then
        assertEquals(0L, result);
        verify(entityManager, times(1)).createQuery(anyString(), eq(Long.class));
        verify(longTypedQuery, times(1)).getSingleResult();
    }

    @Test
    @DisplayName("Should delete course by id")
    void testDeleteCourseById() throws Exception {
        // Given
        when(entityManager.find(Course.class, 1L)).thenReturn(course);
        when(entityManager.merge(course)).thenReturn(course);

        // When
        Course result = courseService.deleteCourseById(1L);

        // Then
        assertNotNull(result);
        assertEquals('Y', result.getArchived());
        verify(entityManager, times(1)).find(Course.class, 1L);
        verify(entityManager, times(1)).merge(course);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent course")
    void testDeleteNonExistentCourse() {
        // Given
        when(entityManager.find(Course.class, 99L)).thenReturn(null);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> courseService.deleteCourseById(99L));
        assertEquals("Course with id 99 not found", exception.getMessage());
    }

    @Test
    @DisplayName("Should update course students")
    void testUpdateCourseStudents() throws Exception {
        // Given
        CourseDto updateDto = new CourseDto();
        updateDto.setCourseTitle("Updated Programming Course");
        List<Long> studentIds = Arrays.asList(1L, 2L);
        updateDto.setStudentIds(studentIds);

        Student student1 = new Student();
        student1.setId(1L);
        student1.setCourses(new ArrayList<>());
        student1.setFacultyMembers(new ArrayList<>());

        Student student2 = new Student();
        student2.setId(2L);
        student2.setCourses(new ArrayList<>());
        student2.setFacultyMembers(new ArrayList<>());

        List<Student> students = Arrays.asList(student1, student2);

        when(entityManager.find(Course.class, 1L)).thenReturn(course);
        when(entityManager.createQuery(anyString(), eq(Student.class))).thenReturn(studentTypedQuery);
        when(studentTypedQuery.setParameter(anyString(), any())).thenReturn(studentTypedQuery);
        when(studentTypedQuery.getResultList()).thenReturn(students);
        when(entityManager.merge(any(Course.class))).thenReturn(course);

        // When
        Course result = courseService.updateCourse(1L, updateDto);

        // Then
        assertNotNull(result);
        verify(entityManager, times(1)).find(Course.class, 1L);
        verify(entityManager, times(1)).createQuery(anyString(), eq(Student.class));
        verify(studentTypedQuery, times(1)).setParameter(anyString(), any());
    }

    @Test
    @DisplayName("Should update course faculty")
    void testUpdateCourseFaculty() throws Exception {
        // Given
        CourseDto updateDto = new CourseDto();
        updateDto.setCourseTitle("Updated Programming Course");
        List<Long> facultyIds = Arrays.asList(1L, 2L);
        updateDto.setFacultyMemberIds(facultyIds);

        Faculty faculty1 = new Faculty();
        faculty1.setId(1L);
        faculty1.setCourses(new ArrayList<>());
        faculty1.setStudents(new ArrayList<>());

        Faculty faculty2 = new Faculty();
        faculty2.setId(2L);
        faculty2.setCourses(new ArrayList<>());
        faculty2.setStudents(new ArrayList<>());

        List<Faculty> faculties = Arrays.asList(faculty1, faculty2);

        when(entityManager.find(Course.class, 1L)).thenReturn(course);
        when(entityManager.createQuery(anyString(), eq(Faculty.class))).thenReturn(facultyTypedQuery);
        when(facultyTypedQuery.setParameter(anyString(), any())).thenReturn(facultyTypedQuery);
        when(facultyTypedQuery.getResultList()).thenReturn(faculties);
        when(entityManager.merge(any(Course.class))).thenReturn(course);

        // When
        Course result = courseService.updateCourse(1L, updateDto);

        // Then
        assertNotNull(result);
        verify(entityManager, times(1)).find(Course.class, 1L);
        verify(entityManager, times(1)).createQuery(anyString(), eq(Faculty.class));
        verify(facultyTypedQuery, times(1)).setParameter(anyString(), any());
    }
}