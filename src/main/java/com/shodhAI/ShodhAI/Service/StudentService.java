package com.shodhAI.ShodhAI.Service;

import com.shodhAI.ShodhAI.Component.Constant;
import com.shodhAI.ShodhAI.Dto.AcademicDegreeDto;
import com.shodhAI.ShodhAI.Dto.AccuracyDto;
import com.shodhAI.ShodhAI.Dto.CourseDto;
import com.shodhAI.ShodhAI.Dto.CriticalThinkingDto;
import com.shodhAI.ShodhAI.Dto.FacultyDto;
import com.shodhAI.ShodhAI.Dto.MemoryDto;
import com.shodhAI.ShodhAI.Dto.StudentDto;
import com.shodhAI.ShodhAI.Dto.TimeSpentDto;
import com.shodhAI.ShodhAI.Dto.UnderstandingDto;
import com.shodhAI.ShodhAI.Entity.AcademicDegree;
import com.shodhAI.ShodhAI.Entity.Accuracy;
import com.shodhAI.ShodhAI.Entity.Assignment;
import com.shodhAI.ShodhAI.Entity.Course;
import com.shodhAI.ShodhAI.Entity.CriticalThinking;
import com.shodhAI.ShodhAI.Entity.Faculty;
import com.shodhAI.ShodhAI.Entity.Gender;
import com.shodhAI.ShodhAI.Entity.Memory;
import com.shodhAI.ShodhAI.Entity.Role;
import com.shodhAI.ShodhAI.Entity.Student;
import com.shodhAI.ShodhAI.Entity.StudentAssignment;
import com.shodhAI.ShodhAI.Entity.TimeSpent;
import com.shodhAI.ShodhAI.Entity.Understanding;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.util.Date;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class StudentService {

    @Autowired
    EntityManager entityManager;

    @Autowired
    GenderService genderService;

    @Autowired
    RoleService roleService;

    @Autowired
    AcademicDegreeService academicDegreeService;

    @Autowired
    ExceptionHandlingService exceptionHandlingService;

    @Autowired
    AccuracyService accuracyService;

    @Autowired
    CriticalThinkingService criticalThinkingService;

    @Autowired
    TimeSpentService timeSpentService;

    @Autowired
    UnderstandingService understandingService;

    @Autowired
    MemoryService memoryService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    public void validateStudent(StudentDto studentDto) throws Exception {
        try {
            if (studentDto.getFirstName() == null || studentDto.getFirstName().isEmpty()) {
                throw new IllegalArgumentException("Student name cannot be null or empty");
            }
            studentDto.setFirstName(studentDto.getFirstName().trim());

            if (studentDto.getLastName() != null) {
                if (studentDto.getLastName().isEmpty() || studentDto.getLastName().trim().isEmpty()) {
                    throw new IllegalArgumentException("Last name cannot be empty");
                }
                studentDto.setLastName(studentDto.getLastName().trim());
            }

            if (studentDto.getCountryCode() != null) {
                if (studentDto.getCountryCode().isEmpty() || studentDto.getCountryCode().trim().isEmpty()) {
                    throw new IllegalArgumentException("Country code cannot be empty");
                }
                studentDto.setCountryCode(studentDto.getCountryCode().trim());
            }

            if (studentDto.getMobileNumber() == null || studentDto.getMobileNumber().trim().isEmpty()) {
                throw new IllegalArgumentException("Student Mobile Number cannot be null or empty");
            }
            studentDto.setMobileNumber(studentDto.getMobileNumber().trim());

            if (studentDto.getUserName() == null || studentDto.getUserName().trim().isEmpty()) {
                throw new IllegalArgumentException("User name cannot be null or empty");
            }
            studentDto.setUserName(studentDto.getUserName().trim());

            if (studentDto.getPassword() == null) {
                throw new IllegalArgumentException("Password cannot be null");
            }
            String hashedPassword = passwordEncoder.encode(studentDto.getPassword());
            studentDto.setPassword(hashedPassword);
            studentDto.setUserName(studentDto.getUserName().trim());

            if (studentDto.getGenderId() == null || studentDto.getGenderId() <= 0) {
                throw new IllegalArgumentException(("Gender Id cannot be null or <= 0"));
            }
            if (studentDto.getAcademicDegreeId() == null || studentDto.getAcademicDegreeId() <= 0) {
                throw new IllegalArgumentException(("Academic Degree Id cannot be null or <= 0"));
            }

        } catch (IllegalArgumentException illegalArgumentException) {
            exceptionHandlingService.handleException(illegalArgumentException);
            throw new IllegalArgumentException(illegalArgumentException.getMessage());
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            throw new Exception(exception.getMessage());
        }
    }

    @Transactional
    public Student saveStudent(StudentDto studentDto, String otp, Character archived) throws Exception {
        try {

            Gender gender = genderService.getGenderById(studentDto.getGenderId());
            Role role = roleService.getRoleById(4L);
            AcademicDegree academicDegree = null;
            if (studentDto.getAcademicDegreeId() != null) {
                academicDegree = academicDegreeService.getAcademicDegreeById(studentDto.getAcademicDegreeId());
            }

            Date currentDate = new Date();

            Student student = new Student();
            student.setFirstName(studentDto.getFirstName());
            student.setLastName(studentDto.getLastName());
            student.setUserName(studentDto.getUserName());
            student.setCountryCode(studentDto.getCountryCode());
            student.setCreatedDate(currentDate);
            student.setUpdatedDate(currentDate);
            student.setMobileNumber(studentDto.getMobileNumber());
            student.setCollegeEmail(studentDto.getCollegeEmail());
            student.setPersonalEmail(studentDto.getPersonalEmail());
            student.setDateOfBirth(studentDto.getDateOfBirth());
            student.setOtp(otp);
            student.setArchived(archived);

            student.setUserName(studentDto.getUserName());
            student.setPassword(studentDto.getPassword());
            student.setGender(gender);
            student.setRole(role);
            student.setAcademicDegree(academicDegree);
            student.setProfilePictureUrl(studentDto.getProfilePictureUrl());

            CriticalThinking criticalThinking = criticalThinkingService.saveCriticalThinking(new CriticalThinkingDto());
            Accuracy accuracy = accuracyService.saveAccuracy(new AccuracyDto());
            TimeSpent timeSpent = timeSpentService.saveTimeSpent(new TimeSpentDto());
            Understanding understanding = understandingService.saveUnderstanding(new UnderstandingDto());
            Memory memory = memoryService.saveMemory(new MemoryDto());

            student.setCriticalThinking(criticalThinking);
            student.setAccuracy(accuracy);
            student.setTimeSpent(timeSpent);
            student.setMemory(memory);
            student.setUnderstanding(understanding);

            return entityManager.merge(student);

        } catch (DataIntegrityViolationException dataIntegrityViolationException) {
            exceptionHandlingService.handleException(dataIntegrityViolationException);
            throw new IndexOutOfBoundsException(dataIntegrityViolationException.getMessage());
        } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
            exceptionHandlingService.handleException(indexOutOfBoundsException);
            throw new IndexOutOfBoundsException(indexOutOfBoundsException.getMessage());
        } catch (PersistenceException persistenceException) {
            exceptionHandlingService.handleException(persistenceException);
            throw new PersistenceException(persistenceException.getMessage());
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            throw new Exception(exception.getMessage());
        }
    }

    @Transactional
    public Student uploadProfilePicture(Student student) throws Exception {
        try {

            return entityManager.merge(student);

        } catch (DataIntegrityViolationException dataIntegrityViolationException) {
            exceptionHandlingService.handleException(dataIntegrityViolationException);
            throw new IndexOutOfBoundsException(dataIntegrityViolationException.getMessage());
        } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
            exceptionHandlingService.handleException(indexOutOfBoundsException);
            throw new IndexOutOfBoundsException(indexOutOfBoundsException.getMessage());
        } catch (PersistenceException persistenceException) {
            exceptionHandlingService.handleException(persistenceException);
            throw new PersistenceException(persistenceException.getMessage());
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            throw new Exception(exception.getMessage());
        }
    }

    public List<Student> getAllStudent() throws Exception {
        try {

            TypedQuery<Student> query = entityManager.createQuery(Constant.GET_ALL_STUDENT, Student.class);
            return query.getResultList();

        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            throw new Exception(exception.getMessage());
        }
    }

    public Student getStudentById(Long studentId) throws Exception {
        try {

            TypedQuery<Student> query = entityManager.createQuery(Constant.GET_STUDENT_BY_ID, Student.class);
            query.setParameter("studentId", studentId);
            return query.getResultList().get(0);

        } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
            exceptionHandlingService.handleException(indexOutOfBoundsException);
            throw new IndexOutOfBoundsException("Student not found with given Id");
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            throw new Exception(exception);
        }
    }

    public List<Student> getStudentLeaderboard() throws Exception {
        try {

            TypedQuery<Student> query = entityManager.createQuery(Constant.GET_STUDENT_LEADERBOARD, Student.class);
            return query.getResultList();

        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            throw new Exception(exception.getMessage());
        }
    }

    public Student retrieveStudentByUsername(String username) {

        // Execute the query using JdbcTemplate
        TypedQuery<Student> query = entityManager.createQuery("SELECT s FROM Student s WHERE s.collegeEmail = : username", Student.class);
        query.setParameter("username", username);
        List<Student> students = query.getResultList();

        // Check if the user exists
        if (students.isEmpty()) {
            throw new UsernameNotFoundException("Student not found with username: " + username);
        }

        Student student = students.get(0);  // Assuming only one user with this username
        return student;
    }

    public void validateAndSaveStudentForUpdate(StudentDto studentDto, Student studentToUpdate) throws Exception {
        try {
            if (Objects.nonNull(studentDto.getFirstName())) {
                if(studentDto.getFirstName().isEmpty()) {
                    throw new IllegalArgumentException("Student name cannot be empty");
                }
                studentDto.setFirstName(studentDto.getFirstName().trim());
                studentToUpdate.setFirstName(studentDto.getFirstName());
            }
            if(studentDto.getLastName() != null) {
                if(studentDto.getLastName().isEmpty() || studentDto.getLastName().trim().isEmpty()) {
                    throw new IllegalArgumentException("Last name cannot be empty");
                }
                studentDto.setLastName(studentDto.getLastName().trim());
                studentToUpdate.setFirstName(studentDto.getLastName());
            }

            if(studentDto.getCountryCode() != null) {
                if(studentDto.getCountryCode().isEmpty() || studentDto.getCountryCode().trim().isEmpty()) {
                    throw new IllegalArgumentException("Country code cannot be empty");
                }
                studentDto.setCountryCode(studentDto.getCountryCode().trim());
                studentToUpdate.setCountryCode(studentDto.getCountryCode());
            }

            if(studentDto.getMobileNumber()!=null)
            {
                if(studentDto.getMobileNumber().trim().isEmpty()) {
                    throw new IllegalArgumentException("Faculty Mobile Number cannot be empty");
                }
                studentDto.setMobileNumber(studentDto.getMobileNumber().trim());
                studentToUpdate.setMobileNumber(studentDto.getMobileNumber());
            }

            if(studentDto.getUserName()!=null)
            {
                if(studentDto.getUserName().trim().isEmpty()) {
                    throw new IllegalArgumentException("User name cannot be empty");
                }
                studentDto.setUserName(studentDto.getUserName().trim());
                studentToUpdate.setUserName(studentDto.getUserName());

            }
            if(studentDto.getPassword()!=null)
            {
                String hashedPassword = passwordEncoder.encode(studentDto.getPassword());
                studentDto.setPassword(hashedPassword);
                studentToUpdate.setPassword(studentDto.getPassword());
            }
            if(studentDto.getAcademicDegreeId()!=null)
            {
                if(studentDto.getAcademicDegreeId() <= 0) {
                    throw new IllegalArgumentException(("Academic Degree Id cannot be <= 0"));
                }
            }
            if(studentDto.getGenderId()!=null)
            {
                if(studentDto.getGenderId() <= 0) {
                    throw new IllegalArgumentException(("Gender Id cannot be <= 0"));
                }
                Gender gender = genderService.getGenderById(studentDto.getGenderId());
                studentToUpdate.setGender(gender);
            }
            if (studentDto.getCourseIds() != null) {
                if (!studentDto.getCourseIds().isEmpty()) {
                    // Fetch all valid courses
                    List<Course> coursesToAdd = entityManager.createQuery(
                                    "SELECT c FROM Course c WHERE c.courseId IN :courseIds", Course.class)
                            .setParameter("courseIds", studentDto.getCourseIds())
                            .getResultList();

                    if (coursesToAdd.size() != studentDto.getCourseIds().size()) {
                        throw new IllegalArgumentException("One or more Course IDs are invalid.");
                    }
                    studentToUpdate.getCourses().forEach(course -> course.getStudents().remove(studentToUpdate));
                    studentToUpdate.getCourses().clear();
                    studentToUpdate.getFacultyMembers().forEach(faculty -> faculty.getStudents().remove(studentToUpdate));
                    studentToUpdate.getFacultyMembers().clear();
                    entityManager.merge(studentToUpdate);
                    studentToUpdate.setCourses(coursesToAdd);
                    List<Faculty> facultyToAdd = new ArrayList<>();
                    for (Course course : coursesToAdd) {
                        for (Faculty faculty : course.getFacultyMembers()) {
                            if (!facultyToAdd.contains(faculty)) {
                                facultyToAdd.add(faculty);
                            }
                        }
                    }
                    studentToUpdate.setFacultyMembers(facultyToAdd);
                    for (Faculty faculty : facultyToAdd) {
                        if (!faculty.getStudents().contains(studentToUpdate)) {
                            faculty.getStudents().add(studentToUpdate);
                        }
                    }
                    for (Course course : coursesToAdd) {
                        if (!course.getStudents().contains(studentToUpdate)) {
                            course.getStudents().add(studentToUpdate);
                        }
                    }
                    entityManager.merge(studentToUpdate);
                }
                else {
                    studentToUpdate.getCourses().forEach(course -> course.getStudents().remove(studentToUpdate));
                    studentToUpdate.getCourses().clear();
                    studentToUpdate.getFacultyMembers().forEach(faculty -> faculty.getStudents().remove(studentToUpdate));
                    studentToUpdate.getFacultyMembers().clear();
                    entityManager.merge(studentToUpdate);
                }

            }

        } catch (IllegalArgumentException illegalArgumentException) {
            exceptionHandlingService.handleException(illegalArgumentException);
            throw new IllegalArgumentException(illegalArgumentException.getMessage());
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            throw new Exception(exception.getMessage());
        }
    }

    @Transactional
    public Student updateStudent(Long studentId, StudentDto studentDto) throws Exception {
        Student studentToUpdate= entityManager.find(Student.class,studentId);
        if(studentToUpdate==null)
        {
            throw new IllegalArgumentException("Student with id "+ studentId+" not found");
        }
        validateAndSaveStudentForUpdate(studentDto,studentToUpdate);
        return entityManager.merge(studentToUpdate);
    }

    public List<Student> filterStudents(String username, Long studentId, String personalEmail) {
        StringBuilder queryString = new StringBuilder("SELECT s FROM Student s WHERE 1 = 1");

        if (username != null && !username.isEmpty()) {
            queryString.append(" AND s.userName = :username");
        }
        if (studentId != null) {
            queryString.append(" AND s.id = :studentId");
        }
        if (personalEmail != null && !personalEmail.isEmpty()) {
            queryString.append(" AND s.personalEmail = :personalEmail");
        }

        TypedQuery<Student> query = entityManager.createQuery(queryString.toString(), Student.class);

        if (username != null && !username.isEmpty()) {
            query.setParameter("username", username);
        }
        if (studentId != null) {
            query.setParameter("studentId", studentId);
        }
        if (personalEmail != null && !personalEmail.isEmpty()) {
            query.setParameter("personalEmail", personalEmail);
        }

        List<Student> students = query.getResultList();
        /*if (students.isEmpty()) {
            throw new UsernameNotFoundException("No students found matching the criteria");
        }*/

        return students;
    }

    @Transactional
    public StudentAssignment submitAssignment(Long assignmentId, Long studentId, MultipartFile file, String submissionText) {
        // Retrieve the student assignment record
        entityManager.clear();
        Assignment assignment = entityManager.find(Assignment.class, assignmentId);
        if (assignment == null) {
            throw new IllegalArgumentException("Assignment with id " + studentId + " not found");
        }
        Student student = entityManager.find(Student.class, studentId);
        if (student == null) {
            throw new IllegalArgumentException("Student with id " + studentId + " not found");
        }
        List<StudentAssignment> results = entityManager.createQuery(
                        "SELECT sa FROM StudentAssignment sa " +
                                "WHERE sa.assignment.assignmentId = :assignmentId " +
                                "AND sa.student.id = :studentId", StudentAssignment.class)
                .setParameter("assignmentId", assignmentId)
                .setParameter("studentId", studentId)
                .getResultList();

        StudentAssignment studentAssignment = results.isEmpty() ? null : results.get(0);

        if (studentAssignment == null) {
            throw new IllegalArgumentException("No assignment found for the given student.");
        }

        // Check if the assignment is already submitted
        if (Boolean.TRUE.equals(studentAssignment.getCompletionStatus())) {
            throw new IllegalArgumentException("Assignment has already been submitted.");
        }

        // Handle file submission if provided
        if (file != null && !file.isEmpty()) {
            try {
                studentAssignment.setSubmittedFile(file.getBytes()); // Assuming you have this field
                studentAssignment.setSubmittedFileName(file.getOriginalFilename()); // Store file name
            } catch (Exception e) {
                throw new RuntimeException("Error processing file: " + e.getMessage());
            }
        }

        // Handle text submission if provided
        if (submissionText != null && !submissionText.isEmpty()) {
            studentAssignment.setSubmittedText(submissionText);
        }

        // Update submission details
        studentAssignment.setCompletionStatus(true);
        studentAssignment.setSubmissionDate(new Date());
        studentAssignment.setUpdatedDate(new Date());

        entityManager.merge(studentAssignment);
        return studentAssignment;
    }
}
