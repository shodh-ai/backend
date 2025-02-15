package com.shodhAI.ShodhAI.Controller;

import com.shodhAI.ShodhAI.Entity.PriorityLevel;
import com.shodhAI.ShodhAI.Service.ExceptionHandlingService;
import com.shodhAI.ShodhAI.Service.PriorityLevelService;
import com.shodhAI.ShodhAI.Service.ResponseService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/priority-level", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
public class PriorityLevelController {

    @Autowired
    ExceptionHandlingService exceptionHandlingService;

    @Autowired
    PriorityLevelService priorityLevelService;


    @GetMapping("/get-priority-level-by-id/{priorityLevelIdString}")
    public ResponseEntity<?> retrievePriorityLevelById(HttpServletRequest request, @PathVariable String priorityLevelIdString) {
        try {

            Long priorityLevelId = Long.parseLong(priorityLevelIdString);
            PriorityLevel priorityLevel = priorityLevelService.getPriorityLevelById(priorityLevelId);
            if (priorityLevel == null) {
                return ResponseService.generateErrorResponse("Data not present in the DB", HttpStatus.OK);
            }
            return ResponseService.generateSuccessResponse("Priority Level Retrieved Successfully", priorityLevel, HttpStatus.OK);

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

    @GetMapping("/get-all")
    public ResponseEntity<?> retrieveTopicTypes(HttpServletRequest request) {
        try {

            List<PriorityLevel> priorityLevelList = priorityLevelService.getAllPriorityLevels();
            if (priorityLevelList.isEmpty()) {
                return ResponseService.generateErrorResponse("Data not present in the DB", HttpStatus.OK);
            }
            return ResponseService.generateSuccessResponse("Priority Level Retrieved Successfully", priorityLevelList, HttpStatus.OK);

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
