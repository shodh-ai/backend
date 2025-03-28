package com.shodhAI.ShodhAI.Controller;

import com.shodhAI.ShodhAI.Component.JwtUtil;
import com.shodhAI.ShodhAI.Dto.ParentTopicWrapper;
import com.shodhAI.ShodhAI.Dto.ReportWrapper;
import com.shodhAI.ShodhAI.Dto.TopicDto;
import com.shodhAI.ShodhAI.Dto.TopicWrapper;
import com.shodhAI.ShodhAI.Entity.Conversation;
import com.shodhAI.ShodhAI.Entity.Question;
import com.shodhAI.ShodhAI.Entity.Session;
import com.shodhAI.ShodhAI.Entity.Topic;
import com.shodhAI.ShodhAI.Entity.TopicType;
import com.shodhAI.ShodhAI.Service.ConversationService;
import com.shodhAI.ShodhAI.Service.ExceptionHandlingService;
import com.shodhAI.ShodhAI.Service.QuestionService;
import com.shodhAI.ShodhAI.Service.ResponseService;
import com.shodhAI.ShodhAI.Service.SessionService;
import com.shodhAI.ShodhAI.Service.TopicService;
import jakarta.persistence.PersistenceException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/topic", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
public class TopicController {

    @Autowired
    ExceptionHandlingService exceptionHandlingService;

    @Autowired
    TopicService topicService;

    @Autowired
    QuestionService questionService;

    @Autowired
    JwtUtil jwtTokenUtil;

    @Autowired
    SessionService sessionService;

    @Autowired
    ConversationService conversationService;

