package com.shodhAI.ShodhAI.Service;

import com.shodhAI.ShodhAI.Component.Constant;
import com.shodhAI.ShodhAI.Dto.QuestionMaterialDto;
import com.shodhAI.ShodhAI.Dto.QuestionRequestDto;
import com.shodhAI.ShodhAI.Dto.QuestionResponseDto;
import com.shodhAI.ShodhAI.Entity.Hint;
import com.shodhAI.ShodhAI.Entity.Module;
import com.shodhAI.ShodhAI.Entity.Question;
import com.shodhAI.ShodhAI.Entity.Topic;
import com.shodhAI.ShodhAI.Entity.TopicType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
            if (questionRequestDto.getQuestionMaterial() == null) {
                return;
            }
            for (QuestionMaterialDto questionMaterialDto : questionRequestDto.getQuestionMaterial()) {
                fileTypeService.getFileTypeById(questionMaterialDto.getFileTypeId());

                if (questionMaterialDto.getUrl() == null || questionMaterialDto.getUrl().trim().isEmpty()) {
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
            if (questionRequestDto.getTopicId() == null) {
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
//            question.setAnswer(question.getAnswer());
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

    @Transactional(readOnly = true)
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

    public void validateAssignmentQuestionByTopic(Topic topic) throws Exception {
        try {
            TopicType topicType = topic.getTopicType();

            if (!topicType.getTopicTypeName().equalsIgnoreCase(Constant.GET_TOPIC_TYPE_ASSIGNMENT)) {
                throw new IllegalArgumentException("The topic type is not ASSIGNMENT");
            }

        } catch (IllegalArgumentException illegalArgumentException) {
            exceptionHandlingService.handleException(illegalArgumentException);
            throw new IllegalArgumentException(illegalArgumentException.getMessage());
        } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
            exceptionHandlingService.handleException(indexOutOfBoundsException);
            throw new IndexOutOfBoundsException("Question not found with given Id");
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            throw new Exception(exception);
        }
    }

    public void validatePracticeQuestionByTopic(Topic topic) throws Exception {
        try {
            TopicType topicType = topic.getTopicType();

            if (!topicType.getTopicTypeName().equalsIgnoreCase(Constant.GET_TOPIC_TYPE_TEACHING)) {
                throw new IllegalArgumentException("The topic type is not TEACHING to get Practice Question");
            }

        } catch (IllegalArgumentException illegalArgumentException) {
            exceptionHandlingService.handleException(illegalArgumentException);
            throw new IllegalArgumentException(illegalArgumentException.getMessage());
        } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
            exceptionHandlingService.handleException(indexOutOfBoundsException);
            throw new IndexOutOfBoundsException("Question not found with given Id");
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            throw new Exception(exception);
        }
    }

    @Transactional
    public List<Question> saveQuestionList(List<QuestionResponseDto> questionResponseDtoList, Topic topic) throws Exception {
        try {

            List<Question> questionList = new ArrayList<>();
            Date currentDate = new Date();
            for (QuestionResponseDto questionResponseDto : questionResponseDtoList) {
                // Create a new Question object to save
                Question question = new Question();
                question.setQuestion(questionResponseDto.getQuestion());
//                question.setAnswer(questionResponseDto.getAnswer());
                question.setCognitiveDomain(questionResponseDto.getCognitiveDomain());
                question.setTopic(topic);
                // Process hints and add to the question
                List<Hint> hints = new ArrayList<>();
                for (String hintText : questionResponseDto.getHints()) {
                    Hint hint = new Hint();
                    hint.setLevel("basic");  // You can further improve by categorizing hints (basic, advanced, etc.)
                    hint.setText(hintText);
                    hints.add(hint);
                }
                question.setHints(hints);
//
//                 Save the question object in the database (assuming saveQuestion is a method that stores it)
                question.setCreatedDate(currentDate);
                question.setUpdatedDate(currentDate);
                entityManager.merge(question);
                questionList.add(question);
            }
            return questionList;

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

    @Transactional(readOnly = true)
    public List<Question> questionFilter(Topic topic, Long questionTypeId) throws Exception {
        try {

            StringBuilder jpql = new StringBuilder("SELECT s FROM Question s WHERE 1=1 ");

            if (topic != null) {
                jpql.append("AND s.topic.id = :topicId ");
            }
            if (questionTypeId != null) {
                jpql.append("AND s.questionType.id = :questionTypeId ");
            }

            // Create the query
            TypedQuery<Question> query = entityManager.createQuery(jpql.toString(), Question.class);

            if (topic != null) {
                query.setParameter("topicId", topic.getTopicId());
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
