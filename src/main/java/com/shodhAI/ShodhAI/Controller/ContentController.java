package com.shodhAI.ShodhAI.Controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.shodhAI.ShodhAI.Component.Constant;
import com.shodhAI.ShodhAI.Dto.FlowListWrapper;
import com.shodhAI.ShodhAI.Dto.FlowRequestDto;
import com.shodhAI.ShodhAI.Dto.QuestionResponseDto;
import com.shodhAI.ShodhAI.Entity.Content;
import com.shodhAI.ShodhAI.Entity.ContentType;
import com.shodhAI.ShodhAI.Entity.Question;
import com.shodhAI.ShodhAI.Entity.Topic;
import com.shodhAI.ShodhAI.Service.AIService;
import com.shodhAI.ShodhAI.Service.ContentService;
import com.shodhAI.ShodhAI.Service.ExceptionHandlingService;
import com.shodhAI.ShodhAI.Service.QuestionService;
import com.shodhAI.ShodhAI.Service.ResponseService;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/content", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
public class ContentController {

    @Autowired
    ExceptionHandlingService exceptionHandlingService;

    @Autowired
    private Cloudinary cloudinary;

    @Autowired
    ContentService contentService;

    @Autowired
    TopicService topicService;

    @Autowired
    QuestionService questionService;

    @Autowired
    AIService aiService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadContent(HttpServletRequest request,
                                           @RequestParam("file") MultipartFile file,
                                           @RequestParam("topic_id") Long topicId,
                                           @RequestParam("content_type_id") Long contentTypeId) {
        try {

            // Upload profile picture to Cloudinary
            Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());

            /*// Set the profile picture URL in the student DTO
            String fileUrl = uploadResult.get("url").toString();
            // Get the file format from the response
            String format = (String) uploadResult.get("format");

            System.out.println("Uploaded file format: " + format);*/

            contentService.validateContent(topicId, contentTypeId);
            Content content = contentService.saveContent(topicId, uploadResult, contentTypeId);

            return ResponseService.generateSuccessResponse("File Uploaded Successfully", content, HttpStatus.OK);

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

    @GetMapping(value = "/get-content-by-id/{contentIdString}")
    public ResponseEntity<?> retrieveContentById(HttpServletRequest request, @PathVariable("contentIdString") String contentIdString) {
        try {

            Long contentId = Long.parseLong(contentIdString);
            Content content = contentService.getContentById(contentId);

            if (content == null) {
                return ResponseService.generateErrorResponse("Data not present in the DB", HttpStatus.OK);
            }

            return ResponseService.generateSuccessResponse("Content Retrieved Successfully", content, HttpStatus.OK);

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

    @GetMapping(value = "/get-content-by-topic-id/{topicIdString}")
    public ResponseEntity<?> retrieveContentByTopicId(HttpServletRequest request, @PathVariable("topicIdString") String topicIdString) {
        try {

            Long topicId = Long.parseLong(topicIdString);
            List<Content> contents = contentService.getContentByTopicId(topicId);

            if (contents == null) {
                return ResponseService.generateErrorResponse("Data not present in the DB", HttpStatus.OK);
            }

            return ResponseService.generateSuccessResponse("Content Retrieved Successfully", contents, HttpStatus.OK);

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

    @PostMapping(value = "/get-flow/{topicIdString}")
    public ResponseEntity<?> retrieveContentFlow(HttpServletRequest request, @PathVariable String topicIdString) {
        try {

            Long topicId = Long.parseLong(topicIdString);
            Topic topic = topicService.getTopicById(topicId);
            if (topic == null) {
                return ResponseService.generateErrorResponse("Data not present in the DB", HttpStatus.OK);
            }

            List<Content> contentList = contentService.getContentByTopic(topic);
            List<Question> questionList = questionService.getQuestionByTopic(topic);

            // TODO Apis integration from the ml/ai part.


            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("module", topic.getModule().getModuleTitle());
            dataMap.put("topic", topic.getTopicTitle());

            List<Map<String, Object>> contentDataMapList = new ArrayList<>();
            for (Content content : contentList) {
                Map<String, Object> contentDataMap = new HashMap<>();
                contentDataMap.put("type", content.getFileType().getFileTypeName().toLowerCase());
                contentDataMap.put("url", content.getUrl());

                if (content.getContentType().getContentTypeName().equals(Constant.GET_TOPIC_TYPE_TEACHING)) {
                    contentDataMapList.add(contentDataMap);
                }
            }

            dataMap.put("content_list", contentDataMapList);
            List<String> questionStringList = new ArrayList<>();
            for (Question question : questionList) {
                questionStringList.add(question.getQuestion());
            }

            dataMap.put("question_list", questionStringList);

            FlowListWrapper flowList = aiService.callAIToGetFlow(dataMap);
            return ResponseService.generateSuccessResponse("Content Flow Retrieved Successfully", flowList, HttpStatus.OK);

        } catch (DataIntegrityViolationException dataIntegrityViolationException) {
            exceptionHandlingService.handleException(dataIntegrityViolationException);
            throw new IndexOutOfBoundsException("Data Integrity Exception caught [college_email, username, mobileNumber must be unique]: " + dataIntegrityViolationException.getMessage());
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

    @GetMapping("/get-content-type-by-id/{contentTypeIdString}")
    public ResponseEntity<?> retrieveContentTypeById(HttpServletRequest request, @PathVariable String contentTypeIdString) {
        try {

            Long contentTypeId = Long.parseLong(contentTypeIdString);
            ContentType contentType = contentService.getContentTypeById(contentTypeId);
            if (contentType == null) {
                return ResponseService.generateErrorResponse("Data not present in the DB", HttpStatus.OK);
            }
            return ResponseService.generateSuccessResponse("Content Type Retrieved Successfully", contentType, HttpStatus.OK);

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

    @GetMapping("/get-all-content-type")
    public ResponseEntity<?> retrieveContentTypes(HttpServletRequest request) {
        try {

            List<ContentType> contentTypeList = contentService.getAllContentTypes();
            if (contentTypeList.isEmpty()) {
                return ResponseService.generateErrorResponse("Data not present in the DB", HttpStatus.OK);
            }
            return ResponseService.generateSuccessResponse("Content Type Retrieved Successfully", contentTypeList, HttpStatus.OK);

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
