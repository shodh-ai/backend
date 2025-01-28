package com.shodhAI.ShodhAI.Controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.shodhAI.ShodhAI.Dto.LeaderboardWrapper;
import com.shodhAI.ShodhAI.Dto.ScoreDto;
import com.shodhAI.ShodhAI.Dto.StudentDto;
import com.shodhAI.ShodhAI.Dto.StudentSemesterDto;
import com.shodhAI.ShodhAI.Dto.StudentWrapper;
import com.shodhAI.ShodhAI.Entity.Student;
import com.shodhAI.ShodhAI.Service.ExceptionHandlingService;
import com.shodhAI.ShodhAI.Service.ResponseService;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
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

    @PostMapping(value = "/add")
    public ResponseEntity<?> addStudent(HttpServletRequest request, @RequestBody StudentDto studentDto) {
        try {

            studentService.validateStudent(studentDto);
            Student student = studentService.saveStudent(studentDto);

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

            // Set the profile picture URL in the student DTO
            String profilePictureUrl = uploadResult.get("url").toString();
            student.setProfilePictureUrl(profilePictureUrl);

            student = studentService.uploadProfilePicture(student);

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

    @GetMapping("/get-all")
    public ResponseEntity<?> retrieveAllStudent(HttpServletRequest request) {
        try {

            List<Student> studentList = studentService.getAllStudent();
            if (studentList.isEmpty()) {
                return ResponseService.generateErrorResponse("Data not present in the DB", HttpStatus.OK);
            }

            List<StudentWrapper> studentWrapperList = new ArrayList<>();
            for(Student student: studentList) {
                StudentWrapper studentWrapper = new StudentWrapper();
                studentWrapper.wrapDetails(student);

                studentWrapperList.add(studentWrapper);
            }
            return ResponseService.generateSuccessResponse("Student Data Retrieved Successfully", studentWrapperList, HttpStatus.OK);

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

        /*StudentWrapper studentWrapper = new StudentWrapper();
            studentWrapper.wrapDetails(student);*/

            return ResponseService.generateSuccessResponse("Student Retrieved Successfully", studentSemesterDto, HttpStatus.OK);

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
    @GetMapping("/get-leaderboard")
    public ResponseEntity<?> retrieveStudentLeaderboard(HttpServletRequest request) {
        try {

            List<Student> studentList = studentService.getStudentLeaderboard();
            if (studentList.isEmpty()) {
                return ResponseService.generateErrorResponse("Data not present in the DB", HttpStatus.OK);
            }

            List<LeaderboardWrapper> leaderboardWrapperList = new ArrayList<>();
            for(Student student: studentList) {
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

}
