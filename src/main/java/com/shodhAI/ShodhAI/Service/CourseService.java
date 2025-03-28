package com.shodhAI.ShodhAI.Service;

import com.shodhAI.ShodhAI.Component.Constant;
import com.shodhAI.ShodhAI.Dto.CourseDto;
import com.shodhAI.ShodhAI.Dto.FacultyDto;
import com.shodhAI.ShodhAI.Entity.Course;
import com.shodhAI.ShodhAI.Entity.Faculty;
import com.shodhAI.ShodhAI.Entity.Gender;
import com.shodhAI.ShodhAI.Entity.Semester;
import com.shodhAI.ShodhAI.Entity.Session;
import com.shodhAI.ShodhAI.Entity.Student;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class CourseService {

    @Autowired
    EntityManager entityManager;

    @Autowired
    ExceptionHandlingService exceptionHandlingService;

    public void validateCourse(CourseDto courseDto) throws Exception {
        try {
            if (courseDto.getCourseTitle() == null || courseDto.getCourseTitle().isEmpty()) {
                throw new IllegalArgumentException("Course title cannot be null or empty");
            }
            courseDto.setCourseTitle(courseDto.getCourseTitle().trim());

            if (courseDto.getCourseDescription() != null) {
                if (courseDto.getCourseDescription().isEmpty() || courseDto.getCourseDescription().trim().isEmpty()) {
                    throw new IllegalArgumentException("Course Description cannot be empty");
                }
                courseDto.setCourseDescription(courseDto.getCourseDescription().trim());
            }

            if (courseDto.getCourseDuration() != null) {
                if (courseDto.getCourseDuration().isEmpty()) {
                    throw new IllegalArgumentException("Course Duration cannot be null or empty");
                }
                courseDto.setCourseDuration(courseDto.getCourseDuration().trim());
            }

            // Dates
            if (courseDto.getStartDate() != null && courseDto.getEndDate() != null) {
                if (!courseDto.getStartDate().before(courseDto.getEndDate())) {
                    throw new IllegalArgumentException("Course Start date must be before of end date");
                }
            } else if (courseDto.getStartDate() == null && courseDto.getEndDate() != null) {
                throw new IllegalArgumentException("Course Start date cannot be null if Course End date is passed");
            }

        } catch (IllegalArgumentException illegalArgumentException) {
            exceptionHandlingService.handleException(illegalArgumentException);
            throw new IllegalArgumentException(illegalArgumentException.getMessage());
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            throw new Exception(exception.getMessage());
        }
    }

    @Transactional
    public Course saveCourse(CourseDto courseDto) throws Exception {
        try {

            Course course = new Course();

            Date currentDate = new Date();

            course.setCreatedDate(currentDate);
            course.setUpdatedDate(currentDate);
            course.setCourseTitle(courseDto.getCourseTitle());
            course.setCourseDescription(courseDto.getCourseDescription());
            course.setCourseDuration(courseDto.getCourseDuration());
            course.setStartDate(courseDto.getStartDate());
            course.setEndDate(courseDto.getEndDate());

            return entityManager.merge(course);

        } catch (PersistenceException persistenceException) {
            exceptionHandlingService.handleException(persistenceException);
            throw new PersistenceException(persistenceException.getMessage());
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            throw new Exception(exception.getMessage());
        }
    }

    public List<Course> getAllCourse() throws Exception {
        try {

            TypedQuery<Course> query = entityManager.createQuery(Constant.GET_ALL_COURSES, Course.class);
            return query.getResultList();

        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            throw new Exception(exception.getMessage());
        }
    }

    public Course getCourseById(Long courseId) throws Exception {
        try {

            TypedQuery<Course> query = entityManager.createQuery(Constant.GET_COURSE_BY_ID, Course.class);
            query.setParameter("courseId", courseId);
            return query.getResultList().get(0);

        } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
            exceptionHandlingService.handleException(indexOutOfBoundsException);
            throw new IndexOutOfBoundsException("Course not found with given Id");
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            throw new Exception(exception);
        }
    }

    public void validateAndSaveCourseForUpdate(CourseDto courseDto, Course courseToUpdate) throws Exception {
        try {
            if(courseDto.getCourseTitle()!=null)
            {
                if (courseDto.getCourseTitle().isEmpty()) {
                    throw new IllegalArgumentException("Course title cannot be null or empty");
                }
                courseDto.setCourseTitle(courseDto.getCourseTitle().trim());
                courseToUpdate.setCourseTitle(courseDto.getCourseTitle());
            }

            if (courseDto.getCourseDescription() != null) {
                if (courseDto.getCourseDescription().isEmpty() || courseDto.getCourseDescription().trim().isEmpty()) {
                    throw new IllegalArgumentException("Course Description cannot be empty");
                }
                courseDto.setCourseDescription(courseDto.getCourseDescription().trim());
                courseToUpdate.setCourseDescription(courseDto.getCourseDescription());
            }

            if (courseDto.getCourseDuration() != null) {
                if (courseDto.getCourseDuration().isEmpty()) {
                    throw new IllegalArgumentException("Course Duration cannot be null or empty");
                }
                courseDto.setCourseDuration(courseDto.getCourseDuration().trim());
                courseToUpdate.setCourseDuration(courseDto.getCourseDuration());
            }

          /*  if(courseDto.getSemesterId()!=null)
            {
                Semester semester = entityManager.find(Semester.class,courseDto.getSemesterId());
                if(semester==null)
                {
                    throw new IllegalArgumentException("Semester with id " + courseDto.getSemesterId()+ " does not exit");
                }
                courseToUpdate.setSemester(semester);
            }*/

            if (courseDto.getStartDate() != null && courseDto.getEndDate() != null) {
                if (!courseDto.getStartDate().before(courseDto.getEndDate())) {
                    throw new IllegalArgumentException("Course Start date must be before of end date");
                }
                courseToUpdate.setStartDate(courseDto.getStartDate());
                courseToUpdate.setEndDate(courseDto.getEndDate());
            } else if (courseDto.getStartDate() == null && courseDto.getEndDate() != null) {
                throw new IllegalArgumentException("Course Start date cannot be null if Course End date is passed");
            }
            if (courseDto.getStudentIds() != null) {
                updateCourseStudents(courseToUpdate, courseDto.getStudentIds());
            }

            // Handle faculty assignments
            if (courseDto.getFacultyMemberIds() != null) {
                updateCourseFaculty(courseToUpdate, courseDto.getFacultyMemberIds());
            }

            courseToUpdate.setUpdatedDate(new Date());

        } catch (IllegalArgumentException illegalArgumentException) {
            exceptionHandlingService.handleException(illegalArgumentException);
            throw new IllegalArgumentException(illegalArgumentException.getMessage());
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            throw new Exception(exception.getMessage());
        }
    }

    @Transactional
    public Course updateCourse(Long courseId, CourseDto courseDto) throws Exception {
        Course courseToUpdate = entityManager.find(Course.class,courseId);
        if(courseToUpdate ==null)
        {
            throw new IllegalArgumentException("Course with id " + courseId+ " does not found");
        }
        validateAndSaveCourseForUpdate(courseDto,courseToUpdate);
        return entityManager.merge(courseToUpdate);
    }

    @Transactional
    private void updateCourseStudents(Course course, List<Long> studentIds) {
        List<Student> currentStudents = new ArrayList<>(course.getStudents());
        List<Student> studentsToRemove = currentStudents.stream()
                .filter(student -> studentIds == null || !studentIds.contains(student.getId()))
                .collect(Collectors.toList());
        if (studentIds != null && !studentIds.isEmpty()) {
            List<Student> studentsToAdd = entityManager.createQuery(
                            "SELECT s FROM Student s WHERE s.id IN :studentIds", Student.class)
                    .setParameter("studentIds", studentIds)
                    .getResultList();
            if (studentsToAdd.size() != studentIds.size()) {
                List<Long> foundIds = studentsToAdd.stream()
                        .map(Student::getId)
                        .collect(Collectors.toList());

                List<Long> invalidIds = studentIds.stream()
                        .filter(id -> !foundIds.contains(id))
                        .collect(Collectors.toList());

                throw new IllegalArgumentException("The following student IDs are invalid: " + invalidIds);
            }

            // Remove students from course and its faculty
            for (Student studentToRemove : studentsToRemove) {
                // Remove from course
                studentToRemove.getCourses().remove(course);
                course.getStudents().remove(studentToRemove);

                // Remove from faculty associated with this course
                List<Faculty> courseFaculty = course.getFacultyMembers();
                for (Faculty faculty : courseFaculty) {
                    faculty.getStudents().remove(studentToRemove);
                    studentToRemove.getFacultyMembers().remove(faculty);
                    entityManager.merge(faculty);
                }
                entityManager.merge(studentToRemove);
            }

            // Add new students to course
            for (Student student : studentsToAdd) {
                if (!course.getStudents().contains(student)) {
                    course.getStudents().add(student);
                    student.getCourses().add(course);
                }

                // Automatically map faculty teaching this course with new students
                List<Faculty> courseFaculty = course.getFacultyMembers();
                for (Faculty faculty : courseFaculty) {
                    if (!faculty.getStudents().contains(student)) {
                        faculty.getStudents().add(student);
                    }
                    if (!student.getFacultyMembers().contains(faculty)) {
                        student.getFacultyMembers().add(faculty);
                    }
                    entityManager.merge(faculty);
                }
            }
            entityManager.merge(course);
        } else {
            // If no students are provided, remove all students from course and its faculty
            for (Student studentToRemove : currentStudents) {
                // Remove from course
                studentToRemove.getCourses().remove(course);
                course.getStudents().remove(studentToRemove);

                // Remove from faculty associated with this course
                List<Faculty> courseFaculty = course.getFacultyMembers();
                for (Faculty faculty : courseFaculty) {
                    faculty.getStudents().remove(studentToRemove);
                    studentToRemove.getFacultyMembers().remove(faculty);
                    entityManager.merge(faculty);
                }
                entityManager.merge(studentToRemove);
            }
            entityManager.merge(course);
        }
    }

    @Transactional
    private void updateCourseFaculty(Course course, List<Long> facultyIds) {
        List<Faculty> currentFaculty = new ArrayList<>(course.getFacultyMembers());
        List<Faculty> facultyToRemove = currentFaculty.stream()
                .filter(faculty -> facultyIds == null || !facultyIds.contains(faculty.getId()))
                .collect(Collectors.toList());
        if (facultyIds != null && !facultyIds.isEmpty()) {
            List<Faculty> facultyToAdd = entityManager.createQuery(
                            "SELECT f FROM Faculty f WHERE f.id IN :facultyIds", Faculty.class)
                    .setParameter("facultyIds", facultyIds)
                    .getResultList();
            if (facultyToAdd.size() != facultyIds.size()) {
                List<Long> foundIds = facultyToAdd.stream()
                        .map(Faculty::getId)
                        .collect(Collectors.toList());

                List<Long> invalidIds = facultyIds.stream()
                        .filter(id -> !foundIds.contains(id))
                        .collect(Collectors.toList());

                throw new IllegalArgumentException("The following faculty IDs not found: " + invalidIds);
            }

            // Remove faculty from course and its students
            for (Faculty facultyMemberToRemove : facultyToRemove) {
                // Remove from course
                facultyMemberToRemove.getCourses().remove(course);
                course.getFacultyMembers().remove(facultyMemberToRemove);

                // Remove from students associated with this course
                List<Student> courseStudents = course.getStudents();
                for (Student student : courseStudents) {
                    facultyMemberToRemove.getStudents().remove(student);
                    student.getFacultyMembers().remove(facultyMemberToRemove);
                    entityManager.merge(student);
                }
                entityManager.merge(facultyMemberToRemove);
            }
            for (Faculty faculty : facultyToAdd) {
                if (!course.getFacultyMembers().contains(faculty)) {
                    course.getFacultyMembers().add(faculty);
                    faculty.getCourses().add(course);
                }

                // Automatically map students in this course with new faculty
                List<Student> courseStudents = course.getStudents();
                for (Student student : courseStudents) {
                    if (!faculty.getStudents().contains(student)) {
                        faculty.getStudents().add(student);
                    }
                    if (!student.getFacultyMembers().contains(faculty)) {
                        student.getFacultyMembers().add(faculty);
                    }
                    entityManager.merge(student);
                }
                entityManager.merge(faculty);
            }
            entityManager.merge(course);
        } else {
            // If no faculty are provided, remove all faculty from course and its students
            for (Faculty facultyMemberToRemove : currentFaculty) {
                // Remove from course
                facultyMemberToRemove.getCourses().remove(course);
                course.getFacultyMembers().remove(facultyMemberToRemove);

                // Remove from students associated with this course
                List<Student> courseStudents = course.getStudents();
                for (Student student : courseStudents) {
                    facultyMemberToRemove.getStudents().remove(student);
                    student.getFacultyMembers().remove(facultyMemberToRemove);
                    entityManager.merge(student);
                }
                entityManager.merge(facultyMemberToRemove);
            }
            entityManager.merge(course);
        }
    }

    @Transactional
    public List<Course> courseFilter(
            Long courseId,
            Long userId,
            Long roleId,
            Long semesterId,
            Long degreeId
    ) throws Exception {
        try {
            StringBuilder jpql = new StringBuilder(
                    "SELECT DISTINCT c FROM Course c " +
                            "JOIN c.courseSemesterDegrees csd " +
                            "WHERE 1=1 "
            );

            // Prepare parameters map
            Map<String, Object> params = new HashMap<>();

            // Add filters based on input parameters
            if (courseId != null) {
                jpql.append("AND c.courseId = :courseId ");
                params.put("courseId", courseId);
            }

            if (semesterId != null) {
                jpql.append("AND csd.semester.semesterId = :semesterId ");
                params.put("semesterId", semesterId);
            }

            if (degreeId != null) {
                jpql.append("AND csd.academicDegree.degreeId = :degreeId ");
                params.put("degreeId", degreeId);
            }

            // Order by course ID
            jpql.append("ORDER BY c.courseId ASC");

            // Create the query
            TypedQuery<Course> query = entityManager.createQuery(jpql.toString(), Course.class);

            // Set parameters
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                query.setParameter(entry.getKey(), entry.getValue());
            }

            return query.getResultList();

        } catch (PersistenceException persistenceException) {
            exceptionHandlingService.handleException(persistenceException);
            throw new PersistenceException(persistenceException.getMessage());
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            throw new Exception(exception.getMessage());
        }
    }
}
