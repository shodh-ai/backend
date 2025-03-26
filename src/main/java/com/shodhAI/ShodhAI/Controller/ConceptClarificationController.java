package com.shodhAI.ShodhAI.Controller;
import com.shodhAI.ShodhAI.Entity.ConceptClarification;
import com.shodhAI.ShodhAI.Service.ConceptClarificationService;
import com.shodhAI.ShodhAI.Service.ExceptionHandlingService;
import com.shodhAI.ShodhAI.Service.ResponseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/concept-clarification")
public class ConceptClarificationController {

    private final ConceptClarificationService conceptClarificationService;
    private final ExceptionHandlingService exceptionHandlingService;

    @Autowired
    public ConceptClarificationController(ConceptClarificationService conceptClarificationService, ExceptionHandlingService exceptionHandlingService) {
        this.conceptClarificationService = conceptClarificationService;
        this.exceptionHandlingService= exceptionHandlingService;
    }

    @PostMapping("/request/{studentId}")
    public ResponseEntity<?> requestConceptClarification(@PathVariable Long studentId, @RequestParam String term, @RequestParam(required = false) Long courseId) {
        try{
            ConceptClarification clarification = conceptClarificationService.requestConceptClarification(studentId, term, courseId);
            return ResponseService.generateSuccessResponse("Concept clarification request is raised for given term",clarification, HttpStatus.OK);
        }
        catch (IllegalArgumentException illegalArgumentException)
        {
            exceptionHandlingService.handleException(illegalArgumentException);
           return ResponseService.generateErrorResponse(illegalArgumentException.getMessage(),HttpStatus.BAD_REQUEST);
        }catch (Exception exception)
        {
            exceptionHandlingService.handleException(exception);
            return ResponseService.generateErrorResponse(exception.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get-clarification/{clarificationId}")
    public ResponseEntity<?> getConceptClarification(@PathVariable Long clarificationId) {
        try
        {
            ConceptClarification clarification = conceptClarificationService.getConceptClarification(clarificationId);
            return ResponseService.generateSuccessResponse("Clarification detail is fetched successfully",clarification,HttpStatus.OK);
        }
        catch (IllegalArgumentException illegalArgumentException)
        {
            exceptionHandlingService.handleException(illegalArgumentException);
            return ResponseService.generateErrorResponse(illegalArgumentException.getMessage(),HttpStatus.BAD_REQUEST);
        }catch (Exception exception)
        {
            exceptionHandlingService.handleException(exception);
            return ResponseService.generateErrorResponse(exception.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}