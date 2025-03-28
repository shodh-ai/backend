package com.shodhAI.ShodhAI.Service;

import com.shodhAI.ShodhAI.Entity.Course;
import com.shodhAI.ShodhAI.Entity.Role;
import com.shodhAI.ShodhAI.Entity.UserCourseProgress;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

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

    @Transactional
    public UserCourseProgress saveUserCourseProgress(Long userId, Long roleId, Long courseId) throws Exception {
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
