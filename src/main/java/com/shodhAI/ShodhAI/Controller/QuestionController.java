package com.shodhAI.ShodhAI.Controller;

import com.shodhAI.ShodhAI.Component.Constant;
import com.shodhAI.ShodhAI.Dto.QuestionRequestDto;
import com.shodhAI.ShodhAI.Dto.QuestionResponseDto;
import com.shodhAI.ShodhAI.Entity.Content;
import com.shodhAI.ShodhAI.Entity.Module;
import com.shodhAI.ShodhAI.Entity.Question;
import com.shodhAI.ShodhAI.Entity.Topic;
import com.shodhAI.ShodhAI.Service.AIService;
import com.shodhAI.ShodhAI.Service.ContentService;
import com.shodhAI.ShodhAI.Service.ExceptionHandlingService;
import com.shodhAI.ShodhAI.Service.QuestionService;
import com.shodhAI.ShodhAI.Service.ResponseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
            for (Content content : contentList) {
                Map<String, Object> contentDataMap = new HashMap<>();
                contentDataMap.put("type", content.getFileType().getFileTypeName().toLowerCase());
                contentDataMap.put("url", content.getUrl());

                contentDataMapList.add(contentDataMap);
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

            System.out.println("HERE 11");
            if (questionList.isEmpty() || questionList == null) {
                // Call the AI/ML API to generate questions
                List<QuestionResponseDto> questionResponseDtoList = aiService.callAIToGenerateQuestions(dataMap);
                System.out.println("ISSUE YHA HAI: " + questionResponseDtoList.size());
                questionList = questionService.saveQuestionList(questionResponseDtoList);

                System.out.println("HERE 11" + questionList.size());
                if (questionList.isEmpty()) {
                    return ResponseService.generateSuccessResponse("Not able to Created Question Successfully", questionList, HttpStatus.OK);
                }
                return ResponseService.generateSuccessResponse("Question Created Successfully", questionList, HttpStatus.OK);
            } else {

                // fetch the questions from the db once they get saved in the db.
                return ResponseService.generateSuccessResponse("Question Fetched Successfully", null, HttpStatus.OK);
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
                questionList = questionService.saveQuestionList(questionResponseDtoList);

                if (questionList.isEmpty()) {
                    return ResponseService.generateSuccessResponse("Not able to Created Question Successfully", questionList, HttpStatus.OK);
                }
                return ResponseService.generateSuccessResponse("Question Created Successfully", questionList, HttpStatus.OK);
            } else {

                // fetch the questions from the db once they get saved in the db.
                return ResponseService.generateSuccessResponse("Question Fetched Successfully", null, HttpStatus.OK);
            }
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            return ResponseService.generateErrorResponse("Exception Caught: " + exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
