package com.shodhAI.ShodhAI.Controller;

import com.shodhAI.ShodhAI.Component.Constant;
import com.shodhAI.ShodhAI.Dto.QuestionRequestDto;
import com.shodhAI.ShodhAI.Dto.QuestionResponseDto;
import com.shodhAI.ShodhAI.Entity.Content;
import com.shodhAI.ShodhAI.Entity.Module;
import com.shodhAI.ShodhAI.Entity.Question;
import com.shodhAI.ShodhAI.Entity.QuestionType;
import com.shodhAI.ShodhAI.Entity.Topic;
import com.shodhAI.ShodhAI.Service.AIService;
import com.shodhAI.ShodhAI.Service.ContentService;
import com.shodhAI.ShodhAI.Service.ExceptionHandlingService;
import com.shodhAI.ShodhAI.Service.QuestionService;
import com.shodhAI.ShodhAI.Service.QuestionTypeService;
import com.shodhAI.ShodhAI.Service.ResponseService;
import com.shodhAI.ShodhAI.Service.TopicService;
import jakarta.persistence.PersistenceException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/question", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
public class QuestionController {

    @Autowired
    ExceptionHandlingService exceptionHandlingService;

    @Autowired
    QuestionService questionService;

    @Autowired
    ContentService contentService;

    @Autowired
    AIService aiService;

    @Autowired
    TopicService topicService;

    @Autowired
    QuestionTypeService questionTypeService;

