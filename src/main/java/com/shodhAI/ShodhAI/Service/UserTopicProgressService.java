package com.shodhAI.ShodhAI.Service;

import com.shodhAI.ShodhAI.Entity.Role;
import com.shodhAI.ShodhAI.Entity.Topic;
import com.shodhAI.ShodhAI.Entity.UserModuleProgress;
import com.shodhAI.ShodhAI.Entity.UserTopicProgress;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

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

}
