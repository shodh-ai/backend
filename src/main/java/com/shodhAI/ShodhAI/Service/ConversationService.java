package com.shodhAI.ShodhAI.Service;

import com.shodhAI.ShodhAI.Dto.ConversationDto;
import com.shodhAI.ShodhAI.Entity.Conversation;
import com.shodhAI.ShodhAI.Entity.Role;
import com.shodhAI.ShodhAI.Entity.Session;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class ConversationService {

    @Autowired
    EntityManager entityManager;

    @Autowired
    RoleService roleService;

    @Autowired
    SessionService sessionService;

    @Autowired
    ExceptionHandlingService exceptionHandlingService;

    public void validateConversation(ConversationDto conversationDto) throws Exception {
        try {

            if (conversationDto.getSessionId() == null || conversationDto.getSessionId() <= 0) {
                throw new IllegalArgumentException("Session Id cannot be null or <= 0");
            }
            if (conversationDto.getUserDialogue() == null || conversationDto.getUserDialogue().isEmpty()) {
                throw new IllegalArgumentException("User Text cannot be null or empty>");
            }
            conversationDto.setUserDialogue(conversationDto.getUserDialogue().trim());

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

            Conversation conversation = new Conversation();
            Date currentDate = new Date();

            Role role = roleService.getRoleById(roleId);
            List<Session> session = sessionService.sessionFilter(conversationDto.getSessionId(), null, null, null, null);

            if (session.isEmpty()) {
                throw new IllegalArgumentException("No Session found with this session Id");
            }
            conversation.setSession(session.get(0));
            conversation.setUserDialogueTimestamp(currentDate);
            conversation.setUserDialogue(conversationDto.getUserDialogue());

            // ml-api integration


            conversation.setUserId(userId);
            conversation.setUserRole(role);

            return entityManager.merge(conversation);

        } catch (PersistenceException persistenceException) {
            exceptionHandlingService.handleException(persistenceException);
            throw new PersistenceException(persistenceException.getMessage());
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            throw new Exception(exception.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public List<Conversation> conversationFilter(Long sessionId, Long userId, Long roleId) throws Exception {
        try {

            StringBuilder jpql = new StringBuilder("SELECT c FROM Conversation c WHERE 1=1 ");

            if (sessionId != null) {
                jpql.append("AND c.session.id = :sessionId ");
            }
            if (userId != null && roleId != null) {
                jpql.append("AND c.userId = :userId ");
                jpql.append("AND c.userRole.id = :roleId ");
            }

            // Create the query
            TypedQuery<Conversation> query = entityManager.createQuery(jpql.toString(), Conversation.class);

            // Set parameters
            if (sessionId != null) {
                query.setParameter("sessionId", sessionId);
            }
            if (userId != null && roleId != null) {
                query.setParameter("userId", userId);
                query.setParameter("roleId", roleId);
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
