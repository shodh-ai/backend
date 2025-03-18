package com.shodhAI.ShodhAI.Controller;


import com.shodhAI.ShodhAI.Component.JwtUtil;
import com.shodhAI.ShodhAI.Dto.SessionDto;
import com.shodhAI.ShodhAI.Dto.SessionFilterDto;
import com.shodhAI.ShodhAI.Entity.Session;
import com.shodhAI.ShodhAI.Service.ExceptionHandlingService;
import com.shodhAI.ShodhAI.Service.ResponseService;
import com.shodhAI.ShodhAI.Service.SessionService;
import jakarta.persistence.PersistenceException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/session", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
public class SessionController {

    @Autowired
    SessionService sessionService;

    @Autowired
    JwtUtil jwtTokenUtil;

    @Autowired
    ExceptionHandlingService exceptionHandlingService;

    @PostMapping(value = "/create-session")
    public ResponseEntity<?> addSession(HttpServletRequest request, @RequestBody SessionDto sessionDto, @RequestHeader(value = "Authorization") String authHeader) {
        try {

            String jwtToken = authHeader.substring(7);
            Long roleId = jwtTokenUtil.extractRoleId(jwtToken);
            Long userId = jwtTokenUtil.extractId(jwtToken);

            sessionService.validateSession(sessionDto);
            Session session = sessionService.saveSession(userId, roleId, sessionDto);

            return ResponseService.generateSuccessResponse("Session Created Successfully", session, HttpStatus.OK);

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


    @GetMapping("/get-filter-session")
    public ResponseEntity<?> getFilterProducts(
            @RequestBody SessionFilterDto sessionDto,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int limit) {

        try {
            if(offset<0)
            {
                throw new IllegalArgumentException("Offset for pagination cannot be a negative number");
            }
            if(limit<=0)
            {
                throw new IllegalArgumentException("Limit for pagination cannot be a negative number or 0");
            }

            // Fetch filtered products
            List<Session> sessions = sessionService.sessionFilter(sessionDto.getSessionId(), sessionDto.getUserId(), sessionDto.getRoleId(), sessionDto.getTopicId(), sessionDto.getQuestionTypeId());

            if (sessions.isEmpty()) {
                return ResponseService.generateSuccessResponse("No Sessions found with the given criteria", new ArrayList<>(), HttpStatus.OK);
            }

            // Pagination logic
            int totalItems = sessions.size();
            int totalPages = (int) Math.ceil((double) totalItems / limit);
            int fromIndex = offset * limit;
            int toIndex = Math.min(fromIndex + limit, totalItems);

            if (offset >= totalPages && offset != 0) {
                throw new IllegalArgumentException("No more products available");
            }
            // Validate offset request
            if (fromIndex >= totalItems) {
                return ResponseService.generateErrorResponse("Page index out of range", HttpStatus.BAD_REQUEST);
            }

            List<Session> paginatedList = sessions.subList(fromIndex, toIndex);

            // Construct paginated response
            Map<String, Object> response = new HashMap<>();
            response.put("session", sessions);
            response.put("totalItems", totalItems);
            response.put("totalPages", totalPages);
            response.put("currentPage", offset);

            return ResponseService.generateSuccessResponse("Session Retrieved Successfully", response, HttpStatus.OK);

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
