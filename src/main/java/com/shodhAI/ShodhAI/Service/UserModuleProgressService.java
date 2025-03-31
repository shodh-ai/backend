package com.shodhAI.ShodhAI.Service;

import com.shodhAI.ShodhAI.Entity.Module;
import com.shodhAI.ShodhAI.Entity.Role;
import com.shodhAI.ShodhAI.Entity.UserCourseProgress;
import com.shodhAI.ShodhAI.Entity.UserModuleProgress;
import com.shodhAI.ShodhAI.Entity.UserTopicProgress;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class UserModuleProgressService {

    @Autowired
    EntityManager entityManager;

    @Autowired
    ExceptionHandlingService exceptionHandlingService;

    @Autowired
    RoleService roleService;

    @Autowired
    ModuleService moduleService;

    @Transactional
    public UserModuleProgress saveUserModuleProgress(Long userId, Long roleId, Long moduleId, UserCourseProgress userCourseProgress) throws Exception {
        try {

            UserModuleProgress userModuleProgress = new UserModuleProgress();
            userModuleProgress.setUserId(userId);

            Role role = roleService.getRoleById(roleId);
            userModuleProgress.setRole(role);

            Module module = moduleService.getModuleById(moduleId);
            userModuleProgress.setModule(module);

            Date currentDate = new Date();
            userModuleProgress.setCreatedDate(currentDate);
            userModuleProgress.setUpdatedDate(currentDate);
            userModuleProgress.setUserCourseProgress(userCourseProgress);

            return entityManager.merge(userModuleProgress);

        } catch (PersistenceException persistenceException) {
            exceptionHandlingService.handleException(persistenceException);
            throw new PersistenceException(persistenceException.getMessage());
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            throw new Exception(exception.getMessage());
        }
    }

    @Transactional
    public List<UserModuleProgress> getUserModuleProgressFilter(Long userModuleProgressId, Long userId, Long roleId, Long moduleId) throws Exception {
        try {

            StringBuilder jpql = new StringBuilder("SELECT u FROM UserModuleProgress u WHERE 1=1 ");

            if (userModuleProgressId != null) {
                jpql.append("AND u.id = :userModuleProgressId ");
            }
            if (userId != null && roleId != null) {
                jpql.append("AND u.userId = :userId ");
                jpql.append("AND u.role.id = :roleId ");
            }
            if (moduleId != null) {
                jpql.append("AND u.module.id = :moduleId ");
            }

            TypedQuery<UserModuleProgress> query = entityManager.createQuery(jpql.toString(), UserModuleProgress.class);

            if (userModuleProgressId != null) {
                query.setParameter("userModuleProgressId", userModuleProgressId);
            }
            if (userId != null && roleId != null) {
                query.setParameter("userId", userId);
                query.setParameter("roleId", roleId);
            }
            if (moduleId != null) {
                query.setParameter("moduleId", moduleId);
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