    @PostMapping(value = "/add")
    public ResponseEntity<?> addTopic(@RequestBody TopicDto topicDto) {
        try {

            topicService.validateTopic(topicDto);
            Topic topic = topicService.saveTopic(topicDto);

            return ResponseService.generateSuccessResponse("Topic Created Successfully", topic, HttpStatus.OK);

        } catch (DataIntegrityViolationException dataIntegrityViolationException) {
            exceptionHandlingService.handleException(dataIntegrityViolationException);
            throw new IndexOutOfBoundsException("Data Integrity Exception caught: " + dataIntegrityViolationException.getMessage());
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

    // TODO We have to make this filter api for topic in future. (based on title, course , module etc).

    @GetMapping("/get-all")
    public ResponseEntity<?> retrieveTableOfTopics(HttpServletRequest request,
                                                   @RequestParam(value = "courseId", required = false) Long courseId,
                                                   @RequestParam("moduleId") Long moduleId) {
        try {

            List<Topic> parentTopicList = topicService.getParentTopicListByModuleId(moduleId);

            if (parentTopicList.isEmpty()) {
                return ResponseService.generateErrorResponse("Data not present in the DB", HttpStatus.OK);
            }

            List<ParentTopicWrapper> wrapper = new ArrayList<>();
            for (Topic parentTopic : parentTopicList) {
                List<Topic> subTopics = topicService.getSubTopic(parentTopic);

                ParentTopicWrapper topicWrapper = new ParentTopicWrapper();
                topicWrapper.wrapDetails(parentTopic, subTopics);
                wrapper.add(topicWrapper);

            }
            return ResponseService.generateSuccessResponse("Topic Retrieved Successfully", wrapper, HttpStatus.OK);

        } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
            exceptionHandlingService.handleException(indexOutOfBoundsException);
            return ResponseService.generateErrorResponse("Index Out of Bound Exception Caught: " + indexOutOfBoundsException.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (IllegalArgumentException illegalArgumentException) {
            exceptionHandlingService.handleException(illegalArgumentException);
            return ResponseService.generateErrorResponse("Illegal Exception Caught: " + illegalArgumentException.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            return ResponseService.generateErrorResponse("Exception Caught: " + exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get-topic-by-id/{topicIdString}")
    public ResponseEntity<?> retrieveTopicById(HttpServletRequest request, @PathVariable String topicIdString) {
        try {

            Long topicId = Long.parseLong(topicIdString);
            Topic topic = topicService.getTopicById(topicId);

            List<Question> practiceQuestion = questionService.questionFilter(topic, 1L);
            List<Question> exampleQuestion = questionService.questionFilter(topic, 2L);
            List<Question> testingQuestion = questionService.questionFilter(topic, 3L);
            List<Question> quiz = questionService.questionFilter(topic, 4L);

            TopicWrapper wrapper = new TopicWrapper();
            wrapper.wrapDetails(topic, practiceQuestion, exampleQuestion, testingQuestion, quiz);

            if (topic == null) {
                return ResponseService.generateErrorResponse("Data not present in the DB", HttpStatus.OK);
            }
            return ResponseService.generateSuccessResponse("Topic Retrieved Successfully", wrapper, HttpStatus.OK);

        } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
            exceptionHandlingService.handleException(indexOutOfBoundsException);
            return ResponseService.generateErrorResponse("Index Out of Bound Exception Caught: " + indexOutOfBoundsException.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (IllegalArgumentException illegalArgumentException) {
            exceptionHandlingService.handleException(illegalArgumentException);
            return ResponseService.generateErrorResponse("Illegal Exception Caught: " + illegalArgumentException.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            return ResponseService.generateErrorResponse("Exception Caught: " + exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get-topic-type-by-id/{topicTypeIdString}")
    public ResponseEntity<?> retrieveTopicTypeById(HttpServletRequest request, @PathVariable String topicTypeIdString) {
        try {

            Long topicTypeId = Long.parseLong(topicTypeIdString);
            TopicType topicType = topicService.getTopicTypeById(topicTypeId);
            if (topicType == null) {
                return ResponseService.generateErrorResponse("Data not present in the DB", HttpStatus.OK);
            }
            return ResponseService.generateSuccessResponse("Topic Type Retrieved Successfully", topicType, HttpStatus.OK);

        } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
            exceptionHandlingService.handleException(indexOutOfBoundsException);
            return ResponseService.generateErrorResponse("Index Out of Bound Exception Caught: " + indexOutOfBoundsException.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (IllegalArgumentException illegalArgumentException) {
            exceptionHandlingService.handleException(illegalArgumentException);
            return ResponseService.generateErrorResponse("Illegal Exception Caught: " + illegalArgumentException.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            return ResponseService.generateErrorResponse("Exception Caught: " + exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get-all-topic-type")
    public ResponseEntity<?> retrieveTopicTypes(HttpServletRequest request) {
        try {

            List<TopicType> topicTypeList = topicService.getAllTopicTypes();
            if (topicTypeList.isEmpty()) {
                return ResponseService.generateErrorResponse("Data not present in the DB", HttpStatus.OK);
            }
            return ResponseService.generateSuccessResponse("Topic Type Retrieved Successfully", topicTypeList, HttpStatus.OK);

        } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
            exceptionHandlingService.handleException(indexOutOfBoundsException);
            return ResponseService.generateErrorResponse("Index Out of Bound Exception Caught: " + indexOutOfBoundsException.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (IllegalArgumentException illegalArgumentException) {
            exceptionHandlingService.handleException(illegalArgumentException);
            return ResponseService.generateErrorResponse("Illegal Exception Caught: " + illegalArgumentException.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            return ResponseService.generateErrorResponse("Exception Caught: " + exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get-assignment-question-by-topic-id/{topicIdString}")
    public ResponseEntity<?> retrieveAssignmentQuestionById(HttpServletRequest request, @PathVariable String topicIdString) {
        try {

            Long topicId = Long.parseLong(topicIdString);
            Topic topic = topicService.getTopicById(topicId);
            if (topic == null) {
                return ResponseService.generateErrorResponse("Data not present in the DB", HttpStatus.OK);
            }

            questionService.validateAssignmentQuestionByTopic(topic);
            List<Question> questionList = questionService.getQuestionByTopic(topic);

            return ResponseService.generateSuccessResponse("Assignment Question Retrieved Successfully", questionList, HttpStatus.OK);

        } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
            exceptionHandlingService.handleException(indexOutOfBoundsException);
            return ResponseService.generateErrorResponse("Index Out of Bound Exception Caught: " + indexOutOfBoundsException.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (IllegalArgumentException illegalArgumentException) {
            exceptionHandlingService.handleException(illegalArgumentException);
            return ResponseService.generateErrorResponse("Illegal Exception Caught: " + illegalArgumentException.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            return ResponseService.generateErrorResponse("Exception Caught: " + exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get-practice-question-by-topic-id/{topicIdString}")
    public ResponseEntity<?> retrievePracticeQuestionById(HttpServletRequest request, @PathVariable String topicIdString) {
        try {

            Long topicId = Long.parseLong(topicIdString);
            Topic topic = topicService.getTopicById(topicId);
            if (topic == null) {
                return ResponseService.generateErrorResponse("Data not present in the DB", HttpStatus.OK);
            }

            questionService.validatePracticeQuestionByTopic(topic);
            List<Question> questionList = questionService.getQuestionByTopic(topic);

            return ResponseService.generateSuccessResponse("Assignment Question Retrieved Successfully", questionList, HttpStatus.OK);

        } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
            exceptionHandlingService.handleException(indexOutOfBoundsException);
            return ResponseService.generateErrorResponse("Index Out of Bound Exception Caught: " + indexOutOfBoundsException.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (IllegalArgumentException illegalArgumentException) {
            exceptionHandlingService.handleException(illegalArgumentException);
            return ResponseService.generateErrorResponse("Illegal Exception Caught: " + illegalArgumentException.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            return ResponseService.generateErrorResponse("Exception Caught: " + exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/generate-report/{topicIdString}")
    public ResponseEntity<?> generateReport(HttpServletRequest request,
                                            @PathVariable String topicIdString,
                                            @RequestHeader(value = "Authorization") String authHeader) {
        try {

            Long topicId = Long.parseLong(topicIdString);
            Topic topic = topicService.getTopicById(topicId);
            if (topic == null) {
                return ResponseService.generateErrorResponse("Data not present in the DB", HttpStatus.OK);
            }

            String jwtToken = authHeader.substring(7);
            Long roleId = jwtTokenUtil.extractRoleId(jwtToken);
            Long userId = jwtTokenUtil.extractId(jwtToken);

            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("module", topic.getModule().getModuleTitle());
            dataMap.put("topic", topic.getDefaultParentTopic().getTopicTitle());
            dataMap.put("sub-topic", topic.getTopicTitle());

            List<Map<String, Object>> conversations = new ArrayList<>();
            // first find the last session and then find  the conversation from the conversation table.

            List<Long> allowedQuestionTypeIds = Arrays.asList(null, 1L, 2L, 3L, 4L);

            for (Long questionTypeId : allowedQuestionTypeIds) {
                List<Session> filteredSessions = sessionService.sessionFilter(null, userId, roleId, topicId, questionTypeId);

                if (!filteredSessions.isEmpty()) {
                    // Get the last session for the current question type
                    Session latestSession = filteredSessions.get(filteredSessions.size() - 1);

                    List<Conversation> filteredConversations = conversationService.conversationFilter(latestSession.getSessionId(), userId, roleId);
                    List<Map<String, String>> conversationDetails = new ArrayList<>();

                    for (Conversation conversation : filteredConversations) {
                        Map<String, String> dialogueMap = new HashMap<>();
                        dialogueMap.put("user_dialogue", conversation.getUserDialogue());
                        dialogueMap.put("assistant_dialogue", conversation.getAssistantDialogue());
                        conversationDetails.add(dialogueMap);
                    }

                    if (!conversationDetails.isEmpty()) {
                        Map<String, Object> conversationMap = new HashMap<>();
                        conversationMap.put("reference_answer", null); // Add reference answer if needed
                        conversationMap.put("conversation", conversationDetails);
                        conversations.add(conversationMap);
                    }
                }
            }

            dataMap.put("conversations", conversations);

            // ml-api integration

            ReportWrapper reportWrapper = null;
            return ResponseService.generateSuccessResponse("Report Generated Successfully", dataMap, HttpStatus.OK);

        } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
            exceptionHandlingService.handleException(indexOutOfBoundsException);
            return ResponseService.generateErrorResponse("Index Out of Bound Exception Caught: " + indexOutOfBoundsException.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (IllegalArgumentException illegalArgumentException) {
            exceptionHandlingService.handleException(illegalArgumentException);
            return ResponseService.generateErrorResponse("Illegal Exception Caught: " + illegalArgumentException.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            return ResponseService.generateErrorResponse("Exception Caught: " + exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
