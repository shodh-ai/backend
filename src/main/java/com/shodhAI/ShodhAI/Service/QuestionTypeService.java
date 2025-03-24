package com.shodhAI.ShodhAI.Service;

import com.shodhAI.ShodhAI.Component.Constant;
import com.shodhAI.ShodhAI.Entity.QuestionType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

}
