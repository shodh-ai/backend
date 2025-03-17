package com.shodhAI.ShodhAI.Service;

import com.shodhAI.ShodhAI.Dto.ConversationDto;
import com.shodhAI.ShodhAI.Dto.SessionDto;
import com.shodhAI.ShodhAI.Entity.Conversation;
import com.shodhAI.ShodhAI.Entity.QuestionType;
import com.shodhAI.ShodhAI.Entity.Role;
import com.shodhAI.ShodhAI.Entity.Session;
import com.shodhAI.ShodhAI.Entity.Topic;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class ConversationService {

    @Autowired
    EntityManager entityManager;

    @Autowired
    TopicService topicService;

    @Autowired
    RoleService roleService;

    @Autowired
    QuestionTypeService questionTypeService;

    @Autowired
    SessionService sessionService;

    @Autowired
    ExceptionHandlingService exceptionHandlingService;

    public void validateConversation(ConversationDto conversationDto) throws Exception {
        try {

            if (conversationDto.getSessionId() == null || conversationDto.getSessionId() <= 0) {
                throw new IllegalArgumentException("Session Id cannot be null or <= 0");
            }
            if (conversationDto.getUserText() == null || conversationDto.getUserText().isEmpty()) {
                throw new IllegalArgumentException("User Text cannot be null or empty>");
            }
            conversationDto.setUserText(conversationDto.getUserText().trim());

        } catch (IllegalArgumentException illegalArgumentException) {
            exceptionHandlingService.handleException(illegalArgumentException);
            throw new IllegalArgumentException(illegalArgumentException.getMessage());
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            throw new Exception(exception.getMessage());
        }

    }

    @Transactional
    public Conversation saveConversation(Long userId, Long roleId, ConversationDto conversationDto) throws Exception {
        try {

            /*Conversation conversation = new Conversation();
            Date currentDate = new Date();

            Role role = roleService.getRoleById(roleId);
            Session session = sessionService.

            session.setTopic(topic);
            session.setQuestionType(questionType);
            session.setStartTime(currentDate);
            session.setUserId(userId);
            session.setUserRole(role);

            return entityManager.merge(session);*/
            return null;

        } catch (PersistenceException persistenceException) {
            exceptionHandlingService.handleException(persistenceException);
            throw new PersistenceException(persistenceException.getMessage());
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            throw new Exception(exception.getMessage());
        }
    }

}
