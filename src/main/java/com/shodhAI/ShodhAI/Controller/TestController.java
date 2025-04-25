package com.shodhAI.ShodhAI.Controller;

import com.shodhAI.ShodhAI.Entity.Student;
import com.shodhAI.ShodhAI.Service.ExceptionHandlingService;
import com.shodhAI.ShodhAI.Service.ResponseService;
import com.shodhAI.ShodhAI.Service.SanitizerService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private SanitizerService sanitizerService;

    @Autowired
    private ExceptionHandlingService exceptionHandlingService;

    @PostMapping("/sanitizer")
    public ResponseEntity<?> testSanitizer(@RequestBody Map<String,Object> map, HttpSession session, HttpServletRequest request) throws Exception {
        try {
            Student student = new Student();
            student.setFirstName("raman");
            List<Object> data = new ArrayList<>();
            data.add(student);
            data.add(map);
            sanitizerService.sanitizeInputMap(data);
            return ResponseService.generateSuccessResponse("Sanitized map", "successfully validated", HttpStatus.OK);
        } catch (IllegalArgumentException illegalArgumentException) {
            exceptionHandlingService.handleException(illegalArgumentException);
            return ResponseService.generateErrorResponse("Illegal Exception Caught: " + illegalArgumentException.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            return ResponseService.generateErrorResponse("Exception Caught: " + exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/test12")
    public String testSanitizer() {
        return "hello";
    }

}
