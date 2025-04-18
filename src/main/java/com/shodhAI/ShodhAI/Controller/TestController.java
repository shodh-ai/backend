package com.shodhAI.ShodhAI.Controller;

import com.shodhAI.ShodhAI.Service.ResponseService;
import com.shodhAI.ShodhAI.Service.SanitizerService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping(value = "/test", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
public class TestController {

    @Autowired
    private SanitizerService sanitizerService;

    @PostMapping("/sanitizer")
    public ResponseEntity<?> testSanitizer(@RequestBody Map<String,Object> map, HttpSession session, HttpServletRequest request) {
        return ResponseService.generateSuccessResponse("Sanitized map", sanitizerService.sanitizeInputMap(map), HttpStatus.OK);
    }

    @GetMapping("/dummy")
    public String DummyPage() {
        return "dummy123";
    }

}
