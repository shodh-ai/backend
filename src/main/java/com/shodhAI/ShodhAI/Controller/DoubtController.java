package com.shodhAI.ShodhAI.Controller;

import com.shodhAI.ShodhAI.Component.JwtUtil;
import com.shodhAI.ShodhAI.Dto.DoubtDto;
import com.shodhAI.ShodhAI.Entity.Doubt;
import com.shodhAI.ShodhAI.Entity.DoubtLevel;
import com.shodhAI.ShodhAI.Service.DoubtService;
import com.shodhAI.ShodhAI.Service.ExceptionHandlingService;
import com.shodhAI.ShodhAI.Service.ResponseService;
import jakarta.persistence.PersistenceException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/doubt", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
public class DoubtController {

    @Autowired
    DoubtService doubtService;

    @Autowired
    JwtUtil jwtTokenUtil;

    @Autowired
    ExceptionHandlingService exceptionHandlingService;

    @PostMapping(value = "/ask-doubt")
    public ResponseEntity<?> addDoubt(HttpServletRequest request, @RequestBody DoubtDto doubtDto, @RequestHeader(value = "Authorization") String authHeader) {
        try {

            String jwtToken = authHeader.substring(7);
            Long roleId = jwtTokenUtil.extractRoleId(jwtToken);
            Long userId = jwtTokenUtil.extractId(jwtToken);

            doubtService.validateDoubt(doubtDto);
            Doubt doubt = doubtService.saveDoubt(doubtDto);

            doubtService.saveStudentDoubtLinkage(userId, roleId, doubt);
            return ResponseService.generateSuccessResponse("Doubt Resolved Successfully", doubt, HttpStatus.OK);

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

    @GetMapping("/get-doubt-level-by-id/{doubtLevelIdString}")
    public ResponseEntity<?> retrieveDoubtLevelById(HttpServletRequest request, @PathVariable String doubtLevelIdString) {
        try {

            Long doubtLevelId = Long.parseLong(doubtLevelIdString);
            DoubtLevel doubtLevel = doubtService.getDoubtLevelById(doubtLevelId);
            if (doubtLevel == null) {
                return ResponseService.generateErrorResponse("Data not present in the DB", HttpStatus.OK);
            }
            return ResponseService.generateSuccessResponse("Doubt Level Retrieved Successfully", doubtLevel, HttpStatus.OK);

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

    @GetMapping("/get-all-doubt-level")
    public ResponseEntity<?> retrieveDoubtLevel(@RequestParam(defaultValue = "0") int offset, @RequestParam(defaultValue = "10") int limit, HttpServletRequest request) {
        try {
            if (offset < 0) {
                throw new IllegalArgumentException("Offset for pagination cannot be a negative number");
            }
            if (limit <= 0) {
                throw new IllegalArgumentException("Limit for pagination cannot be a negative number or 0");
            }
            List<DoubtLevel> doubtLevelList = doubtService.getAllDoubtLevels();
            if (doubtLevelList.isEmpty()) {
                return ResponseService.generateErrorResponse("Data not present in the DB", HttpStatus.OK);
            }
            int totalItems = doubtLevelList.size();
            int totalPages = (int) Math.ceil((double) totalItems / limit);
            int fromIndex = offset * limit;
            int toIndex = Math.min(fromIndex + limit, totalItems);

            if (offset >= totalPages && offset != 0) {
                throw new IllegalArgumentException("No more doubts available");
            }

            if (fromIndex >= totalItems) {
                return ResponseService.generateErrorResponse("Page index out of range", HttpStatus.BAD_REQUEST);
            }

            List<DoubtLevel> doubtLevels = doubtLevelList.subList(fromIndex, toIndex);

            Map<String, Object> response = new HashMap<>();
            response.put("doubtLevels", doubtLevels);
            response.put("totalItems", totalItems);
            response.put("totalPages", totalPages);
            response.put("currentPage", offset);

            return ResponseService.generateSuccessResponse("Doubt Level Retrieved Successfully", response, HttpStatus.OK);

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
