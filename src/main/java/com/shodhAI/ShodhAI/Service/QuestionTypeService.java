package com.shodhAI.ShodhAI.Service;

import com.shodhAI.ShodhAI.Component.Constant;
import com.shodhAI.ShodhAI.Entity.QuestionType;
import com.shodhAI.ShodhAI.Entity.QuestionType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class QuestionTypeService {

    @Autowired
    EntityManager entityManager;

    @Autowired
    ExceptionHandlingService exceptionHandlingService;

    public List<QuestionType> getAllQuestionTypes() throws Exception {
        try {

            TypedQuery<QuestionType> query = entityManager.createQuery(Constant.GET_ALL_QUESTION_TYPE, QuestionType.class);
            return query.getResultList();

        } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
            exceptionHandlingService.handleException(indexOutOfBoundsException);
            throw new IndexOutOfBoundsException(indexOutOfBoundsException.getMessage());
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            throw new Exception(exception);
        }
    }

    public QuestionType getQuestionTypeById(Long questionTypeId) throws Exception {
        try {

            TypedQuery<QuestionType> query = entityManager.createQuery(Constant.GET_QUESTION_TYPE_BY_ID, QuestionType.class);
            query.setParameter("questionTypeId", questionTypeId);
            return query.getResultList().get(0);

        } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
            exceptionHandlingService.handleException(indexOutOfBoundsException);
            throw new IndexOutOfBoundsException(indexOutOfBoundsException.getMessage());
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            throw new Exception(exception);
        }
    }
    @Transactional
    public QuestionType addQuestionType(QuestionType questionType) throws Exception {
        try
        {
            if(questionType.getQuestionType()==null|| questionType.getQuestionType().trim().isEmpty())
            {
                throw new IllegalArgumentException("Question type name cannot be null or empty");
            }
            List<QuestionType> questionTypes= getAllQuestionTypes();
            for(QuestionType questionTypeToGet : questionTypes)
            {
                if (questionTypeToGet.getQuestionType().equalsIgnoreCase(questionType.getQuestionType().trim()))
                {
                    throw new IllegalArgumentException("Question type already exists with name " + questionType.getQuestionType().trim());
                }
            }
            QuestionType questionTypeToAdd= new QuestionType();
            questionTypeToAdd.setQuestionType(questionType.getQuestionType().trim());
            questionTypeToAdd.setCreatedDate(new Date());
            entityManager.persist(questionTypeToAdd);
            return questionTypeToAdd;
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

    @Transactional
    public QuestionType deleteQuestionTypeById(Long questionTypeId) throws Exception {
        try {
            QuestionType questionTypeToDelete = entityManager.find(QuestionType.class, questionTypeId);
            if (questionTypeToDelete == null)
            {
                throw new IllegalArgumentException("Question type with id " + questionTypeId + " not found");
            }
            questionTypeToDelete.setArchived('Y');
            entityManager.merge(questionTypeToDelete);
            return questionTypeToDelete;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @Transactional
    public QuestionType updateQuestionType(Long questionTypeId, QuestionType questionType) throws Exception {
        try
        {
            QuestionType questionTypeToUpdate= entityManager.find(QuestionType.class,questionTypeId);
            if(questionTypeToUpdate==null)
            {
                throw new IllegalArgumentException("Question type with id "+ questionTypeId+ " not found");
            }
            if(questionType.getQuestionType()!=null)
            {
                if(questionType.getQuestionType().trim().isEmpty())
                {
                    throw new IllegalArgumentException("Question type name cannot be null or empty");
                }
                List<QuestionType> questionTypes= getAllQuestionTypes();
                for(QuestionType questionTypeToGet : questionTypes)
                {
                    if (!Objects.equals(questionTypeToGet.getQuestionTypeId(), questionTypeId) && questionTypeToGet.getQuestionType().equalsIgnoreCase(questionType.getQuestionType().trim()))
                    {
                        throw new IllegalArgumentException("Question type already exists with name " + questionType.getQuestionType().trim());
                    }
                }
                questionTypeToUpdate.setQuestionType(questionType.getQuestionType().trim());
            }
            questionTypeToUpdate.setUpdatedDate(new Date());
            entityManager.merge(questionTypeToUpdate);
            return questionTypeToUpdate;
        }
        catch (IllegalArgumentException illegalArgumentException)
        {
            throw new IllegalArgumentException(illegalArgumentException.getMessage());
        }
        catch (Exception exception)
        {
            throw new Exception(exception.getMessage());
        }
    }

    @Transactional
    public List<QuestionType> questionTypeFilter() throws Exception {
        try {
            String jpql = "SELECT f FROM QuestionType f WHERE f.archived = 'N' ORDER BY f.questionTypeId ASC";
            TypedQuery<QuestionType> query = entityManager.createQuery(jpql, QuestionType.class);
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
