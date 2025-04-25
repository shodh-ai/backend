package com.shodhAI.ShodhAI.Controller;

import com.shodhAI.ShodhAI.Entity.QuestionType;
import com.shodhAI.ShodhAI.Service.ExceptionHandlingService;
import com.shodhAI.ShodhAI.Service.QuestionTypeService;
import com.shodhAI.ShodhAI.Service.ResponseService;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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
@RequestMapping(value = "/question-type", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
public class QuestionTypeController {

    @Autowired
    EntityManager entityManager;

    @Autowired
    QuestionTypeService questionTypeService;

    @Autowired
    ExceptionHandlingService exceptionHandlingService;

    @GetMapping("/get-all")
    public ResponseEntity<?> retrieveAllQuestionType(@RequestParam(defaultValue = "0") int offset, @RequestParam(defaultValue = "10") int limit, HttpServletRequest request) {
        try {
            if (offset < 0) {
                throw new IllegalArgumentException("Offset for pagination cannot be a negative number");
            }
            if (limit <= 0) {
                throw new IllegalArgumentException("Limit for pagination cannot be a negative number or 0");
            }
            List<QuestionType> questionTypeList = questionTypeService.getAllQuestionTypes()                                           ;
            if (questionTypeList.isEmpty()) {
                return ResponseService.generateErrorResponse("Data not present in the DB", HttpStatus.OK);
            }
            int totalItems = questionTypeList.size();
            int totalPages = (int) Math.ceil((double) totalItems / limit);
            int fromIndex = offset * limit;
            int toIndex = Math.min(fromIndex + limit, totalItems);

            if (offset >= totalPages && offset != 0) {
                throw new IllegalArgumentException("No more question types available");
            }

            if (fromIndex >= totalItems) {
                return ResponseService.generateErrorResponse("Page index out of range", HttpStatus.BAD_REQUEST);
            }

            List<QuestionType> questionTypes = questionTypeList.subList(fromIndex, toIndex);

            Map<String, Object> response = new HashMap<>();
            response.put("questionTypes", questionTypes);
            response.put("totalItems", totalItems);
            response.put("totalPages", totalPages);
            response.put("currentPage", offset);
            return ResponseService.generateSuccessResponse("Question Type Retrieved Successfully", response, HttpStatus.OK);

        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            return ResponseService.generateErrorResponse("Exception Caught: " + exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get-question-type-by-id/{questionTypeIdString}")
    public ResponseEntity<?> retrieveQuestionTypeById(HttpServletRequest request, @PathVariable String questionTypeIdString) {
        try {

            Long genderId = Long.parseLong(questionTypeIdString);
            QuestionType questionType = questionTypeService.getQuestionTypeById(genderId);
            if (questionType == null) {
                return ResponseService.generateErrorResponse("Data not present in the DB", HttpStatus.OK);
            }
            return ResponseService.generateSuccessResponse("Question Type Retrieved Successfully", questionType, HttpStatus.OK);

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

    @PostMapping("/add")
    public ResponseEntity<?> addQuestionType(@RequestBody QuestionType questionType)
    {
        try
        {
            QuestionType questionTypeToSave=questionTypeService.addQuestionType(questionType);
            return ResponseService.generateSuccessResponse("Question type is successfully added",questionTypeToSave, HttpStatus.CREATED);
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

    @DeleteMapping("/delete/{questionTypeIdString}")
    public ResponseEntity<?> deleteQuestionTpe (@PathVariable String questionTypeIdString)
    {
        try
        {
            Long questionTypeId = Long.parseLong(questionTypeIdString);
            QuestionType questionType = questionTypeService.getQuestionTypeById(questionTypeId);
            if (questionType == null) {
                return ResponseService.generateErrorResponse("Data not present in the DB", HttpStatus.OK);
            }
            QuestionType deletedQuestionType =questionTypeService.deleteQuestionTypeById(questionTypeId);
            return ResponseService.generateSuccessResponse("Question type is archived successfully",deletedQuestionType ,HttpStatus.OK);
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

    @PatchMapping("/update/{questionTypeIdString}")
    public ResponseEntity<?> updateQuestionType(@RequestBody QuestionType questionType, @PathVariable String questionTypeIdString)
    {
        try {
            Long questionTypeId = Long.parseLong(questionTypeIdString);
            QuestionType questionTypeToUpdate=questionTypeService.getQuestionTypeById(questionTypeId);
            if (questionTypeToUpdate == null) {
                return ResponseService.generateErrorResponse("Data not present in the DB", HttpStatus.OK);
            }
            QuestionType updatedQuestion= questionTypeService.updateQuestionType(questionTypeId,questionType);
            return ResponseService.generateSuccessResponse("Question type is updated successfully ", updatedQuestion,HttpStatus.OK);
        }
        catch (IllegalArgumentException e) {
            exceptionHandlingService.handleException(e);
            return ResponseService.generateErrorResponse(e.getMessage(),HttpStatus.BAD_REQUEST);
        }
        catch (Exception e) {
            exceptionHandlingService.handleException(e);
            return ResponseService.generateErrorResponse(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get-filter-question-types")
    public ResponseEntity<?> getFilterQuestionTypes(
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int limit) {

        try {
            if (offset < 0) {
                throw new IllegalArgumentException("Offset for pagination cannot be a negative number");
            }
            if (limit <= 0) {
                throw new IllegalArgumentException("Limit for pagination cannot be a negative number or 0");
            }

            List<QuestionType> questionTypes = questionTypeService.questionTypeFilter();

            if (questionTypes.isEmpty()) {
                return ResponseService.generateSuccessResponse("No question types found", new ArrayList<>(), HttpStatus.OK);
            }

            // Pagination logic
            int totalItems = questionTypes.size();
            int totalPages = (int) Math.ceil((double) totalItems / limit);
            int fromIndex = offset * limit;
            int toIndex = Math.min(fromIndex + limit, totalItems);

            if (offset >= totalPages && offset != 0) {
                throw new IllegalArgumentException("No more question types available");
            }
            if (fromIndex >= totalItems) {
                return ResponseService.generateErrorResponse("Page index out of range", HttpStatus.BAD_REQUEST);
            }

            List<QuestionType> paginatedList = questionTypes.subList(fromIndex, toIndex);

            // Construct response
            Map<String, Object> response = new HashMap<>();
            response.put("questionTypes", paginatedList);
            response.put("totalItems", totalItems);
            response.put("totalPages", totalPages);
            response.put("currentPage", offset);

            return ResponseService.generateSuccessResponse("Question Types Retrieved Successfully", response, HttpStatus.OK);

        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            return ResponseService.generateErrorResponse(exception.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

}
