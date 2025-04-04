package com.shodhAI.ShodhAI.Controller;

import com.shodhAI.ShodhAI.Entity.Role;
import com.shodhAI.ShodhAI.Service.ExceptionHandlingService;
import com.shodhAI.ShodhAI.Service.ResponseService;
import com.shodhAI.ShodhAI.Service.RoleService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/role", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
public class RoleController {

    @Autowired
    RoleService roleService;

    @Autowired
    ExceptionHandlingService exceptionHandlingService;

    @Transactional
    @GetMapping("/get-all")
    public ResponseEntity<?> retrieveAllRoles(@RequestParam(defaultValue = "0") int offset, @RequestParam(defaultValue = "10") int limit, HttpServletRequest request) {
        try {
            if (offset < 0) {
                throw new IllegalArgumentException("Offset for pagination cannot be a negative number");
            }
            if (limit <= 0) {
                throw new IllegalArgumentException("Limit for pagination cannot be a negative number or 0");
            }
            List<Role> roleList = roleService.getAllRole();
            if (roleList.isEmpty()) {
                return ResponseService.generateErrorResponse("Data not present in the DB", HttpStatus.OK);
            }
            int totalItems = roleList.size();
            int totalPages = (int) Math.ceil((double) totalItems / limit);
            int fromIndex = offset * limit;
            int toIndex = Math.min(fromIndex + limit, totalItems);

            if (offset >= totalPages && offset != 0) {
                throw new IllegalArgumentException("No more roles available");
            }

            if (fromIndex >= totalItems) {
                return ResponseService.generateErrorResponse("Page index out of range", HttpStatus.BAD_REQUEST);
            }

            List<Role> roles = roleList.subList(fromIndex, toIndex);

            Map<String, Object> response = new HashMap<>();
            response.put("roles", roles);
            response.put("totalItems", totalItems);
            response.put("totalPages", totalPages);
            response.put("currentPage", offset);
            return ResponseService.generateSuccessResponse("Role Retrieved Successfully", response, HttpStatus.OK);

        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            return ResponseService.generateErrorResponse("Exception Caught: " + exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    @GetMapping("/get-role-by-id/{roleIdString}")
    public ResponseEntity<?> retrieveGenderById(HttpServletRequest request, @PathVariable String roleIdString) {
        try {

            Long roleId = Long.parseLong(roleIdString);
            Role role = roleService.getRoleById(roleId);
            if (role == null) {
                return ResponseService.generateErrorResponse("Data not present in the DB", HttpStatus.OK);
            }
            return ResponseService.generateSuccessResponse("Role Retrieved Successfully", role, HttpStatus.OK);

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
}
