package com.shodhAI.ShodhAI.Service;

import com.shodhAI.ShodhAI.Component.Constant;
import com.shodhAI.ShodhAI.Dto.CourseDto;
import com.shodhAI.ShodhAI.Entity.Course;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

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

}
