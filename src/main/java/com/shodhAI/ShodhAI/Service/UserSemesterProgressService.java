package com.shodhAI.ShodhAI.Service;

import com.shodhAI.ShodhAI.Entity.Course;
import com.shodhAI.ShodhAI.Entity.Role;
import com.shodhAI.ShodhAI.Entity.Semester;
import com.shodhAI.ShodhAI.Entity.UserCourseProgress;
import com.shodhAI.ShodhAI.Entity.UserSemesterProgress;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class UserSemesterProgressService {

    @Autowired
    EntityManager entityManager;

    @Autowired
    ExceptionHandlingService exceptionHandlingService;

    @Autowired
    RoleService roleService;

    @Autowired
    SemesterService semesterService;

    public void validateUserSemesterProgress(Long semesterId) throws Exception {
        try {

            if (semesterId == null ||semesterId <= 0) {
                throw new IllegalArgumentException(("Semester Id cannot be null or <= 0"));
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
    public UserSemesterProgress saveUserSemesterProgress(Long userId, Long roleId, Long semesterId) throws Exception {
        try {

            UserSemesterProgress userSemesterProgress = new UserSemesterProgress();
            userSemesterProgress.setUserId(userId);

            Role role = roleService.getRoleById(roleId);
            userSemesterProgress.setRole(role);

            Semester semester = semesterService.getSemesterById(semesterId);
            userSemesterProgress.setSemester(semester);

            Date currentDate = new Date();
            userSemesterProgress.setCreatedDate(currentDate);
            userSemesterProgress.setUpdatedDate(currentDate);

            return entityManager.merge(userSemesterProgress);

        } catch (PersistenceException persistenceException) {
            exceptionHandlingService.handleException(persistenceException);
            throw new PersistenceException(persistenceException.getMessage());
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            throw new Exception(exception.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public List<UserSemesterProgress> getUserSemesterProgressFilter(Long userSemesterProgressId, Long userId, Long roleId, Long semesterId) throws Exception {
        try {

            StringBuilder jpql = new StringBuilder("SELECT u FROM UserSemesterProgress u WHERE 1=1 ");

            if (userSemesterProgressId != null) {
                jpql.append("AND u.id = :userSemesterProgressId ");
            }
            if (userId != null && roleId != null) {
                jpql.append("AND u.userId = :userId ");
                jpql.append("AND u.role.id = :roleId ");
            }
            if (semesterId != null) {
                jpql.append("AND u.semester.id = :semesterId ");
            }

            TypedQuery<UserSemesterProgress> query = entityManager.createQuery(jpql.toString(), UserSemesterProgress.class);

            if (userSemesterProgressId != null) {
                query.setParameter("userSemesterProgressId", userSemesterProgressId);
            }
            if (userId != null && roleId != null) {
                query.setParameter("userId", userId);
                query.setParameter("roleId", roleId);
            }
            if (semesterId != null) {
                query.setParameter("semesterId", semesterId);
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