    @PostMapping("/generate-questions")
    public ResponseEntity<?> generateQuestions(@Valid @RequestBody QuestionRequestDto questionRequestDto) {
        try {

            questionService.validateQuestion(questionRequestDto);
            Module module = questionService.validateModule(questionRequestDto);
            Topic topic = questionService.validateTopic(questionRequestDto);
            List<Content> contentList = contentService.getContentByTopic(topic);

            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("module", module.getModuleTitle());
            dataMap.put("topic", topic.getTopicTitle());

            List<Map<String, Object>> contentDataMapList = new ArrayList<>();
            if (topic.getTopicType().equals(Constant.GET_TOPIC_TYPE_TEACHING)) {
                for (Content content : contentList) {
                    Map<String, Object> contentDataMap = new HashMap<>();
                    contentDataMap.put("type", content.getFileType().getFileTypeName().toLowerCase());
                    contentDataMap.put("url", content.getUrl());

                    if (content.getContentType().equals(Constant.GET_CONTENT_TYPE_PRACTICE_QUESTION)) {
                        contentDataMapList.add(contentDataMap);
                    }
                }
            } else {
                for (Content content : contentList) {
                    Map<String, Object> contentDataMap = new HashMap<>();
                    contentDataMap.put("type", content.getFileType().getFileTypeName().toLowerCase());
                    contentDataMap.put("url", content.getUrl());

                    contentDataMapList.add(contentDataMap);

                    if (content.getContentType().equals(Constant.GET_CONTENT_TYPE_ASSIGNMENT)) {
                        contentDataMapList.add(contentDataMap);
                    }
                }
            }
            dataMap.put("question_material", contentDataMapList);

            if (topic.getTopicType().getTopicTypeName().equals(Constant.GET_TOPIC_TYPE_ASSIGNMENT)) {
                dataMap.put("isPractise", false);
                return generateAssignmentQuestions(questionRequestDto, dataMap, topic);
            } else {
                dataMap.put("isPractise", true);
                return generatePracticeQuestions(questionRequestDto, dataMap, topic);
            }

        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            return ResponseService.generateErrorResponse("Exception Caught: " + exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> generatePracticeQuestions(QuestionRequestDto questionRequestDto, Map<String, Object> dataMap, Topic topic) {
        try {

            List<Question> questionList = questionService.getQuestionByTopic(topic);

            if (questionList.isEmpty() || questionList == null) {
                // Call the AI/ML API to generate questions
                List<QuestionResponseDto> questionResponseDtoList = aiService.callAIToGenerateQuestions(dataMap);
                questionList = questionService.saveQuestionList(questionResponseDtoList, topic);

                if (questionList.isEmpty()) {
                    return ResponseService.generateSuccessResponse("Not able to Created Question Successfully", questionList, HttpStatus.OK);
                }
                return ResponseService.generateSuccessResponse("Question Created Successfully", questionList, HttpStatus.OK);
            } else {

                // fetch the questions from the db once they get saved in the db.
                return ResponseService.generateSuccessResponse("Question Fetched Successfully", questionList, HttpStatus.OK);
            }
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            return ResponseService.generateErrorResponse("Exception Caught: " + exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> generateAssignmentQuestions(QuestionRequestDto questionRequestDto, Map<String, Object> dataMap, Topic topic) {
        try {

            List<Question> questionList = questionService.getQuestionByTopic(topic);

            if (questionList.isEmpty() || questionList == null) {
                // Call the AI/ML API to generate questions
                List<QuestionResponseDto> questionResponseDtoList = aiService.callAIToGenerateQuestions(dataMap);
                questionList = questionService.saveQuestionList(questionResponseDtoList, topic);

                if (questionList.isEmpty()) {
                    return ResponseService.generateSuccessResponse("Not able to Created Question Successfully", questionList, HttpStatus.OK);
                }
                return ResponseService.generateSuccessResponse("Question Created Successfully", questionList, HttpStatus.OK);
            } else {

                // fetch the questions from the db once they get saved in the db.
                return ResponseService.generateSuccessResponse("Question Fetched Successfully", questionList, HttpStatus.OK);
            }
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            return ResponseService.generateErrorResponse("Exception Caught: " + exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/get-filter-question")
    public ResponseEntity<?> getQuestion(@RequestParam(defaultValue = "0") int offset, @RequestParam(defaultValue = "10") int limit,@RequestParam(value = "topic_id", required = false) Long topicId,
                                         @RequestParam(value = "question_type_id", required = false) Long questionTypeId) {

        try {
            if (offset < 0) {
                throw new IllegalArgumentException("Offset for pagination cannot be a negative number");
            }
            if (limit <= 0) {
                throw new IllegalArgumentException("Limit for pagination cannot be a negative number or 0");
            }
            Topic topic=null;
            if(topicId!=null)
            {
                if (topicId <= 0) {
                    throw new IllegalArgumentException("Topic Id cannot be <= 0");
                }
                 topic = topicService.getTopicById(topicId);
            }
            QuestionType questionType=null;
            if(questionTypeId!=null)
            {
                if (questionTypeId <= 0) {
                    throw new IllegalArgumentException("Question Type Id cannot be <= 0");
                }
                questionType = questionTypeService.getQuestionTypeById(questionTypeId);
            }

            assert questionType != null;
            List<Question> questions = questionService.questionFilter(topic, questionType.getQuestionTypeId());

            int totalItems = questions.size();
            int totalPages = (int) Math.ceil((double) totalItems / limit);
            int fromIndex = offset * limit;
            int toIndex = Math.min(fromIndex + limit, totalItems);

            if (offset >= totalPages && offset != 0) {
                throw new IllegalArgumentException("No more questions available");
            }

            List<Question> questionList = questions.subList(fromIndex, toIndex);

            Map<String, Object> response = new HashMap<>();
            response.put("questionList", questionList);
            response.put("totalItems", totalItems);
            response.put("totalPages", totalPages);
            response.put("currentPage", offset);
            return ResponseService.generateSuccessResponse("Questions fetched Successfully", response, HttpStatus.OK);

        } catch (DataIntegrityViolationException dataIntegrityViolationException) {
            exceptionHandlingService.handleException(dataIntegrityViolationException);
            return ResponseService.generateErrorResponse("Data Integrity Exception caught: " + dataIntegrityViolationException.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
            exceptionHandlingService.handleException(indexOutOfBoundsException);
            return ResponseService.generateErrorResponse("Index Out of Bound Exception Caught: " + indexOutOfBoundsException.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (PersistenceException persistenceException) {
            exceptionHandlingService.handleException(persistenceException);
            return ResponseService.generateErrorResponse("Persistence Exception Caught: " + persistenceException.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (IllegalArgumentException illegalArgumentException) {
            exceptionHandlingService.handleException(illegalArgumentException);
            return ResponseService.generateErrorResponse("Illegal Exception Caught: " + illegalArgumentException.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            return ResponseService.generateErrorResponse("Exception Caught: " + exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
