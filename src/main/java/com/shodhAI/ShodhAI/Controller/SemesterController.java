package com.shodhAI.ShodhAI.Controller;

import com.shodhAI.ShodhAI.Component.JwtUtil;
import com.shodhAI.ShodhAI.Dto.SemesterDto;
import com.shodhAI.ShodhAI.Entity.Course;
import com.shodhAI.ShodhAI.Entity.Semester;
import com.shodhAI.ShodhAI.Service.ExceptionHandlingService;
import com.shodhAI.ShodhAI.Service.ResponseService;
import com.shodhAI.ShodhAI.Service.SemesterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/semester")
public class SemesterController
{

    @Autowired
    private SemesterService semesterService;
    @Autowired
    ExceptionHandlingService exceptionHandlingService;

    @Autowired
    JwtUtil jwtTokenUtil;

    @PostMapping("/add")
    public ResponseEntity<?> createSemester(@RequestBody SemesterDto semesterDto) throws ParseException {
        try
        {
            semesterService.validateSemester(semesterDto);
            Semester semesterToAdd=semesterService.saveSemester(semesterDto);
            return ResponseService.generateSuccessResponse("Semester is created successfully",semesterToAdd,HttpStatus.CREATED);
        }
        catch (IllegalArgumentException illegalArgumentException)
        {
            return ResponseService.generateErrorResponse(illegalArgumentException.getMessage(), HttpStatus.BAD_REQUEST);
        }
        catch (Exception exception)
        {
            return ResponseService.generateErrorResponse(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get-all")
    public ResponseEntity<?> retrieveAllSemesters() {
        try {

            List<Semester> semesterList = semesterService.getAllSemesters();
            if (semesterList.isEmpty()) {
                return ResponseService.generateSuccessResponse("Semester list is empty in Database",Collections.emptyList(), HttpStatus.OK);
            }
            return ResponseService.generateSuccessResponse("Semester list is Retrieved Successfully", semesterList, HttpStatus.OK);

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

    @GetMapping("/get-semester-by-id/{semesterIdString}")
    public ResponseEntity<?> retrieveSemesterById(@PathVariable String semesterIdString) {
        try {

            Long semesterIdLong = Long.parseLong(semesterIdString);
            Semester semesterToFind = semesterService.getSemesterById(semesterIdLong);
            if (semesterToFind == null) {
                return ResponseService.generateErrorResponse("Semester with id " + semesterIdLong + " does not exist",  HttpStatus.BAD_REQUEST);
            }
            return ResponseService.generateSuccessResponse("Semester is Retrieved Successfully", semesterToFind, HttpStatus.OK);

        }  catch (IllegalArgumentException illegalArgumentException) {
            exceptionHandlingService.handleException(illegalArgumentException);
            return ResponseService.generateErrorResponse("Illegal Exception Caught: " + illegalArgumentException.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            return ResponseService.generateErrorResponse("Exception Caught: " + exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/update/{semesterIdString}")
    public ResponseEntity<?> updateSemester(@RequestBody SemesterDto semesterDto, @PathVariable String semesterIdString)
    {
        try
        {
            Long semesterId= Long.parseLong(semesterIdString);

            Semester semesterToFind = semesterService.getSemesterById(semesterId);
            if (semesterToFind == null) {
                return ResponseService.generateErrorResponse("Semester with id " + semesterId + " does not exist",  HttpStatus.BAD_REQUEST);
            }
            Semester semesterToUpdate=semesterService.updateSemester(semesterId,semesterDto);
            return ResponseService.generateSuccessResponse("Semester is updated successfully", semesterToUpdate,HttpStatus.OK);

        }catch (IllegalArgumentException e) {
            exceptionHandlingService.handleException(e);
            return ResponseService.generateErrorResponse(e.getMessage(),HttpStatus.BAD_REQUEST);
        }catch (Exception e) {
            exceptionHandlingService.handleException(e);
            return ResponseService.generateErrorResponse(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get-filter-semester")
    public ResponseEntity<?> getFilterSemester(
            @RequestParam(required = false)Long  semesterId,
            @RequestParam(required = false) Long  academicDegreeId,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int limit,
            @RequestHeader(value = "Authorization") String authHeader) {

        try {
            if (offset < 0) {
                throw new IllegalArgumentException("Offset for pagination cannot be a negative number");
            }
            if (limit <= 0) {
                throw new IllegalArgumentException("Limit for pagination cannot be a negative number or 0");
            }

            String jwtToken = authHeader.substring(7);
            Long roleId = jwtTokenUtil.extractRoleId(jwtToken);
            Long userId = jwtTokenUtil.extractId(jwtToken);


            List<Semester> semesters = semesterService.semesterFilter(semesterId, userId, roleId, academicDegreeId);

            if (semesters.isEmpty()) {
                return ResponseService.generateSuccessResponse("No semesters found with the given criteria", new ArrayList<>(), HttpStatus.OK);
            }

            // Pagination logic
            int totalItems = semesters.size();
            int totalPages = (int) Math.ceil((double) totalItems / limit);
            int fromIndex = offset * limit;
            int toIndex = Math.min(fromIndex + limit, totalItems);

            if (offset >= totalPages && offset != 0) {
                throw new IllegalArgumentException("No more semesters available");
            }
            // Validate offset request
            if (fromIndex >= totalItems) {
                return ResponseService.generateErrorResponse("Page index out of range", HttpStatus.BAD_REQUEST);
            }

            List<Semester> paginatedList = semesters.subList(fromIndex, toIndex);

            // Construct paginated response
            Map<String, Object> response = new HashMap<>();
            response.put("semesters", semesters);
            response.put("totalItems", totalItems);
            response.put("totalPages", totalPages);
            response.put("currentPage", offset);

            return ResponseService.generateSuccessResponse("Semesters Retrieved Successfully", response, HttpStatus.OK);

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
