package com.shodhAI.ShodhAI.Controller;

import com.shodhAI.ShodhAI.Component.JwtUtil;
import com.shodhAI.ShodhAI.Entity.Topic;
import com.shodhAI.ShodhAI.Entity.UserSubComponentProgress;
import com.shodhAI.ShodhAI.Entity.UserSubTopicProgress;
import com.shodhAI.ShodhAI.Service.ExceptionHandlingService;
import com.shodhAI.ShodhAI.Service.ResponseService;
import com.shodhAI.ShodhAI.Service.TopicService;
import com.shodhAI.ShodhAI.Service.UserSubComponentProgressService;
import com.shodhAI.ShodhAI.Service.UserSubTopicProgressService;
import jakarta.persistence.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/user", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
public class UserController {

    @Autowired
    ExceptionHandlingService exceptionHandlingService;

    @Autowired
    JwtUtil jwtTokenUtil;

    @Autowired
    UserSubComponentProgressService userSubComponentProgressService;

    @Autowired
    UserSubTopicProgressService userSubTopicProgressService;

    @Autowired
    TopicService topicService;

    @PostMapping(value = "/add-sub-component-progress")
    public ResponseEntity<?> addSubComponentProgress(@RequestParam(value = "sub_component_name") String subComponentName,
                                                     @RequestParam("sub_topic_id") String subtopicIdString,
                                                     @RequestParam(value = "user_sub_topic_progress_id", required = false) String userSubTopicProgressIdString,
                                                     @RequestHeader(value = "Authorization") String authHeader) {
        try {

            Long subTopicId = Long.parseLong(subtopicIdString);
            Long userSubTopicProgressId = null;
            if(userSubTopicProgressIdString != null) {
                userSubTopicProgressId = Long.parseLong(userSubTopicProgressIdString);
            }

            String jwtToken = authHeader.substring(7);
            Long roleId = jwtTokenUtil.extractRoleId(jwtToken);
            Long userId = jwtTokenUtil.extractId(jwtToken);

            userSubComponentProgressService.validateUserSubComponentProgress(subTopicId, subComponentName);
            Topic subTopic = topicService.getTopicById(subTopicId);

            List<UserSubTopicProgress> userSubTopicProgressList = userSubTopicProgressService.getUserSubTopicProgressFilter(userSubTopicProgressId, userId, roleId, null);
            UserSubTopicProgress userSubTopicProgress = null;
            if(!userSubTopicProgressList.isEmpty()) {
                userSubTopicProgress = userSubTopicProgressList.get(0);
            }
            UserSubComponentProgress userSubComponentProgress = userSubComponentProgressService.saveUserSubComponentProgress(userId, roleId, userSubTopicProgress, subTopic, subComponentName);

            return ResponseService.generateSuccessResponse("User Sub Component Progress Created Successfully", userSubComponentProgress, HttpStatus.OK);

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

    @GetMapping("/get-filter-user-sub-component-progress")
    public ResponseEntity<?> getFilterUserSubComponentProgress (
            @RequestParam(value = "topic_id", required = false) String topicIdString,
            @RequestParam(value = "sub_component_name", required = false) String subComponentName,
            @RequestParam(value = "user_sub_component_progress_id", required = false) String userSubComponentProgressIdString,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int limit,
            @RequestHeader(value = "Authorization") String authHeader) {

        try {
            Long topicId = null, userSubComponentProgressId = null;
            if(topicIdString != null) {
                topicId = Long.parseLong(topicIdString);
            }
            if(userSubComponentProgressIdString != null) {
                userSubComponentProgressId = Long.parseLong(userSubComponentProgressIdString);
            }

            if (offset < 0) {
                throw new IllegalArgumentException("Offset for pagination cannot be a negative number");
            }
            if (limit <= 0) {
                throw new IllegalArgumentException("Limit for pagination cannot be a negative number or 0");
            }

            String jwtToken = authHeader.substring(7);
            Long roleId = jwtTokenUtil.extractRoleId(jwtToken);
            Long userId = jwtTokenUtil.extractId(jwtToken);

            // Fetch filtered products
            List<UserSubComponentProgress> userSubComponentProgressList = userSubComponentProgressService.getUserSubComponentProgressFilter(userSubComponentProgressId, userId, roleId, topicId, subComponentName);

            if (userSubComponentProgressList.isEmpty()) {
                return ResponseService.generateSuccessResponse("No User Sub Component found with the given criteria", new ArrayList<>(), HttpStatus.OK);
            }

            // Pagination logic
            int totalItems = userSubComponentProgressList.size();
            int totalPages = (int) Math.ceil((double) totalItems / limit);
            int fromIndex = offset * limit;
            int toIndex = Math.min(fromIndex + limit, totalItems);

            if (offset >= totalPages && offset != 0) {
                throw new IllegalArgumentException("No more Conversation available");
            }
            // Validate offset request
            if (fromIndex >= totalItems) {
                return ResponseService.generateErrorResponse("Page index out of range", HttpStatus.BAD_REQUEST);
            }

            List<UserSubComponentProgress> paginatedList = userSubComponentProgressList.subList(fromIndex, toIndex);

            // Construct paginated response
            Map<String, Object> response = new HashMap<>();
            response.put("userSubComponentProgress", userSubComponentProgressList);
            response.put("totalItems", totalItems);
            response.put("totalPages", totalPages);
            response.put("currentPage", offset);

            return ResponseService.generateSuccessResponse("User Sub Component Progress Retrieved Successfully", response, HttpStatus.OK);

        } catch (NumberFormatException numberFormatException) {
            exceptionHandlingService.handleException(numberFormatException);
            return ResponseService.generateErrorResponse(numberFormatException.getMessage(), HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException illegalArgumentException) {
            exceptionHandlingService.handleException(illegalArgumentException);
            return ResponseService.generateErrorResponse(illegalArgumentException.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            return ResponseService.generateErrorResponse(exception.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PatchMapping("/update-user-sub-component-progress")
    public ResponseEntity<?> updateUserSubComponentProgress (
            @RequestParam(value = "topic_id", required = false) String topicIdString,
            @RequestParam("sub_component_name") String subComponentName,
            @RequestParam("is_completed") Boolean isCompleted,
            @RequestParam("user_sub_component_progress_id") String userSubComponentProgressIdString,
            @RequestHeader(value = "Authorization") String authHeader) {

        try {
            Long topicId = null, userSubComponentProgressId = null;
            if(topicIdString != null) {
                topicId = Long.parseLong(topicIdString);
            }
            if(userSubComponentProgressIdString != null) {
                userSubComponentProgressId = Long.parseLong(userSubComponentProgressIdString);
            }

            String jwtToken = authHeader.substring(7);
            Long roleId = jwtTokenUtil.extractRoleId(jwtToken);
            Long userId = jwtTokenUtil.extractId(jwtToken);

            // Fetch filtered products
            List<UserSubComponentProgress> userSubComponentProgressList = userSubComponentProgressService.getUserSubComponentProgressFilter(userSubComponentProgressId, userId, roleId, topicId, subComponentName);

            if (userSubComponentProgressList.isEmpty()) {
                return ResponseService.generateSuccessResponse("No User Sub Component found with the given criteria", new ArrayList<>(), HttpStatus.OK);
            }

            UserSubComponentProgress userSubComponentProgress = userSubComponentProgressList.get(0);
            userSubComponentProgressService.updateUserSubComponentProgress(userSubComponentProgress, isCompleted);
            return ResponseService.generateSuccessResponse("User Sub Component Progress Updated Successfully", userSubComponentProgressList, HttpStatus.OK);

        } catch (NumberFormatException numberFormatException) {
            exceptionHandlingService.handleException(numberFormatException);
            return ResponseService.generateErrorResponse(numberFormatException.getMessage(), HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException illegalArgumentException) {
            exceptionHandlingService.handleException(illegalArgumentException);
            return ResponseService.generateErrorResponse(illegalArgumentException.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            return ResponseService.generateErrorResponse(exception.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

}
