package com.shodhAI.ShodhAI.Controller;

import com.shodhAI.ShodhAI.Component.Constant;
import com.shodhAI.ShodhAI.Dto.AcademicDegreeDto;
import com.shodhAI.ShodhAI.Entity.AcademicDegree;
import com.shodhAI.ShodhAI.Service.AcademicDegreeService;
import com.shodhAI.ShodhAI.Service.ExceptionHandlingService;
import com.shodhAI.ShodhAI.Service.ResponseService;
import com.shodhAI.ShodhAI.annotation.Authorize;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/academic-degree", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
public class AcademicDegreeController {

    @Autowired
    EntityManager entityManager;

    @Autowired
    AcademicDegreeService academicDegreeService;

    @Autowired
    ExceptionHandlingService exceptionHandlingService;

//    @Authorize(value = {Constant.ROLE_SUPER_ADMIN,Constant.ROLE_ADMIN})
    @PostMapping("/add")
    public ResponseEntity<?> addAcademicDegree(@RequestBody AcademicDegreeDto academicDegreeDto) {
        try {

            academicDegreeService.validateAcademicDegree(academicDegreeDto);
            AcademicDegree academicDegree = academicDegreeService.saveAcademicDegree(academicDegreeDto);

            return ResponseService.generateSuccessResponse("Academic Degree Created Successfully", academicDegree, HttpStatus.OK);

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

    @GetMapping("/get-all")
    public ResponseEntity<?> retrieveAllAcademicDegree(HttpServletRequest request,
                                                        @RequestParam(defaultValue = "0") int offset,
                                                        @RequestParam(defaultValue = "10") int limit) {
        try {

            if (offset < 0) {
                throw new IllegalArgumentException("Offset for pagination cannot be a negative number");
            }
            if (limit <= 0) {
                throw new IllegalArgumentException("Limit for pagination cannot be a negative number or 0");
            }

            List<AcademicDegree> academicDegreeList = academicDegreeService.getAllAcademicDegree();
            if (academicDegreeList.isEmpty()) {
                return ResponseService.generateErrorResponse("Data not present in the DB", HttpStatus.OK);
            }

            // Pagination logic
            int totalItems = academicDegreeList.size();
            int totalPages = (int) Math.ceil((double) totalItems / limit);
            int fromIndex = offset * limit;
            int toIndex = Math.min(fromIndex + limit, totalItems);

            if (offset >= totalPages && offset != 0) {
                throw new IllegalArgumentException("No more Academic Degree available");
            }

            // Validate offset request
            if (fromIndex >= totalItems) {
                return ResponseService.generateErrorResponse("Page index out of range", HttpStatus.BAD_REQUEST);
            }

            List<AcademicDegree> paginatedList = academicDegreeList.subList(fromIndex, toIndex);

            // Construct paginated response
            Map<String, Object> response = new HashMap<>();
            response.put("academicDegree", paginatedList);
            response.put("totalItems", totalItems);
            response.put("totalPages", totalPages);
            response.put("currentPage", offset);

            return ResponseService.generateSuccessResponse("Academic Degree Retrieved Successfully", response, HttpStatus.OK);

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

    @GetMapping("/get-academic-degree-by-id/{academicDegreeIdString}")
    public ResponseEntity<?> retrieveAcademicDegreeById(HttpServletRequest request, @PathVariable String academicDegreeIdString) {
        try {

            Long academicDegreeId = Long.parseLong(academicDegreeIdString);
            AcademicDegree academicDegree = academicDegreeService.getAcademicDegreeById(academicDegreeId);
            if (academicDegree == null) {
                return ResponseService.generateErrorResponse("Data not present in the DB", HttpStatus.OK);
            }
            return ResponseService.generateSuccessResponse("Academic Degree Retrieved Successfully", academicDegree, HttpStatus.OK);

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

    @Authorize(value = {Constant.ROLE_SUPER_ADMIN,Constant.ROLE_ADMIN})
    @PatchMapping("/update/{academicDegreeIdString}")
    public ResponseEntity<?> updateAcademicDegree(@RequestBody AcademicDegreeDto academicDegreeDto,@PathVariable String academicDegreeIdString)
    {
        try {
            Long academicDegreeId = Long.parseLong(academicDegreeIdString);
            AcademicDegree academicDegree = academicDegreeService.getAcademicDegreeById(academicDegreeId);
            if (academicDegree == null) {
                return ResponseService.generateErrorResponse("Data not present in the DB", HttpStatus.OK);
            }
            AcademicDegree updatedAcademicDegree=  academicDegreeService.updateAcademicDegree(academicDegreeId,academicDegreeDto);
            return ResponseService.generateSuccessResponse("Academic Degree is updated successfully", updatedAcademicDegree,HttpStatus.OK);

        } catch (IllegalArgumentException illegalArgumentException) {
            exceptionHandlingService.handleException(illegalArgumentException);
            return ResponseService.generateErrorResponse("Illegal Exception Caught: " + illegalArgumentException.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            return ResponseService.generateErrorResponse("Exception Caught: " + exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
