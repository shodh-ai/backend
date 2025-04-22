package com.shodhAI.ShodhAI.Service;

import com.shodhAI.ShodhAI.Entity.Role;
import com.shodhAI.ShodhAI.Entity.Topic;
import com.shodhAI.ShodhAI.Entity.UserModuleProgress;
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
public class UserTopicProgressService {

    @Autowired
    EntityManager entityManager;

    @Autowired
    ExceptionHandlingService exceptionHandlingService;

    @Autowired
    RoleService roleService;

    @Autowired
    TopicService topicService;

    public void validateUserTopicProgress(Long topicId) throws Exception {
        try {

            if (topicId == null || topicId <= 0) {
                throw new IllegalArgumentException(("Topic Id cannot be null or <= 0"));
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
    public UserTopicProgress saveUserTopicProgress(Long userId, Long roleId, Long topicId, UserModuleProgress userModuleProgress) throws Exception {
        try {

            UserTopicProgress userTopicProgress = new UserTopicProgress();
            userTopicProgress.setUserId(userId);

            Role role = roleService.getRoleById(roleId);
            userTopicProgress.setRole(role);

            Topic topic = topicService.getTopicById(topicId);
            userTopicProgress.setTopic(topic);

            Date currentDate = new Date();
            userTopicProgress.setCreatedDate(currentDate);
            userTopicProgress.setUpdatedDate(currentDate);
            userTopicProgress.setUserModuleProgress(userModuleProgress);

            return entityManager.merge(userTopicProgress);

        } catch (PersistenceException persistenceException) {
            exceptionHandlingService.handleException(persistenceException);
            throw new PersistenceException(persistenceException.getMessage());
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            throw new Exception(exception.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public List<UserTopicProgress> getUserTopicProgressFilter(Long userTopicProgressId, Long userId, Long roleId, Long topicId) throws Exception {
        try {

            StringBuilder jpql = new StringBuilder("SELECT u FROM UserTopicProgress u WHERE 1=1 ");

            if (userTopicProgressId != null) {
                jpql.append("AND u.id = :userTopicProgressId ");
            }
            if (userId != null && roleId != null) {
                jpql.append("AND u.userId = :userId ");
                jpql.append("AND u.role.id = :roleId ");
            }
            if (topicId != null) {
                jpql.append("AND u.topic.id = :topicId ");
            }

            TypedQuery<UserTopicProgress> query = entityManager.createQuery(jpql.toString(), UserTopicProgress.class);

            if (userTopicProgressId != null) {
                query.setParameter("userTopicProgressId", userTopicProgressId);
            }
            if (userId != null && roleId != null) {
                query.setParameter("userId", userId);
                query.setParameter("roleId", roleId);
            }
            if (topicId != null) {
                query.setParameter("topicId", topicId);
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
    public UserTopicProgress updateUserTopicProgress(UserTopicProgress userTopicProgress, Boolean isCompleted) throws Exception {
        try {

            userTopicProgress.setCompleted(isCompleted);
            return entityManager.merge(userTopicProgress);

        } catch (PersistenceException persistenceException) {
            exceptionHandlingService.handleException(persistenceException);
            throw new PersistenceException(persistenceException.getMessage());
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            throw new Exception(exception.getMessage());
        }
    }

}
