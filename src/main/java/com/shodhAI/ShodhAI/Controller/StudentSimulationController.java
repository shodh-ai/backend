package com.shodhAI.ShodhAI.Controller;

import com.shodhAI.ShodhAI.Entity.StudentSimulationProgress;
import com.shodhAI.ShodhAI.Service.ExceptionHandlingService;
import com.shodhAI.ShodhAI.Service.ResponseService;
import com.shodhAI.ShodhAI.Service.StudentSimulationProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/simulation")
public class StudentSimulationController {

    @Autowired
    private StudentSimulationProgressService studentSimulationProgressService;

    @Autowired
    private ExceptionHandlingService exceptionHandlingService;

    @PostMapping("/update-timestamp")
    public ResponseEntity<?> updateProgress(@RequestParam Long studentId,
                                            @RequestParam Long topicId,
                                            @RequestParam Double timestamp) {
        try {
            StudentSimulationProgress studentSimulationProgress = studentSimulationProgressService.updateSimulationProgress(studentId, topicId, timestamp);
            return ResponseService.generateSuccessResponse("Progress updated successfully.", studentSimulationProgress, HttpStatus.OK);
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            return ResponseService.generateErrorResponse("Exception Caught: " + exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get-timestamp")
    public ResponseEntity<?> getProgress(@RequestParam Long studentId,
                                         @RequestParam Long topicId) {
        try {
            StudentSimulationProgress studentSimulationProgress = studentSimulationProgressService.getStudentSimulationProgress(studentId, topicId);
            return ResponseService.generateSuccessResponse("Progress Fetched successfully.", studentSimulationProgress, HttpStatus.OK);
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            return ResponseService.generateErrorResponse("Exception Caught: " + exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

