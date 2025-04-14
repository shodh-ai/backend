package com.shodhAI.ShodhAI.Controller;

import com.shodhAI.ShodhAI.Entity.TopicType;
import com.shodhAI.ShodhAI.Service.ExceptionHandlingService;
import com.shodhAI.ShodhAI.Service.ResponseService;
import com.shodhAI.ShodhAI.Service.TopicTypeService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
@RequestMapping("/topic-type")
public class TopicTypeController
{
    @Autowired
    TopicTypeService topicTypeService;

    @Autowired
    ExceptionHandlingService exceptionHandlingService;

    @PostMapping("/add")
    public ResponseEntity<?> addTopicType(@RequestBody TopicType topicType) throws Exception {
        try
        {
            TopicType topicTypeToAdd=topicTypeService.addTopicType(topicType);
            return ResponseService.generateSuccessResponse("Topic Type is successfully added",topicTypeToAdd, HttpStatus.CREATED);
        }
        catch (IllegalArgumentException illegalArgumentException)
        {
            exceptionHandlingService.handleException(illegalArgumentException);
            return ResponseService.generateErrorResponse(illegalArgumentException.getMessage(),HttpStatus.BAD_REQUEST);
        }
        catch (Exception exception)
        {
            exceptionHandlingService.handleException(exception);
            return ResponseService.generateErrorResponse(exception.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    @GetMapping("/get-topic-type-by-id/{topicTypeIdString}")
    public ResponseEntity<?> retrieveTopicTypeById(HttpServletRequest request, @PathVariable String topicTypeIdString) {
        try {

            Long genderId = Long.parseLong(topicTypeIdString);
            TopicType topicType = topicTypeService.getTopicTypeById(genderId);
            if (topicType == null) {
                return ResponseService.generateErrorResponse("Data not present in the DB", HttpStatus.OK);
            }
            return ResponseService.generateSuccessResponse("Topic Type Retrieved Successfully", topicType, HttpStatus.OK);

        } catch (IllegalArgumentException illegalArgumentException) {
            exceptionHandlingService.handleException(illegalArgumentException);
            return ResponseService.generateErrorResponse("Illegal Argument exception caught: " + illegalArgumentException.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
            exceptionHandlingService.handleException(indexOutOfBoundsException);
            return ResponseService.generateErrorResponse("Index out of bound exception Caught: " + indexOutOfBoundsException.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            return ResponseService.generateErrorResponse("Exception Caught: " + exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete/{topicTypeIdString}")
    public ResponseEntity<?> deleteTopicTpe (@PathVariable String topicTypeIdString)
    {
        try
        {
            Long topicTypeId = Long.parseLong(topicTypeIdString);
            TopicType topicType = topicTypeService.getTopicTypeById(topicTypeId);
            if (topicType == null) {
                return ResponseService.generateErrorResponse("Data not present in the DB", HttpStatus.OK);
            }
            TopicType deletedTopicType =topicTypeService.deleteTopicTypeById(topicTypeId);
            return ResponseService.generateSuccessResponse("Topic type is archived successfully",deletedTopicType ,HttpStatus.OK);
        }
        catch (IllegalArgumentException illegalArgumentException)
        {
            exceptionHandlingService.handleException(illegalArgumentException);
            return ResponseService.generateErrorResponse(illegalArgumentException.getMessage(),HttpStatus.BAD_REQUEST);
        }
        catch (Exception e) {
            exceptionHandlingService.handleException(e);
            return ResponseService.generateErrorResponse(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get-filter-topic-types")
    public ResponseEntity<?> getFilterTopicTypes(
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int limit) {

        try {
            if (offset < 0) {
                throw new IllegalArgumentException("Offset for pagination cannot be a negative number");
            }
            if (limit <= 0) {
                throw new IllegalArgumentException("Limit for pagination cannot be a negative number or 0");
            }

            List<TopicType> topicTypes = topicTypeService.topicTypeFilter();

            if (topicTypes.isEmpty()) {
                return ResponseService.generateSuccessResponse("No topic types found", new ArrayList<>(), HttpStatus.OK);
            }

            // Pagination logic
            int totalItems = topicTypes.size();
            int totalPages = (int) Math.ceil((double) totalItems / limit);
            int fromIndex = offset * limit;
            int toIndex = Math.min(fromIndex + limit, totalItems);

            if (offset >= totalPages && offset != 0) {
                throw new IllegalArgumentException("No more topic types available");
            }
            if (fromIndex >= totalItems) {
                return ResponseService.generateErrorResponse("Page index out of range", HttpStatus.BAD_REQUEST);
            }

            List<TopicType> paginatedList = topicTypes.subList(fromIndex, toIndex);

            // Construct response
            Map<String, Object> response = new HashMap<>();
            response.put("topicTypes", paginatedList);
            response.put("totalItems", totalItems);
            response.put("totalPages", totalPages);
            response.put("currentPage", offset);

            return ResponseService.generateSuccessResponse("Topic Types Retrieved Successfully", response, HttpStatus.OK);

        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            return ResponseService.generateErrorResponse(exception.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
