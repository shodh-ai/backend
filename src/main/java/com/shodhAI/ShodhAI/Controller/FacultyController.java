package com.shodhAI.ShodhAI.Controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.shodhAI.ShodhAI.Dto.FacultyDto;
import com.shodhAI.ShodhAI.Dto.FacultyWrapper;
import com.shodhAI.ShodhAI.Entity.Faculty;
import com.shodhAI.ShodhAI.Service.ExceptionHandlingService;
import com.shodhAI.ShodhAI.Service.FacultyService;
import com.shodhAI.ShodhAI.Service.ResponseService;
import jakarta.persistence.PersistenceException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/faculty", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
public class FacultyController {

    @Autowired
    ExceptionHandlingService exceptionHandlingService;

    @Autowired
    FacultyService facultyService;

    @Autowired
    private Cloudinary cloudinary;

    @PostMapping(value = "/add")
    public ResponseEntity<?> addFaculty(HttpServletRequest request, @RequestBody FacultyDto facultyDto) {
        try {

            facultyService.validateFaculty(facultyDto);
            Faculty faculty = facultyService.saveFaculty(facultyDto, null, 'N');

            return ResponseService.generateSuccessResponse("Faculty Created Successfully", faculty, HttpStatus.OK);

        } catch (DataIntegrityViolationException dataIntegrityViolationException) {
            exceptionHandlingService.handleException(dataIntegrityViolationException);
            throw new IndexOutOfBoundsException("Data Integrity Exception caught [college_email, username, mobileNumber must be unique]: " + dataIntegrityViolationException.getMessage());
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

    @PostMapping(value = "/upload-profile-picture/{facultyIdString}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadProfilePicture(HttpServletRequest request, @PathVariable String facultyIdString,
                                                  @RequestParam("profile_picture") MultipartFile profilePicture) {
        try {

            Long facultyId = Long.parseLong(facultyIdString);
            Faculty faculty = facultyService.getFacultyById(facultyId);
            if (faculty == null) {
                return ResponseService.generateErrorResponse("Data not present in the DB", HttpStatus.OK);
            }

            // Upload profile picture to Cloudinary
            Map<String, Object> uploadResult = cloudinary.uploader().upload(profilePicture.getBytes(), ObjectUtils.emptyMap());

            // Set the profile picture URL in the student DTO
            String profilePictureUrl = uploadResult.get("url").toString();
            faculty.setProfilePictureUrl(profilePictureUrl);

            faculty = facultyService.uploadProfilePicture(faculty);

            return ResponseService.generateSuccessResponse("Profile Picture Uploaded Successfully", faculty, HttpStatus.OK);

        } catch (DataIntegrityViolationException dataIntegrityViolationException) {
            exceptionHandlingService.handleException(dataIntegrityViolationException);
            return ResponseService.generateErrorResponse("Data Integrity Exception caught: " + dataIntegrityViolationException.getMessage(), HttpStatus.BAD_REQUEST);
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
    public ResponseEntity<?> retrieveAllFaculty(@RequestParam(defaultValue = "0") int offset,
                                                @RequestParam(defaultValue = "10") int limit,
                                                HttpServletRequest request) {
        try {
            if (offset < 0) {
                throw new IllegalArgumentException("Offset for pagination cannot be a negative number");
            }
            if (limit <= 0) {
                throw new IllegalArgumentException("Limit for pagination cannot be zero or negative");
            }

            List<Faculty> facultyList = facultyService.getAllFaculty();
            if (facultyList.isEmpty()) {
                return ResponseService.generateErrorResponse("Data not present in the DB", HttpStatus.OK);
            }

            List<FacultyWrapper> facultyWrapperList = new ArrayList<>();
            for (Faculty faculty : facultyList) {
                FacultyWrapper facultyWrapper = new FacultyWrapper();
                facultyWrapper.wrapDetails(faculty);
                facultyWrapperList.add(facultyWrapper);
            }

            int totalItems = facultyWrapperList.size();
            int totalPages = (int) Math.ceil((double) totalItems / limit);
            int fromIndex = offset * limit;
            int toIndex = Math.min(fromIndex + limit, totalItems);

            if (offset >= totalPages && offset != 0) {
                throw new IllegalArgumentException("No more faculties available");
            }

            if (fromIndex >= totalItems) {
                return ResponseService.generateErrorResponse("Page index out of range", HttpStatus.BAD_REQUEST);
            }

            List<FacultyWrapper> paginatedFacultyList = facultyWrapperList.subList(fromIndex, toIndex);

            Map<String, Object> response = new HashMap<>();
            response.put("facultyList", paginatedFacultyList);
            response.put("totalItems", totalItems);
            response.put("totalPages", totalPages);
            response.put("currentPage", offset);

            return ResponseService.generateSuccessResponse("Faculty Data Retrieved Successfully", response, HttpStatus.OK);

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


    @GetMapping("/get-faculty-by-id/{facultyIdString}")
    public ResponseEntity<?> retrieveFacultyById(HttpServletRequest request, @PathVariable String facultyIdString) {
        try {

            Long facultyId = Long.parseLong(facultyIdString);
            Faculty faculty = facultyService.getFacultyById(facultyId);

            if (faculty == null) {
                return ResponseService.generateErrorResponse("Data not present in the DB", HttpStatus.OK);
            }

            FacultyWrapper facultyWrapper = new FacultyWrapper();
            facultyWrapper.wrapDetails(faculty);

            return ResponseService.generateSuccessResponse("Faculty Retrieved Successfully", facultyWrapper, HttpStatus.OK);

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

    @PatchMapping("/update/{facultyIdString}")
    public ResponseEntity<?> updateFaculty( @PathVariable String facultyIdString,@RequestBody FacultyDto facultyDto) {
        try {
            Long facultyId = Long.parseLong(facultyIdString);
            Faculty faculty = facultyService.updateFaculty(facultyId,facultyDto);

            FacultyWrapper facultyWrapper = new FacultyWrapper();
            facultyWrapper.wrapDetails(faculty);

            return ResponseService.generateSuccessResponse("Faculty updated Successfully", facultyWrapper, HttpStatus.OK);
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
