package com.shodhAI.ShodhAI.Service;

import com.shodhAI.ShodhAI.Component.Constant;
import com.shodhAI.ShodhAI.Dto.AssignmentDto;
import com.shodhAI.ShodhAI.Entity.Assignment;
import com.shodhAI.ShodhAI.Entity.PriorityLevel;
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
public class AssignmentService {

    @Autowired
    ExceptionHandlingService exceptionHandlingService;

    @Autowired
    EntityManager entityManager;

    @Autowired
    PriorityLevelService priorityLevelService;

    @Autowired
    TopicService topicService;

    public void validateAssignment(AssignmentDto assignmentDto) throws Exception {
        try {
            if (assignmentDto.getAssignmentName() == null || assignmentDto.getAssignmentName().isEmpty()) {
                throw new IllegalArgumentException("Assignment name cannot be null or empty");
            }
            assignmentDto.setAssignmentName(assignmentDto.getAssignmentName().trim());

            if (assignmentDto.getAssignmentDescription() != null) {
                if (assignmentDto.getAssignmentDescription().isEmpty() || assignmentDto.getAssignmentDescription().trim().isEmpty()) {
                    throw new IllegalArgumentException("Assignment Description cannot be empty");
                }
                assignmentDto.setAssignmentDescription(assignmentDto.getAssignmentDescription().trim());
            }

            if (assignmentDto.getTopicId() == null || assignmentDto.getTopicId() <= 0) {
                throw new IllegalArgumentException(("Topic Id cannot be null or <= 0"));
            }
            if (assignmentDto.getPriorityLevelId() == null || assignmentDto.getPriorityLevelId() <= 0) {
                throw new IllegalArgumentException(("Priority Level cannot be null or <= 0"));
            }

            // Dates
            if (assignmentDto.getActiveStartDate() != null && assignmentDto.getActiveEndDate() != null) {
                if (!assignmentDto.getActiveStartDate().before(assignmentDto.getActiveEndDate())) {
                    throw new IllegalArgumentException("Assignment Start date must be before of end date");
                }
            } else if (assignmentDto.getActiveStartDate() == null && assignmentDto.getActiveEndDate() != null) {
                throw new IllegalArgumentException("Assignment Start date cannot be null if Assignment End date is passed");
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
    public Assignment saveAssignment(AssignmentDto assignmentDto) throws Exception {
        try {

            Topic topic = topicService.getTopicById(assignmentDto.getTopicId());
            PriorityLevel priorityLevel = priorityLevelService.getPriorityLevelById(assignmentDto.getPriorityLevelId());

            Assignment assignment = new Assignment();

            Date currentDate = new Date();

            assignment.setCreatedDate(currentDate);
            assignment.setUpdatedDate(currentDate);
            assignment.setAssignmentName(assignmentDto.getAssignmentName());
            assignment.setAssignmentDescription(assignmentDto.getAssignmentDescription());
            assignment.setActiveStartDate(assignmentDto.getActiveStartDate());
            assignment.setActiveEndDate(assignmentDto.getActiveEndDate());
            assignment.setPriorityLevel(priorityLevel);
            assignment.setTopic(topic);

            return entityManager.merge(assignment);

        } catch (PersistenceException persistenceException) {
            exceptionHandlingService.handleException(persistenceException);
            throw new PersistenceException(persistenceException.getMessage());
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            throw new Exception(exception.getMessage());
        }
    }

    public Assignment getAssignmentById(Long assignmentId) throws Exception {
        try {

            TypedQuery<Assignment> query = entityManager.createQuery(Constant.GET_ASSIGNMENT_BY_ID, Assignment.class);
            query.setParameter("assignmentId", assignmentId);
            return query.getResultList().get(0);

        } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
            exceptionHandlingService.handleException(indexOutOfBoundsException);
            throw new IndexOutOfBoundsException("Assignment not found with given Id");
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            throw new Exception(exception);
        }
    }

    public void validateAssignmentParameters(Long topicId) throws Exception {
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
    public List<Assignment> filterAssignments(Long topicId) throws Exception {
        try {
            // Initialize the JPQL query
            StringBuilder jpql = new StringBuilder("SELECT a FROM Assignment a WHERE 1=1 ");

            Topic topic = null;
            if (topicId != null) {
                topic = topicService.getTopicById(topicId);
                jpql.append("AND a.topic = :topic ");
            }

            // Create the query with the final JPQL string
            TypedQuery<Assignment> query = entityManager.createQuery(jpql.toString(), Assignment.class);

            if (query != null) {
                query.setParameter("topic", topic);
            }

            // Execute and return the result
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
