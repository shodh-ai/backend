package com.shodhAI.ShodhAI.Service;

import com.shodhAI.ShodhAI.Entity.Role;
import com.shodhAI.ShodhAI.Entity.Topic;
import com.shodhAI.ShodhAI.Entity.UserSubComponentProgress;
import com.shodhAI.ShodhAI.Entity.UserSubTopicProgress;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class UserSubComponentProgressService {

    @Autowired
    EntityManager entityManager;

    @Autowired
    ExceptionHandlingService exceptionHandlingService;

    @Autowired
    RoleService roleService;

    public void validateUserSubComponentProgress(Long subtopicId, String subComponentName) throws Exception {
        try {

            // TODO Needs more validation on subComponentName
            if (subComponentName == null || subComponentName.isEmpty()) {
                throw new IllegalArgumentException("Topic title cannot be null or empty");
            }

            if (subtopicId == null || subtopicId <= 0) {
                throw new IllegalArgumentException(("Sub topic Id cannot be null or <= 0"));
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
    public UserSubComponentProgress saveUserSubComponentProgress(Long userId, Long roleId, UserSubTopicProgress userSubTopicProgress, Topic subTopic, String subComponentName) throws Exception {
        try {

            List<UserSubComponentProgress> userSubComponentProgressList = getUserSubComponentProgressFilter(null, userId, roleId, subTopic.getTopicId(), subComponentName);
            if(!userSubComponentProgressList.isEmpty()) {
                return userSubComponentProgressList.get(0);
            }
            UserSubComponentProgress userSubComponentProgress = new UserSubComponentProgress();
            userSubComponentProgress.setUserId(userId);
            userSubComponentProgress.setSubComponentName(subComponentName);
            userSubComponentProgress.setSubTopic(subTopic);

            Role role = roleService.getRoleById(roleId);
            userSubComponentProgress.setRole(role);

            Date currentDate = new Date();
            userSubComponentProgress.setCreatedDate(currentDate);
            userSubComponentProgress.setUpdatedDate(currentDate);
            userSubComponentProgress.setUserSubTopicProgress(userSubTopicProgress);

            return entityManager.merge(userSubComponentProgress);

        } catch (PersistenceException persistenceException) {
            exceptionHandlingService.handleException(persistenceException);
            throw new PersistenceException(persistenceException.getMessage());
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            throw new Exception(exception.getMessage());
        }
    }

    @Transactional
    public List<UserSubComponentProgress> getUserSubComponentProgressFilter(Long userSubComponentProgressId, Long userId, Long roleId, Long subTopicId, String subComponentName) throws Exception {
        try {

            StringBuilder jpql = new StringBuilder("SELECT u FROM UserSubComponentProgress u WHERE 1=1 ");

            if (userSubComponentProgressId != null) {
                jpql.append("AND u.id = :userSubComponentProgressId ");
            }
            if (userId != null && roleId != null) {
                jpql.append("AND u.userId = :userId ");
                jpql.append("AND u.role.id = :roleId ");
            }
            if (subTopicId != null) {
                jpql.append("AND u.subTopic.id = :subTopicId ");
            }
            if (subComponentName != null) {
                jpql.append("AND u.subComponentName = :subComponentName ");
            }

            TypedQuery<UserSubComponentProgress> query = entityManager.createQuery(jpql.toString(), UserSubComponentProgress.class);

            if (userSubComponentProgressId != null) {
                query.setParameter("userSubComponentProgressId", userSubComponentProgressId);
            }
            if (userId != null && roleId != null) {
                query.setParameter("userId", userId);
                query.setParameter("roleId", roleId);
            }
            if (subTopicId != null) {
                query.setParameter("subTopicId", subTopicId);
            }
            if (subComponentName != null) {
                query.setParameter("subComponentName", subComponentName);
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
    public UserSubComponentProgress updateUserSubComponentProgress(UserSubComponentProgress userSubComponentProgress, Boolean isCompleted) throws Exception {
        try {

            userSubComponentProgress.setCompleted(isCompleted);
            return entityManager.merge(userSubComponentProgress);

        } catch (PersistenceException persistenceException) {
            exceptionHandlingService.handleException(persistenceException);
            throw new PersistenceException(persistenceException.getMessage());
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            throw new Exception(exception.getMessage());
        }
    }

}
