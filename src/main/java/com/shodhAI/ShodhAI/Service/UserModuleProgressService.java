package com.shodhAI.ShodhAI.Service;

import com.shodhAI.ShodhAI.Entity.Module;
import com.shodhAI.ShodhAI.Entity.Role;
import com.shodhAI.ShodhAI.Entity.UserCourseProgress;
import com.shodhAI.ShodhAI.Entity.UserModuleProgress;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

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

}
