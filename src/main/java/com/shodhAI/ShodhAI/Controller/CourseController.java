package com.shodhAI.ShodhAI.Controller;

import com.shodhAI.ShodhAI.Component.Constant;
import com.shodhAI.ShodhAI.Component.JwtUtil;
import com.shodhAI.ShodhAI.Dto.CourseDto;
import com.shodhAI.ShodhAI.Entity.Course;
import com.shodhAI.ShodhAI.Service.CourseService;
import com.shodhAI.ShodhAI.Service.ExceptionHandlingService;
import com.shodhAI.ShodhAI.Service.ResponseService;
import com.shodhAI.ShodhAI.annotation.Authorize;
import jakarta.persistence.PersistenceException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
@RequestMapping(value = "/course", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
public class CourseController {

    @Autowired
    ExceptionHandlingService exceptionHandlingService;

    @Autowired
    CourseService courseService;

    @Autowired
    JwtUtil jwtTokenUtil;

    @Authorize(value = {Constant.ROLE_SUPER_ADMIN,Constant.ROLE_ADMIN})
    @PostMapping(value = "/add")
    public ResponseEntity<?> addStudent(@RequestBody CourseDto courseDto) {
        try {

            courseService.validateCourse(courseDto);
            Course course = courseService.saveCourse(courseDto);

            return ResponseService.generateSuccessResponse("Course Created Successfully", course, HttpStatus.OK);

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

    @GetMapping("/get-all")
    public ResponseEntity<?> retrieveAllCourse(HttpServletRequest request) {
        try {

            List<Course> courseList = courseService.getAllCourse();
            if (courseList.isEmpty()) {
                return ResponseService.generateErrorResponse("Data not present in the DB", HttpStatus.OK);
            }
            return ResponseService.generateSuccessResponse("Course Retrieved Successfully", courseList, HttpStatus.OK);

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

    @GetMapping("/get-course-by-id/{courseIdString}")
    public ResponseEntity<?> retrieveCourseById(HttpServletRequest request, @PathVariable String courseIdString) {
        try {

            Long courseId = Long.parseLong(courseIdString);
            Course course = courseService.getCourseById(courseId);
            if (course == null) {
                return ResponseService.generateErrorResponse("Data not present in the DB", HttpStatus.OK);
            }
            return ResponseService.generateSuccessResponse("Course Retrieved Successfully", course, HttpStatus.OK);

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
    @PatchMapping("/update/{courseIdString}")
    public ResponseEntity<?> updateFaculty( @PathVariable String courseIdString,@RequestBody CourseDto courseDto) {
        try {
            Long courseId = Long.parseLong(courseIdString);
            Course course = courseService.updateCourse(courseId,courseDto);
            return ResponseService.generateSuccessResponse("Course updated Successfully", course, HttpStatus.OK);
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

    @GetMapping("/get-filter-course")
    public ResponseEntity<?> getFilterCourse(
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) Long semesterId,
            @RequestParam(required = false) Long degreeId,
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

            List<Course> courses = courseService.courseFilter(courseId, userId, roleId, semesterId, degreeId);

            if (courses.isEmpty()) {
                return ResponseService.generateSuccessResponse("No courses found with the given criteria", new ArrayList<>(), HttpStatus.OK);
            }

            // Pagination logic
            int totalItems = courses.size();
            int totalPages = (int) Math.ceil((double) totalItems / limit);
            int fromIndex = offset * limit;
            int toIndex = Math.min(fromIndex + limit, totalItems);

            if (offset >= totalPages && offset != 0) {
                throw new IllegalArgumentException("No more courses available");
            }
            // Validate offset request
            if (fromIndex >= totalItems) {
                return ResponseService.generateErrorResponse("Page index out of range", HttpStatus.BAD_REQUEST);
            }

            List<Course> paginatedList = courses.subList(fromIndex, toIndex);

            // Construct paginated response
            Map<String, Object> response = new HashMap<>();
            response.put("courses", paginatedList);
            response.put("totalItems", totalItems);
            response.put("totalPages", totalPages);
            response.put("currentPage", offset);

            return ResponseService.generateSuccessResponse("Courses Retrieved Successfully", response, HttpStatus.OK);

        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            return ResponseService.generateErrorResponse(exception.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/delete/{courseIdString}")
    public ResponseEntity<?> deleteCourse(@PathVariable String courseIdString) {
        try {
            Long courseId = Long.parseLong(courseIdString);
            Course courseToDelete = courseService.getCourseById(courseId);
            if (courseToDelete == null) {
                return ResponseService.generateErrorResponse("Course with id " + courseId + " not found", HttpStatus.BAD_REQUEST);
            }
            Course deletedCourse = courseService.deleteCourseById(courseId);
            return ResponseService.generateSuccessResponse("Course is successfully deleted", deletedCourse, HttpStatus.OK);
        } catch (IllegalArgumentException illegalArgumentException) {
            exceptionHandlingService.handleException(illegalArgumentException);
            return ResponseService.generateErrorResponse(illegalArgumentException.getMessage(),HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            exceptionHandlingService.handleException(e);
            return ResponseService.generateErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
