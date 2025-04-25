package com.shodhAI.ShodhAI.Service;

import com.shodhAI.ShodhAI.Entity.Role;
import com.shodhAI.ShodhAI.Entity.Topic;
import com.shodhAI.ShodhAI.Entity.UserSubComponentProgress;
import com.shodhAI.ShodhAI.Entity.UserSubTopicProgress;
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
public class UserSubTopicProgressService {

    @Autowired
    EntityManager entityManager;

    @Autowired
    ExceptionHandlingService exceptionHandlingService;

    @Autowired
    RoleService roleService;

    @Autowired
    TopicService topicService;

    public void validateUserSubTopicProgress(Long subtopicId) throws Exception {
        try {

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
    public UserSubTopicProgress saveUserSubTopicProgress(Long userId, Long roleId, Long topicId, UserTopicProgress userTopicProgress) throws Exception {
        try {

            UserSubTopicProgress userSubTopicProgress = new UserSubTopicProgress();
            userSubTopicProgress.setUserId(userId);

            Role role = roleService.getRoleById(roleId);
            userSubTopicProgress.setRole(role);

            Topic subTopic = topicService.getTopicById(topicId);
            userSubTopicProgress.setSubTopic(subTopic);

            Date currentDate = new Date();
            userSubTopicProgress.setCreatedDate(currentDate);
            userSubTopicProgress.setUpdatedDate(currentDate);
            userSubTopicProgress.setUserTopicProgress(userTopicProgress);

            return entityManager.merge(userSubTopicProgress);

        } catch (PersistenceException persistenceException) {
            exceptionHandlingService.handleException(persistenceException);
            throw new PersistenceException(persistenceException.getMessage());
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            throw new Exception(exception.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public List<UserSubTopicProgress> getUserSubTopicProgressFilter(Long userSubTopicProgressId, Long userId, Long roleId, Long subTopicId) throws Exception {
        try {

            StringBuilder jpql = new StringBuilder("SELECT u FROM UserSubTopicProgress u WHERE 1=1 ");

            if (userSubTopicProgressId != null) {
                jpql.append("AND u.id = :userSubTopicProgressId ");
            }
            if (userId != null && roleId != null) {
                jpql.append("AND u.userId = :userId ");
                jpql.append("AND u.role.id = :roleId ");
            }
            if (subTopicId != null) {
                jpql.append("AND u.subTopic.id = :subTopicId ");
            }

            TypedQuery<UserSubTopicProgress> query = entityManager.createQuery(jpql.toString(), UserSubTopicProgress.class);

            if (userSubTopicProgressId != null) {
                query.setParameter("userSubTopicProgressId", userSubTopicProgressId);
            }
            if (userId != null && roleId != null) {
                query.setParameter("userId", userId);
                query.setParameter("roleId", roleId);
            }
            if (subTopicId != null) {
                query.setParameter("subTopicId", subTopicId);
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
    public UserSubTopicProgress updateUserSubTopicProgress(UserSubTopicProgress userSubTopicProgress, Boolean isCompleted) throws Exception {
        try {

            userSubTopicProgress.setCompleted(isCompleted);
            return entityManager.merge(userSubTopicProgress);

        } catch (PersistenceException persistenceException) {
            exceptionHandlingService.handleException(persistenceException);
            throw new PersistenceException(persistenceException.getMessage());
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            throw new Exception(exception.getMessage());
        }
    }

}
