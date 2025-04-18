package com.shodhAI.ShodhAI.Controller;

import com.shodhAI.ShodhAI.Component.JwtUtil;
import com.shodhAI.ShodhAI.Entity.Course;
import com.shodhAI.ShodhAI.Entity.Module;
import com.shodhAI.ShodhAI.Entity.Topic;
import com.shodhAI.ShodhAI.Entity.UserCourseProgress;
import com.shodhAI.ShodhAI.Entity.UserModuleProgress;
import com.shodhAI.ShodhAI.Entity.UserSemesterProgress;
import com.shodhAI.ShodhAI.Entity.UserSubComponentProgress;
import com.shodhAI.ShodhAI.Entity.UserSubTopicProgress;
import com.shodhAI.ShodhAI.Entity.UserTopicProgress;
import com.shodhAI.ShodhAI.Service.CourseService;
import com.shodhAI.ShodhAI.Service.ExceptionHandlingService;
import com.shodhAI.ShodhAI.Service.ModuleService;
import com.shodhAI.ShodhAI.Service.ResponseService;
import com.shodhAI.ShodhAI.Service.TopicService;
import com.shodhAI.ShodhAI.Service.UserCourseProgressService;
import com.shodhAI.ShodhAI.Service.UserModuleProgressService;
import com.shodhAI.ShodhAI.Service.UserSemesterProgressService;
import com.shodhAI.ShodhAI.Service.UserSubComponentProgressService;
import com.shodhAI.ShodhAI.Service.UserSubTopicProgressService;
import com.shodhAI.ShodhAI.Service.UserTopicProgressService;
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
    UserTopicProgressService userTopicProgressService;

    @Autowired
    UserModuleProgressService userModuleProgressService;

    @Autowired
    UserCourseProgressService userCourseProgressService;

    @Autowired
    UserSemesterProgressService userSemesterProgressService;

    @Autowired
    TopicService topicService;

    @Autowired
    ModuleService moduleService;

    @Autowired
    CourseService courseService;

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

            Course course = subTopic.getCourse();
            List<UserCourseProgress> userCourseProgressList = userCourseProgressService.getUserCourseProgressFilter(null, userId, roleId, course.getCourseId());
            UserCourseProgress userCourseProgress = null;
            if(userCourseProgressList.isEmpty()) {
                userCourseProgress = userCourseProgressService.saveUserCourseProgress(userId, roleId, course.getCourseId(), null);
            } else {
                userCourseProgress = userCourseProgressList.get(0);
            }

            Module module = subTopic.getModule();
            List<UserModuleProgress> userModuleProgressList = userModuleProgressService.getUserModuleProgressFilter(null, userId, roleId, module.getModuleId());
            UserModuleProgress userModuleProgress = null;
            if(userModuleProgressList.isEmpty()) {
                userModuleProgress = userModuleProgressService.saveUserModuleProgress(userId, roleId, module.getModuleId(), userCourseProgress);
            }  else {
                userModuleProgress = userModuleProgressList.get(0);
            }

            Topic parentTopic = subTopic.getDefaultParentTopic();
            List<UserTopicProgress> userTopicProgressList = userTopicProgressService.getUserTopicProgressFilter(null, userId, roleId, parentTopic.getTopicId());
            UserTopicProgress userTopicProgress = null;
            if(userTopicProgressList.isEmpty()) {
                userTopicProgress = userTopicProgressService.saveUserTopicProgress(userId, roleId, module.getModuleId(), userModuleProgress);
            } else {
                userTopicProgress = userTopicProgressList.get(0);
            }

            List<UserSubTopicProgress> userSubTopicProgressList = userSubTopicProgressService.getUserSubTopicProgressFilter(userSubTopicProgressId, userId, roleId, subTopic.getTopicId());
            UserSubTopicProgress userSubTopicProgress = null;
            if(userSubTopicProgressList.isEmpty()) {
                userSubTopicProgress = userSubTopicProgressService.saveUserSubTopicProgress(userId, roleId, subTopic.getTopicId(), userTopicProgress);
            } else {
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
            @RequestParam(value = "sub_topic_id", required = false) String subTopicIdString,
            @RequestParam("sub_component_name") String subComponentName,
            @RequestParam("is_completed") Boolean isCompleted,
            @RequestParam(value = "user_sub_topic_progress_id", required = false) String userSubComponentProgressIdString,
            @RequestHeader(value = "Authorization") String authHeader) {

        try {
            Long subTopicId = null, userSubComponentProgressId = null;
            if(subTopicIdString != null) {
                subTopicId = Long.parseLong(subTopicIdString);
            }
            if(userSubComponentProgressIdString != null) {
                userSubComponentProgressId = Long.parseLong(userSubComponentProgressIdString);
            }

            String jwtToken = authHeader.substring(7);
            Long roleId = jwtTokenUtil.extractRoleId(jwtToken);
            Long userId = jwtTokenUtil.extractId(jwtToken);

            // Fetch filtered products
            List<UserSubComponentProgress> userSubComponentProgressList = userSubComponentProgressService.getUserSubComponentProgressFilter(userSubComponentProgressId, userId, roleId, subTopicId, subComponentName);

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

    @PostMapping(value = "/add-sub-topic-progress")
    public ResponseEntity<?> addSubTopicProgress(@RequestParam("sub_topic_id") String subtopicIdString,
                                                 @RequestParam(value = "user_topic_progress_id", required = false) String userTopicProgressIdString,
                                                 @RequestHeader(value = "Authorization") String authHeader) {
        try {

            Long subTopicId = Long.parseLong(subtopicIdString);
            Long userTopicProgressId = null;
            if(userTopicProgressIdString != null) {
                userTopicProgressId = Long.parseLong(userTopicProgressIdString);
            }

            String jwtToken = authHeader.substring(7);
            Long roleId = jwtTokenUtil.extractRoleId(jwtToken);
            Long userId = jwtTokenUtil.extractId(jwtToken);

            userSubTopicProgressService.validateUserSubTopicProgress(subTopicId);
            Topic subTopic = topicService.getTopicById(subTopicId);

            List<UserTopicProgress> userTopicProgressList = userTopicProgressService.getUserTopicProgressFilter(userTopicProgressId, userId, roleId, null);
            UserTopicProgress userTopicProgress = null;
            if(!userTopicProgressList.isEmpty()) {
                userTopicProgress = userTopicProgressList.get(0);
            } else {
                // For now handling all the validation through this logic
                throw new IllegalArgumentException("User Topic Progress Does not exists");
            }
            UserSubTopicProgress userSubTopicProgress = userSubTopicProgressService.saveUserSubTopicProgress(userId, roleId, subTopic.getTopicId(), userTopicProgress);

            return ResponseService.generateSuccessResponse("User Sub Topic Progress Created Successfully", userSubTopicProgress, HttpStatus.OK);

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

    @GetMapping("/get-filter-user-sub-topic-progress")
    public ResponseEntity<?> getFilterUserSubTopicProgress (
            @RequestParam(value = "sub_topic_id", required = false) String subTopicIdString,
            @RequestParam(value = "user_sub_topic_progress_id", required = false) String userSubTopicProgressIdString,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int limit,
            @RequestHeader(value = "Authorization") String authHeader) {

        try {
            Long subTopicId = null, userSubTopicProgressId = null;
            if(subTopicIdString != null) {
                subTopicId = Long.parseLong(subTopicIdString);
            }
            if(userSubTopicProgressIdString != null) {
                userSubTopicProgressId = Long.parseLong(userSubTopicProgressIdString);
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
            List<UserSubTopicProgress> userSubTopicProgressList = userSubTopicProgressService.getUserSubTopicProgressFilter(userSubTopicProgressId, userId, roleId, subTopicId);

            if (userSubTopicProgressList.isEmpty()) {
                return ResponseService.generateSuccessResponse("No User Sub Topic found with the given criteria", new ArrayList<>(), HttpStatus.OK);
            }

            // Pagination logic
            int totalItems = userSubTopicProgressList.size();
            int totalPages = (int) Math.ceil((double) totalItems / limit);
            int fromIndex = offset * limit;
            int toIndex = Math.min(fromIndex + limit, totalItems);

            if (offset >= totalPages && offset != 0) {
                throw new IllegalArgumentException("No more Sub Topic Progress Data available");
            }
            // Validate offset request
            if (fromIndex >= totalItems) {
                return ResponseService.generateErrorResponse("Page index out of range", HttpStatus.BAD_REQUEST);
            }

            List<UserSubTopicProgress> paginatedList = userSubTopicProgressList.subList(fromIndex, toIndex);

            // Construct paginated response
            Map<String, Object> response = new HashMap<>();
            response.put("userSubTopicProgress", userSubTopicProgressList);
            response.put("totalItems", totalItems);
            response.put("totalPages", totalPages);
            response.put("currentPage", offset);

            return ResponseService.generateSuccessResponse("User Sub Topic Progress Retrieved Successfully", response, HttpStatus.OK);

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

    @PatchMapping("/update-user-sub-topic-progress")
    public ResponseEntity<?> updateUserSubTopicProgress (
            @RequestParam(value = "sub_topic_id", required = false) String subTopicIdString,
            @RequestParam("is_completed") Boolean isCompleted,
            @RequestParam("user_sub_topic_progress_id") String userSubTopicProgressIdString,
            @RequestHeader(value = "Authorization") String authHeader) {

        try {
            Long subTopicId = null, userSubComponentProgressId = null;
            if(subTopicIdString != null) {
                subTopicId = Long.parseLong(subTopicIdString);
            }
            if(userSubTopicProgressIdString != null) {
                userSubComponentProgressId = Long.parseLong(userSubTopicProgressIdString);
            }

            String jwtToken = authHeader.substring(7);
            Long roleId = jwtTokenUtil.extractRoleId(jwtToken);
            Long userId = jwtTokenUtil.extractId(jwtToken);

            // Fetch filtered products
            List<UserSubTopicProgress> userSubTopicProgressList = userSubTopicProgressService.getUserSubTopicProgressFilter(userSubComponentProgressId, userId, roleId, subTopicId);

            if (userSubTopicProgressList.isEmpty()) {
                return ResponseService.generateSuccessResponse("No User Sub Topic found with the given criteria", new ArrayList<>(), HttpStatus.OK);
            }

            UserSubTopicProgress userSubTopicProgress = userSubTopicProgressList.get(0);
            userSubTopicProgress = userSubTopicProgressService.updateUserSubTopicProgress(userSubTopicProgress, isCompleted);

            return ResponseService.generateSuccessResponse("User Sub Topic Progress Updated Successfully", userSubTopicProgress, HttpStatus.OK);

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

    @PostMapping(value = "/add-topic-progress")
    public ResponseEntity<?> addTopicProgress(@RequestParam("topic_id") String topicIdString,
                                                 @RequestParam(value = "user_module_progress_id", required = false) String userModuleProgressIdString,
                                                 @RequestHeader(value = "Authorization") String authHeader) {
        try {

            Long topicId = Long.parseLong(topicIdString);
            Long userModuleProgressId = null;
            if(userModuleProgressIdString != null) {
                userModuleProgressId = Long.parseLong(userModuleProgressIdString);
            }

            String jwtToken = authHeader.substring(7);
            Long roleId = jwtTokenUtil.extractRoleId(jwtToken);
            Long userId = jwtTokenUtil.extractId(jwtToken);

            userTopicProgressService.validateUserTopicProgress(topicId);
            Topic topic = topicService.getTopicById(topicId);

            List<UserModuleProgress> userModuleProgressList = userModuleProgressService.getUserModuleProgressFilter(userModuleProgressId, userId, roleId, null);
            UserModuleProgress userModuleProgress = null;
            if(!userModuleProgressList.isEmpty()) {
                userModuleProgress = userModuleProgressList.get(0);
            } else {
                // For now handling all the validation through this logic
                throw new IllegalArgumentException("User Module Progress Does not exists");
            }
            UserTopicProgress userTopicProgress = userTopicProgressService.saveUserTopicProgress(userId, roleId, topic.getTopicId(), userModuleProgress);

            return ResponseService.generateSuccessResponse("User Topic Progress Created Successfully", userTopicProgress, HttpStatus.OK);

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

    @GetMapping("/get-filter-user-topic-progress")
    public ResponseEntity<?> getFilterUserTopicProgress (
            @RequestParam(value = "topic_id", required = false) String topicIdString,
            @RequestParam(value = "user_topic_progress_id", required = false) String userTopicProgressIdString,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int limit,
            @RequestHeader(value = "Authorization") String authHeader) {

        try {
            Long topicId = null, userTopicProgressId = null;
            if(topicIdString != null) {
                topicId = Long.parseLong(topicIdString);
            }
            if(userTopicProgressIdString != null) {
                userTopicProgressId = Long.parseLong(userTopicProgressIdString);
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
            List<UserTopicProgress> userTopicProgressList = userTopicProgressService.getUserTopicProgressFilter(userTopicProgressId, userId, roleId, topicId);

            if (userTopicProgressList.isEmpty()) {
                return ResponseService.generateSuccessResponse("No User Topic found with the given criteria", new ArrayList<>(), HttpStatus.OK);
            }

            // Pagination logic
            int totalItems = userTopicProgressList.size();
            int totalPages = (int) Math.ceil((double) totalItems / limit);
            int fromIndex = offset * limit;
            int toIndex = Math.min(fromIndex + limit, totalItems);

            if (offset >= totalPages && offset != 0) {
                throw new IllegalArgumentException("No more Topic Progress Data available");
            }
            // Validate offset request
            if (fromIndex >= totalItems) {
                return ResponseService.generateErrorResponse("Page index out of range", HttpStatus.BAD_REQUEST);
            }

            List<UserTopicProgress> paginatedList = userTopicProgressList.subList(fromIndex, toIndex);

            // Construct paginated response
            Map<String, Object> response = new HashMap<>();
            response.put("userTopicProgress", userTopicProgressList);
            response.put("totalItems", totalItems);
            response.put("totalPages", totalPages);
            response.put("currentPage", offset);

            return ResponseService.generateSuccessResponse("User Topic Progress Retrieved Successfully", response, HttpStatus.OK);

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

    @PatchMapping("/update-user-topic-progress")
    public ResponseEntity<?> updateUserTopicProgress (
            @RequestParam(value = "topic_id", required = false) String topicIdString,
            @RequestParam("is_completed") Boolean isCompleted,
            @RequestParam("user_topic_progress_id") String userTopicProgressIdString,
            @RequestHeader(value = "Authorization") String authHeader) {

        try {
            Long topicId = null, userTopicProgressId = null;
            if(topicIdString != null) {
                topicId = Long.parseLong(topicIdString);
            }
            if(userTopicProgressIdString != null) {
                userTopicProgressId = Long.parseLong(userTopicProgressIdString);
            }

            String jwtToken = authHeader.substring(7);
            Long roleId = jwtTokenUtil.extractRoleId(jwtToken);
            Long userId = jwtTokenUtil.extractId(jwtToken);

            // Fetch filtered products
            List<UserTopicProgress> userTopicProgressList = userTopicProgressService.getUserTopicProgressFilter(userTopicProgressId, userId, roleId, topicId);

            if (userTopicProgressList.isEmpty()) {
                return ResponseService.generateSuccessResponse("No User Topic found with the given criteria", new ArrayList<>(), HttpStatus.OK);
            }

            UserTopicProgress userTopicProgress = userTopicProgressList.get(0);
            userTopicProgress = userTopicProgressService.updateUserTopicProgress(userTopicProgress, isCompleted);

            return ResponseService.generateSuccessResponse("User Sub Topic Progress Updated Successfully", userTopicProgress, HttpStatus.OK);

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

    @PostMapping(value = "/add-module-progress")
    public ResponseEntity<?> addModuleProgress(@RequestParam("module_id") String moduleIdString,
                                               @RequestParam(value = "user_course_progress_id", required = false) String userCourseProgressIdString,
                                               @RequestHeader(value = "Authorization") String authHeader) {
        try {

            Long moduleId = Long.parseLong(moduleIdString);
            Long userCourseProgressId = null;
            if(userCourseProgressIdString != null) {
                userCourseProgressId = Long.parseLong(userCourseProgressIdString);
            }

            String jwtToken = authHeader.substring(7);
            Long roleId = jwtTokenUtil.extractRoleId(jwtToken);
            Long userId = jwtTokenUtil.extractId(jwtToken);

            userModuleProgressService.validateUserModuleProgress(moduleId);
            Module module = moduleService.getModuleById(moduleId);

            List<UserCourseProgress> userCourseProgressList = userCourseProgressService.getUserCourseProgressFilter(userCourseProgressId, userId, roleId, null);
            UserCourseProgress userCourseProgress = null;
            if(!userCourseProgressList.isEmpty()) {
                userCourseProgress = userCourseProgressList.get(0);
            } else {
                // For now handling all the validation through this logic
                throw new IllegalArgumentException("User Course Progress Does not exists");
            }
            UserModuleProgress userModuleProgress = userModuleProgressService.saveUserModuleProgress(userId, roleId, module.getModuleId(), userCourseProgress);

            return ResponseService.generateSuccessResponse("User Module Progress Created Successfully", userModuleProgress, HttpStatus.OK);

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

    @GetMapping("/get-filter-user-module-progress")
    public ResponseEntity<?> getFilterUserModuleProgress (
            @RequestParam(value = "module_id", required = false) String moduleIdString,
            @RequestParam(value = "user_module_progress_id", required = false) String userModuleProgressIdString,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int limit,
            @RequestHeader(value = "Authorization") String authHeader) {

        try {
            Long moduleId = null, userModuleProgressId = null;
            if(moduleIdString != null) {
                moduleId = Long.parseLong(moduleIdString);
            }
            if(userModuleProgressIdString != null) {
                userModuleProgressId = Long.parseLong(userModuleProgressIdString);
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
            List<UserModuleProgress> userModuleProgressList = userModuleProgressService.getUserModuleProgressFilter(userModuleProgressId, userId, roleId, moduleId);

            if (userModuleProgressList.isEmpty()) {
                return ResponseService.generateSuccessResponse("No User Module found with the given criteria", new ArrayList<>(), HttpStatus.OK);
            }

            // Pagination logic
            int totalItems = userModuleProgressList.size();
            int totalPages = (int) Math.ceil((double) totalItems / limit);
            int fromIndex = offset * limit;
            int toIndex = Math.min(fromIndex + limit, totalItems);

            if (offset >= totalPages && offset != 0) {
                throw new IllegalArgumentException("No more Module Progress Data available");
            }
            // Validate offset request
            if (fromIndex >= totalItems) {
                return ResponseService.generateErrorResponse("Page index out of range", HttpStatus.BAD_REQUEST);
            }

            List<UserModuleProgress> paginatedList = userModuleProgressList.subList(fromIndex, toIndex);

            // Construct paginated response
            Map<String, Object> response = new HashMap<>();
            response.put("userModuleProgress", userModuleProgressList);
            response.put("totalItems", totalItems);
            response.put("totalPages", totalPages);
            response.put("currentPage", offset);

            return ResponseService.generateSuccessResponse("User Module Progress Retrieved Successfully", response, HttpStatus.OK);

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

    @PatchMapping("/update-user-module-progress")
    public ResponseEntity<?> updateUserModuleProgress (
            @RequestParam(value = "module_id", required = false) String moduleIdString,
            @RequestParam("is_completed") Boolean isCompleted,
            @RequestParam("user_module_progress_id") String userModuleProgressIdString,
            @RequestHeader(value = "Authorization") String authHeader) {

        try {
            Long moduleId = null, userModuleProgressId = null;
            if(moduleIdString != null) {
                moduleId = Long.parseLong(moduleIdString);
            }
            if(userModuleProgressIdString != null) {
                userModuleProgressId = Long.parseLong(userModuleProgressIdString);
            }

            String jwtToken = authHeader.substring(7);
            Long roleId = jwtTokenUtil.extractRoleId(jwtToken);
            Long userId = jwtTokenUtil.extractId(jwtToken);

            // Fetch filtered products
            List<UserModuleProgress> userModuleProgressList = userModuleProgressService.getUserModuleProgressFilter(userModuleProgressId, userId, roleId, moduleId);

            if (userModuleProgressList.isEmpty()) {
                return ResponseService.generateSuccessResponse("No User Module found with the given criteria", new ArrayList<>(), HttpStatus.OK);
            }

            UserModuleProgress userModuleProgress = userModuleProgressList.get(0);
            userModuleProgress = userModuleProgressService.updateUserModuleProgress(userModuleProgress, isCompleted);

            return ResponseService.generateSuccessResponse("User Sub Topic Progress Updated Successfully", userModuleProgress, HttpStatus.OK);

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

    @PostMapping(value = "/add-course-progress")
    public ResponseEntity<?> addCourseProgress(@RequestParam("course_id") String courseIdString,
                                               @RequestParam(value = "user_semester_progress_id", required = false) String userSemesterProgressIdString,
                                               @RequestHeader(value = "Authorization") String authHeader) {
        try {

            Long courseId = Long.parseLong(courseIdString);
            Long userSemesterProgressId = null;
            if(userSemesterProgressIdString != null) {
                userSemesterProgressId = Long.parseLong(userSemesterProgressIdString);
            }

            String jwtToken = authHeader.substring(7);
            Long roleId = jwtTokenUtil.extractRoleId(jwtToken);
            Long userId = jwtTokenUtil.extractId(jwtToken);

            userCourseProgressService.validateUserCourseProgress(courseId);
            Course course = courseService.getCourseById(courseId);

            List<UserSemesterProgress> userSmesterProgressList = userSemesterProgressService.getUserSemesterProgressFilter(userSemesterProgressId, userId, roleId, null);
            UserSemesterProgress userSemesterProgress = null;
            if(!userSmesterProgressList.isEmpty()) {
                userSemesterProgress = userSmesterProgressList.get(0);
            } else {
                // For now handling all the validation through this logic
                throw new IllegalArgumentException("User Semester Progress Does not exists");
            }
            UserCourseProgress userCourseProgress = userCourseProgressService.saveUserCourseProgress(userId, roleId, course.getCourseId(), userSemesterProgress);

            return ResponseService.generateSuccessResponse("User Course Progress Created Successfully", userCourseProgress, HttpStatus.OK);

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

    @GetMapping("/get-filter-user-course-progress")
    public ResponseEntity<?> getFilterUserCourseProgress (
            @RequestParam(value = "course_id", required = false) String courseIdString,
            @RequestParam(value = "user_course_progress_id", required = false) String userCourseProgressIdString,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int limit,
            @RequestHeader(value = "Authorization") String authHeader) {

        try {
            Long courseId = null, userCourseProgressId = null;
            if(courseIdString != null) {
                courseId = Long.parseLong(courseIdString);
            }
            if(userCourseProgressIdString != null) {
                userCourseProgressId = Long.parseLong(userCourseProgressIdString);
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
            List<UserCourseProgress> userCourseProgressList = userCourseProgressService.getUserCourseProgressFilter(userCourseProgressId, userId, roleId, courseId);

            if (userCourseProgressList.isEmpty()) {
                return ResponseService.generateSuccessResponse("No User Course found with the given criteria", new ArrayList<>(), HttpStatus.OK);
            }

            // Pagination logic
            int totalItems = userCourseProgressList.size();
            int totalPages = (int) Math.ceil((double) totalItems / limit);
            int fromIndex = offset * limit;
            int toIndex = Math.min(fromIndex + limit, totalItems);

            if (offset >= totalPages && offset != 0) {
                throw new IllegalArgumentException("No more Course Progress Data available");
            }
            // Validate offset request
            if (fromIndex >= totalItems) {
                return ResponseService.generateErrorResponse("Page index out of range", HttpStatus.BAD_REQUEST);
            }

            List<UserCourseProgress> paginatedList = userCourseProgressList.subList(fromIndex, toIndex);

            // Construct paginated response
            Map<String, Object> response = new HashMap<>();
            response.put("userCourseProgress", userCourseProgressList);
            response.put("totalItems", totalItems);
            response.put("totalPages", totalPages);
            response.put("currentPage", offset);

            return ResponseService.generateSuccessResponse("User Course Progress Retrieved Successfully", response, HttpStatus.OK);

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

    @PatchMapping("/update-user-course-progress")
    public ResponseEntity<?> updateUserCourseProgress (
            @RequestParam(value = "course_id", required = false) String courseIdString,
            @RequestParam("is_completed") Boolean isCompleted,
            @RequestParam("user_course_progress_id") String userCourseProgressIdString,
            @RequestHeader(value = "Authorization") String authHeader) {

        try {
            Long courseId = null, userCourseProgressId = null;
            if(courseIdString != null) {
                courseId = Long.parseLong(courseIdString);
            }
            if(userCourseProgressIdString != null) {
                userCourseProgressId = Long.parseLong(userCourseProgressIdString);
            }

            String jwtToken = authHeader.substring(7);
            Long roleId = jwtTokenUtil.extractRoleId(jwtToken);
            Long userId = jwtTokenUtil.extractId(jwtToken);

            // Fetch filtered products
            List<UserCourseProgress> userCourseProgressList = userCourseProgressService.getUserCourseProgressFilter(userCourseProgressId, userId, roleId, courseId);

            if (userCourseProgressList.isEmpty()) {
                return ResponseService.generateSuccessResponse("No User Course found with the given criteria", new ArrayList<>(), HttpStatus.OK);
            }

            UserCourseProgress userCourseProgress = userCourseProgressList.get(0);
            userCourseProgress = userCourseProgressService.updateUserCourseProgress(userCourseProgress, isCompleted);

            return ResponseService.generateSuccessResponse("User Course Progress Updated Successfully", userCourseProgress, HttpStatus.OK);

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
