package com.shodhAI.ShodhAI.Controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.shodhAI.ShodhAI.Dto.LeaderboardWrapper;
import com.shodhAI.ShodhAI.Dto.ScoreDto;
import com.shodhAI.ShodhAI.Dto.StudentDto;
import com.shodhAI.ShodhAI.Dto.StudentSemesterDto;
import com.shodhAI.ShodhAI.Dto.StudentWrapper;
import com.shodhAI.ShodhAI.Entity.AcademicDegree;
import com.shodhAI.ShodhAI.Entity.Student;
import com.shodhAI.ShodhAI.Entity.StudentAssignment;
import com.shodhAI.ShodhAI.Service.ExceptionHandlingService;
import com.shodhAI.ShodhAI.Service.ResponseService;
import com.shodhAI.ShodhAI.Service.S3StorageService;
import com.shodhAI.ShodhAI.Service.StudentService;
import jakarta.persistence.PersistenceException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/student", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
public class StudentController {

    @Autowired
    ExceptionHandlingService exceptionHandlingService;

    @Autowired
    StudentService studentService;

    @Autowired
    private Cloudinary cloudinary;

    @Autowired
    private S3StorageService s3StorageService;

    @PostMapping(value = "/add")
    public ResponseEntity<?> addStudent(HttpServletRequest request, @RequestBody StudentDto studentDto) {
        try {

            studentService.validateStudent(studentDto);
            Student student = studentService.saveStudent(studentDto, null, 'N');

            return ResponseService.generateSuccessResponse("Student Created Successfully", student, HttpStatus.OK);

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

    @PostMapping(value = "/upload-profile-picture/{studentIdString}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadProfilePicture(HttpServletRequest request, @PathVariable String studentIdString,
                                                  @RequestParam("profile_picture") MultipartFile profilePicture) {
        try {

            Long studentId = Long.parseLong(studentIdString);
            Student student = studentService.getStudentById(studentId);
            if (student == null) {
                return ResponseService.generateErrorResponse("Data not present in the DB", HttpStatus.OK);
            }

            // Upload profile picture to Cloudinary
            Map<String, Object> uploadResult = cloudinary.uploader().upload(profilePicture.getBytes(), ObjectUtils.emptyMap());

            // upload profile picture to S3
            File tempFile = File.createTempFile("upload", profilePicture.getOriginalFilename());
            profilePicture.transferTo(tempFile);

            String fileUrl = s3StorageService.uploadFile(tempFile, profilePicture.getOriginalFilename());

//            // Set the profile picture URL in the student DTO using Cloudinary.
//            String profilePictureUrl = uploadResult.get("url").toString();
//            student.setProfilePictureUrl(profilePictureUrl);

            // In case of aws s3
            student.setProfilePictureUrl(fileUrl);

            student = studentService.uploadProfilePicture(student);

            return ResponseService.generateSuccessResponse("Profile Picture Uploaded Successfully", student, HttpStatus.OK);

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
    public ResponseEntity<?> retrieveAllStudent(HttpServletRequest request,
                                                @RequestParam(defaultValue = "0") int offset,
                                                @RequestParam(defaultValue = "10") int limit) {
        try {

            if (offset < 0) {
                throw new IllegalArgumentException("Offset for pagination cannot be a negative number");
            }
            if (limit <= 0) {
                throw new IllegalArgumentException("Limit for pagination cannot be a negative number or 0");
            }

            List<Student> studentList = studentService.getAllStudent();
            if (studentList.isEmpty()) {
                return ResponseService.generateErrorResponse("Data not present in the DB", HttpStatus.OK);
            }

            List<StudentWrapper> studentWrapperList = new ArrayList<>();
            for (Student student : studentList) {
                StudentWrapper studentWrapper = new StudentWrapper();
                studentWrapper.wrapDetails(student);

                studentWrapperList.add(studentWrapper);
            }

            // Pagination logic
            int totalItems = studentWrapperList.size();
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

            List<StudentWrapper> paginatedList = studentWrapperList.subList(fromIndex, toIndex);

            // Construct paginated response
            Map<String, Object> response = new HashMap<>();
            response.put("student", paginatedList);
            response.put("totalItems", totalItems);
            response.put("totalPages", totalPages);
            response.put("currentPage", offset);

            return ResponseService.generateSuccessResponse("Student Data Retrieved Successfully", response, HttpStatus.OK);

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

    @CrossOrigin(origins = "*")
    @GetMapping("/get-student-by-id/{studentIdString}")
    public ResponseEntity<?> retrieveStudentById(HttpServletRequest request, @PathVariable String studentIdString) {
        try {

            Long studentId = Long.parseLong(studentIdString);
            Student student = studentService.getStudentById(studentId);

            if (student == null) {
                return ResponseService.generateErrorResponse("Data not present in the DB", HttpStatus.OK);
            }

            StudentSemesterDto studentSemesterDto = new StudentSemesterDto();
            List<ScoreDto> semesterScoreDto = new ArrayList<>();

            ScoreDto accuracyScoreDto = new ScoreDto();
            accuracyScoreDto.wrapDetails(student.getAccuracy());

            ScoreDto criticalThinkingScoreDto = new ScoreDto();
            criticalThinkingScoreDto.wrapDetails(student.getCriticalThinking());

            ScoreDto timeSpentScoreDto = new ScoreDto();
            timeSpentScoreDto.wrapDetails(student.getTimeSpent());

            ScoreDto overallScoreDto = new ScoreDto();
            overallScoreDto.wrapDetails(student);

            semesterScoreDto.add(overallScoreDto);
            semesterScoreDto.add(accuracyScoreDto);
            semesterScoreDto.add(criticalThinkingScoreDto);
            semesterScoreDto.add(timeSpentScoreDto);

            studentSemesterDto.wrapDetails(semesterScoreDto);

            // change the profile picture url with pre-signed one
            student.setProfilePictureUrl(s3StorageService.getPresignedUrl(student.getProfilePictureUrl()).toString());

            StudentWrapper studentWrapper = new StudentWrapper();
            studentWrapper.wrapDetails(student);

            return ResponseService.generateSuccessResponse("Student Retrieved Successfully", studentWrapper, HttpStatus.OK);

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

    //    @CrossOrigin(origins = "*")
    @GetMapping("/get-leaderboard")
    public ResponseEntity<?> retrieveStudentLeaderboard(HttpServletRequest request) {
        try {

            List<Student> studentList = studentService.getStudentLeaderboard();
            if (studentList.isEmpty()) {
                return ResponseService.generateErrorResponse("Data not present in the DB", HttpStatus.OK);
            }

            List<LeaderboardWrapper> leaderboardWrapperList = new ArrayList<>();
            for (Student student : studentList) {
                LeaderboardWrapper leaderboardWrapper = new LeaderboardWrapper();
                leaderboardWrapper.wrapDetails(student);

                leaderboardWrapperList.add(leaderboardWrapper);
            }
            return ResponseService.generateSuccessResponse("Student Data Retrieved Successfully", leaderboardWrapperList, HttpStatus.OK);

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

    @PatchMapping("/update/{studentIdString}")
    public ResponseEntity<?> updateStudent( @PathVariable String studentIdString,@RequestBody StudentDto studentDto) {
        try {
            Long studentId = Long.parseLong(studentIdString);
            Student student = studentService.updateStudent(studentId,studentDto);

            StudentWrapper studentWrapper = new StudentWrapper();
            studentWrapper.wrapDetails(student);

            return ResponseService.generateSuccessResponse("Student updated Successfully", studentWrapper, HttpStatus.OK);
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

    @PostMapping("/submit-assignment/{assignmentId}/{studentId}")
    public ResponseEntity<?> submitAssignment(
            @PathVariable Long assignmentId,
            @PathVariable Long studentId,
            @RequestParam(required = false) MultipartFile file,
            @RequestParam(required = false) String submissionText) {

        try {
            StudentAssignment studentAssignment=studentService.submitAssignment(assignmentId, studentId, submissionText);
            return ResponseService.generateSuccessResponse(
                    "Assignment submitted successfully", studentAssignment, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            exceptionHandlingService.handleException(e);
            return ResponseService.generateErrorResponse("Invalid request: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return ResponseService.generateErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
