package com.shodhAI.ShodhAI.Service;

import com.shodhAI.ShodhAI.Component.Constant;
import com.shodhAI.ShodhAI.Dto.QuestionMaterialDto;
import com.shodhAI.ShodhAI.Dto.QuestionRequestDto;
import com.shodhAI.ShodhAI.Dto.QuestionResponseDto;
import com.shodhAI.ShodhAI.Entity.Content;
import com.shodhAI.ShodhAI.Entity.Module;
import com.shodhAI.ShodhAI.Entity.Question;
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
public class QuestionService {

    @Autowired
    EntityManager entityManager;

    @Autowired
    ExceptionHandlingService exceptionHandlingService;

    @Autowired
    ModuleService moduleService;

    @Autowired
    TopicService topicService;

    @Autowired
    FileTypeService fileTypeService;

    public void validateQuestion(QuestionRequestDto questionRequestDto) throws Exception {
        try {
            /*if(questionRequestDto.getQuestionMaterial().isEmpty() || questionRequestDto.getQuestionMaterial() == null) {
                throw new IllegalArgumentException("Question Material Dto cannot be null or empty");
            }*/

            for (QuestionMaterialDto questionMaterialDto: questionRequestDto.getQuestionMaterial()) {
                fileTypeService.getFileTypeById(questionMaterialDto.getFileTypeId());

                if(questionMaterialDto.getUrl() == null || questionMaterialDto.getUrl().trim().isEmpty()) {
                    throw new IllegalArgumentException("url cannot be cannot be null or empty");
                }
                questionMaterialDto.setUrl(questionMaterialDto.getUrl().trim());
            }

        } catch (IllegalArgumentException illegalArgumentException) {
            exceptionHandlingService.handleException(illegalArgumentException);
            throw new IllegalArgumentException(illegalArgumentException.getMessage());
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            throw new Exception(exception.getMessage());
        }
    }

    public Module validateModule(QuestionRequestDto questionRequestDto) throws Exception {
        try {
            if (questionRequestDto.getModuleId() == null) {
                throw new IllegalArgumentException("Module id name cannot be null");
            }
            return moduleService.getModuleById(questionRequestDto.getModuleId());
        } catch (IllegalArgumentException illegalArgumentException) {
            exceptionHandlingService.handleException(illegalArgumentException);
            throw new IllegalArgumentException(illegalArgumentException.getMessage());
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            throw new Exception(exception.getMessage());
        }
    }

    public Topic validateTopic(QuestionRequestDto questionRequestDto) throws Exception {
        try {
            if(questionRequestDto.getTopicId() == null) {
                throw new IllegalArgumentException("Topic id cannot be null");
            }
            return topicService.getTopicById(questionRequestDto.getTopicId());

        } catch (IllegalArgumentException illegalArgumentException) {
            exceptionHandlingService.handleException(illegalArgumentException);
            throw new IllegalArgumentException(illegalArgumentException.getMessage());
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            throw new Exception(exception.getMessage());
        }
    }

    @Transactional
    public Question saveQuestion(QuestionResponseDto questionResponseDto, Question question) throws Exception {
        try {

            Date currentDate = new Date();
            question.setCreatedDate(currentDate);
            question.setUpdatedDate(currentDate);

            question.setQuestion(questionResponseDto.getQuestion());
            question.setAnswer(question.getAnswer());
            question.setCognitiveDomain(question.getQuestion());
            question.setHints(question.getHints());

            return entityManager.merge(question);

        } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
            exceptionHandlingService.handleException(indexOutOfBoundsException);
            throw new IndexOutOfBoundsException(indexOutOfBoundsException.getMessage());
        } catch (PersistenceException persistenceException) {
            exceptionHandlingService.handleException(persistenceException);
            throw new PersistenceException(persistenceException.getMessage());
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            throw new Exception(exception.getMessage());
        }
    }

    @Transactional
    public List<Question> getQuestionByTopic(Topic topic) throws Exception {
        try {

            TypedQuery<Question> query = entityManager.createQuery(Constant.GET_QUESTION_BY_TOPIC, Question.class);
            query.setParameter("topic", topic);
            return query.getResultList();

        } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
            exceptionHandlingService.handleException(indexOutOfBoundsException);
            throw new IndexOutOfBoundsException("Question not found with given Id");
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            throw new Exception(exception);
        }
    }

}
