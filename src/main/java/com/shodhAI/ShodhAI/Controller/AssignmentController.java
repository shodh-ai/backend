package com.shodhAI.ShodhAI.Controller;

import com.shodhAI.ShodhAI.Dto.AssignmentDto;
import com.shodhAI.ShodhAI.Entity.Assignment;
import com.shodhAI.ShodhAI.Service.AssignmentService;
import com.shodhAI.ShodhAI.Service.ExceptionHandlingService;
import com.shodhAI.ShodhAI.Service.QuestionService;
import com.shodhAI.ShodhAI.Service.ResponseService;
import com.shodhAI.ShodhAI.Service.TopicService;
import jakarta.persistence.PersistenceException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/assignment", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
@Slf4j
public class AssignmentController {

    @Autowired
    ExceptionHandlingService exceptionHandlingService;

    @Autowired
    TopicService topicService;

    @Autowired
    QuestionService questionService;

    @Autowired
    AssignmentService assignmentService;

    @PostMapping(value = "/add")
    public ResponseEntity<?> addAssignment(@RequestBody AssignmentDto assignmentDto) {
        try {
            assignmentService.validateAssignment(assignmentDto);
            Assignment assignment = assignmentService.saveAssignment(assignmentDto);
            return ResponseService.generateSuccessResponse("Assignment Created Successfully", assignment, HttpStatus.OK);

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

    @GetMapping("/get-assignment-by-id/{assignmentIdString}")
    public ResponseEntity<?> retrieveAssignmentById(HttpServletRequest request, @PathVariable String assignmentIdString) {
        try {

            Long assignmentId = Long.parseLong(assignmentIdString);
            Assignment assignment = assignmentService.getAssignmentById(assignmentId);

            if (assignment == null) {
                return ResponseService.generateErrorResponse("Data not present in the DB", HttpStatus.OK);
            }
            return ResponseService.generateSuccessResponse("Assignment Retrieved Successfully", assignment, HttpStatus.OK);

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

    @GetMapping(value = "/get-filter-assignment")
    public ResponseEntity<?> getAssignment(@RequestParam(value = "topic_id", required = false) Long topicId) {
        try {
            assignmentService.validateAssignmentParameters(topicId);
            List<Assignment> assignmentList = assignmentService.filterAssignments(topicId);
            return ResponseService.generateSuccessResponse("Assignments fetched Successfully", assignmentList, HttpStatus.OK);

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
