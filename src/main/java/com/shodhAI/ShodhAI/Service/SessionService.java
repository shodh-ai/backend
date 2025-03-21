package com.shodhAI.ShodhAI.Service;

import com.shodhAI.ShodhAI.Dto.SessionDto;
import com.shodhAI.ShodhAI.Entity.QuestionType;
import com.shodhAI.ShodhAI.Entity.Role;
import com.shodhAI.ShodhAI.Entity.Session;
import com.shodhAI.ShodhAI.Entity.Topic;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class SessionService {

    @Autowired
    EntityManager entityManager;

    @Autowired
    TopicService topicService;

    @Autowired
    RoleService roleService;

    @Autowired
    QuestionTypeService questionTypeService;

    @Autowired
    ExceptionHandlingService exceptionHandlingService;

    public void validateSession(SessionDto sessionDto) throws Exception {
        try {

            if (sessionDto.getTopicId() == null || sessionDto.getTopicId() <= 0) {
                throw new IllegalArgumentException("Topic Id cannot be null or <= 0");
            }
            if (sessionDto.getQuestionTypeId() != null && sessionDto.getQuestionTypeId() <= 0) {
                throw new IllegalArgumentException("Question Type Id cannot be null or <= 0");
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
    public Session saveSession(Long userId, Long roleId, SessionDto sessionDto) throws Exception {
        try {

            Session session = new Session();
            Date currentDate = new Date();

            Role role = roleService.getRoleById(roleId);
            Topic topic = topicService.getTopicById(sessionDto.getTopicId());
            QuestionType questionType = null;
            if(sessionDto.getQuestionTypeId() != null) {
                questionType = questionTypeService.getQuestionTypeById(sessionDto.getQuestionTypeId());
            }

            session.setTopic(topic);
            session.setQuestionType(questionType);
            session.setStartTime(currentDate);
            session.setUserId(userId);
            session.setUserRole(role);

            return entityManager.merge(session);

        } catch (PersistenceException persistenceException) {
            exceptionHandlingService.handleException(persistenceException);
            throw new PersistenceException(persistenceException.getMessage());
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            throw new Exception(exception.getMessage());
        }
    }

    @Transactional
    public List<Session> sessionFilter(Long sessionId, Long userId, Long roleId, Long topicId, Long questionTypeId) throws Exception {
        try {

            StringBuilder jpql = new StringBuilder("SELECT s FROM Session s WHERE 1=1 ");

            if (sessionId != null) {
                jpql.append("AND s.id = :sessionId ");
            }
            if (userId != null && roleId != null) {
                jpql.append("AND s.userId = :userId ");
                jpql.append("AND s.userRole.id = :roleId ");
            }
            if (topicId != null) {
                jpql.append("AND s.topic.id = :topicId ");
            }
            if (questionTypeId != null) {
                jpql.append("AND s.questionType.id = :questionTypeId ");
            }

            // Add ORDER BY clause to sort by sessionId
            jpql.append("ORDER BY s.id ASC");

            // Create the query
            TypedQuery<Session> query = entityManager.createQuery(jpql.toString(), Session.class);

            // Set parameters
            if (sessionId != null) {
                query.setParameter("sessionId", sessionId);
            }
            if (userId != null && roleId != null) {
                query.setParameter("userId", userId);
                query.setParameter("roleId", roleId);
            }
            if (topicId != null) {
                query.setParameter("topicId", topicId);
            }
            if (questionTypeId != null) {
                query.setParameter("questionTypeId", questionTypeId);
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