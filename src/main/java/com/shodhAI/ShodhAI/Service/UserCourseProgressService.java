package com.shodhAI.ShodhAI.Service;

import com.shodhAI.ShodhAI.Entity.Course;
import com.shodhAI.ShodhAI.Entity.Role;
import com.shodhAI.ShodhAI.Entity.UserCourseProgress;
import com.shodhAI.ShodhAI.Entity.UserModuleProgress;
import com.shodhAI.ShodhAI.Entity.UserSemesterProgress;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class UserCourseProgressService {

    @Autowired
    EntityManager entityManager;

    @Autowired
    ExceptionHandlingService exceptionHandlingService;

    @Autowired
    RoleService roleService;

    @Autowired
    CourseService courseService;

    public void validateUserCourseProgress(Long courseId) throws Exception {
        try {

            if (courseId == null || courseId <= 0) {
                throw new IllegalArgumentException(("Course Id cannot be null or <= 0"));
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
    public UserCourseProgress saveUserCourseProgress(Long userId, Long roleId, Long courseId, UserSemesterProgress userSemesterProgress) throws Exception {
        try {

            UserCourseProgress userCourseProgress = new UserCourseProgress();
            userCourseProgress.setUserId(userId);

            Role role = roleService.getRoleById(roleId);
            userCourseProgress.setRole(role);

            Course course = courseService.getCourseById(courseId);
            userCourseProgress.setCourse(course);


            Date currentDate = new Date();
            userCourseProgress.setCreatedDate(currentDate);
            userCourseProgress.setUpdatedDate(currentDate);

            userCourseProgress.setUserSemesterProgress(userSemesterProgress);
            return entityManager.merge(userCourseProgress);

        } catch (PersistenceException persistenceException) {
            exceptionHandlingService.handleException(persistenceException);
            throw new PersistenceException(persistenceException.getMessage());
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            throw new Exception(exception.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public List<UserCourseProgress> getUserCourseProgressFilter(Long userCourseProgressId, Long userId, Long roleId, Long courseId) throws Exception {
        try {

            StringBuilder jpql = new StringBuilder("SELECT u FROM UserCourseProgress u WHERE 1=1 ");

            if (userCourseProgressId != null) {
                jpql.append("AND u.id = :userCourseProgressId ");
            }
            if (userId != null && roleId != null) {
                jpql.append("AND u.userId = :userId ");
                jpql.append("AND u.role.id = :roleId ");
            }
            if (courseId != null) {
                jpql.append("AND u.course.id = :courseId ");
            }

            TypedQuery<UserCourseProgress> query = entityManager.createQuery(jpql.toString(), UserCourseProgress.class);

            if (userCourseProgressId != null) {
                query.setParameter("userCourseProgressId", userCourseProgressId);
            }
            if (userId != null && roleId != null) {
                query.setParameter("userId", userId);
                query.setParameter("roleId", roleId);
            }
            if (courseId != null) {
                query.setParameter("courseId", courseId);
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

    @Transactional
    public UserCourseProgress updateUserCourseProgress(UserCourseProgress userCourseProgress, Boolean isCompleted) throws Exception {
        try {

            userCourseProgress.setCompleted(isCompleted);
            return entityManager.merge(userCourseProgress);

        } catch (PersistenceException persistenceException) {
            exceptionHandlingService.handleException(persistenceException);
            throw new PersistenceException(persistenceException.getMessage());
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            throw new Exception(exception.getMessage());
        }
    }

}
