package com.shodhAI.ShodhAI.Service;

import com.shodhAI.ShodhAI.Component.Constant;
import com.shodhAI.ShodhAI.Entity.TopicType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class TopicTypeService
{
    @Autowired
    private EntityManager entityManager;

    @Autowired
    ExceptionHandlingService exceptionHandlingService;

    @Transactional
    public TopicType addTopicType(TopicType topicType) throws Exception {
        try
        {
            if(topicType.getTopicTypeName()==null|| topicType.getTopicTypeName().trim().isEmpty())
            {
                throw new IllegalArgumentException("Topic type name cannot be null or empty");
            }
            List<TopicType> topicTypes= getAllTopicType();
            for(TopicType topicTypeToGet : topicTypes)
            {
                if (topicTypeToGet.getTopicTypeName().equalsIgnoreCase(topicType.getTopicTypeName().trim()))
                {
                    throw new IllegalArgumentException("Topic type already exists with name " + topicType.getTopicTypeName().trim());
                }
            }
            TopicType topicTypeToAdd= new TopicType();
            topicTypeToAdd.setTopicTypeName(topicType.getTopicTypeName().trim());
            topicTypeToAdd.setCreatedDate(new Date());
            entityManager.persist(topicTypeToAdd);
            return topicTypeToAdd;
        }
        catch (IllegalArgumentException illegalArgumentException)
        {
            exceptionHandlingService.handleException(illegalArgumentException);
            throw new IllegalArgumentException(illegalArgumentException.getMessage());
        }
        catch(Exception exception)
        {
            exceptionHandlingService.handleException(exception);
            throw new Exception(exception.getMessage());
        }
    }

    public TopicType getTopicTypeById(Long topicTypeId) throws Exception {
        try {
            TypedQuery<TopicType> query = entityManager.createQuery(Constant.GET_TOPIC_TYPE_BY_ID, TopicType.class);
            query.setParameter("topicTypeId", topicTypeId);
            return query.getResultList().get(0);

        } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
            exceptionHandlingService.handleException(indexOutOfBoundsException);
            throw new IndexOutOfBoundsException(indexOutOfBoundsException.getMessage());
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            throw new Exception(exception);
        }
    }

    public List<TopicType> getAllTopicType() throws Exception {
        try {
            TypedQuery<TopicType> query = entityManager.createQuery(Constant.GET_ALL_TOPIC_TYPE, TopicType.class);
            return query.getResultList();

        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            throw new Exception(exception.getMessage());
        }
    }
    @Transactional
    public TopicType deleteTopicTypeById(Long topicTypeId) throws Exception {
        try {
            TopicType topicTypeToDelete = entityManager.find(TopicType.class, topicTypeId);
            if (topicTypeToDelete == null)
            {
                throw new IllegalArgumentException("Topic type with id " + topicTypeId + " not found");
            }
            topicTypeToDelete.setArchived('Y');
            entityManager.merge(topicTypeToDelete);
            return topicTypeToDelete;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @Transactional
    public List<TopicType> topicTypeFilter() throws Exception {
        try {
            String jpql = "SELECT f FROM TopicType f WHERE f.archived = 'N' ORDER BY f.topicTypeId ASC";
            TypedQuery<TopicType> query = entityManager.createQuery(jpql, TopicType.class);
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
