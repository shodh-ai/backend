package com.shodhAI.ShodhAI.Service;

import com.shodhAI.ShodhAI.Component.Constant;
import com.shodhAI.ShodhAI.Dto.AccuracyDto;
import com.shodhAI.ShodhAI.Dto.CriticalThinkingDto;
import com.shodhAI.ShodhAI.Dto.FacultyDto;
import com.shodhAI.ShodhAI.Dto.StudentDto;
import com.shodhAI.ShodhAI.Dto.TimeSpentDto;
import com.shodhAI.ShodhAI.Entity.AcademicDegree;
import com.shodhAI.ShodhAI.Entity.Accuracy;
import com.shodhAI.ShodhAI.Entity.Course;
import com.shodhAI.ShodhAI.Entity.CriticalThinking;
import com.shodhAI.ShodhAI.Entity.Faculty;
import com.shodhAI.ShodhAI.Entity.Gender;
import com.shodhAI.ShodhAI.Entity.Role;
import com.shodhAI.ShodhAI.Entity.Student;
import com.shodhAI.ShodhAI.Entity.TimeSpent;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Time;
import java.time.Instant;
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

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    public void validateStudent(StudentDto studentDto) throws Exception {
        try {
            if(studentDto.getFirstName() == null || studentDto.getFirstName().isEmpty()) {
                throw new IllegalArgumentException("Student name cannot be null or empty");
            }
            studentDto.setFirstName(studentDto.getFirstName().trim());

            if(studentDto.getLastName() != null) {
                if(studentDto.getLastName().isEmpty() || studentDto.getLastName().trim().isEmpty()) {
                    throw new IllegalArgumentException("Last name cannot be empty");
                }
                studentDto.setLastName(studentDto.getLastName().trim());
            }

            if(studentDto.getCountryCode() != null) {
                if(studentDto.getCountryCode().isEmpty() || studentDto.getCountryCode().trim().isEmpty()) {
                    throw new IllegalArgumentException("Country code cannot be empty");
                }
                studentDto.setCountryCode(studentDto.getCountryCode().trim());
            }

            if(studentDto.getMobileNumber() == null || studentDto.getMobileNumber().trim().isEmpty()) {
                throw new IllegalArgumentException("Student Mobile Number cannot be null or empty");
            }
            studentDto.setMobileNumber(studentDto.getMobileNumber().trim());

            if(studentDto.getUserName() == null || studentDto.getUserName().trim().isEmpty()) {
                throw new IllegalArgumentException("User name cannot be null or empty");
            }
            studentDto.setUserName(studentDto.getUserName().trim());

            if(studentDto.getPassword() == null) {
                throw new IllegalArgumentException("Password cannot be null");
            }
            String hashedPassword = passwordEncoder.encode(studentDto.getPassword());
            studentDto.setPassword(hashedPassword);
            studentDto.setUserName(studentDto.getUserName().trim());

            if(studentDto.getGenderId() == null || studentDto.getGenderId() <= 0) {
                throw new IllegalArgumentException(("Gender Id cannot be null or <= 0"));
            }
            if(studentDto.getAcademicDegreeId() == null || studentDto.getAcademicDegreeId() <= 0) {
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
    public Student saveStudent(StudentDto studentDto) throws Exception {
        try {

            Gender gender = genderService.getGenderById(studentDto.getGenderId());
            Role role = roleService.getRoleById(4L);
            AcademicDegree academicDegree = academicDegreeService.getAcademicDegreeById(studentDto.getAcademicDegreeId());

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

            student.setUserName(studentDto.getUserName());
            student.setPassword(studentDto.getPassword());
            student.setGender(gender);
            student.setRole(role);
            student.setAcademicDegree(academicDegree);
            student.setProfilePictureUrl(studentDto.getProfilePictureUrl());

            CriticalThinking criticalThinking = criticalThinkingService.saveCriticalThinking(new CriticalThinkingDto());
            Accuracy accuracy = accuracyService.saveAccuracy(new AccuracyDto());
            TimeSpent timeSpent = timeSpentService.saveTimeSpent(new TimeSpentDto());

            student.setCriticalThinking(criticalThinking);
            student.setAccuracy(accuracy);
            student.setTimeSpent(timeSpent);

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
                    List<Course> coursesToAdd = entityManager.createQuery(
                                    "SELECT c FROM Course c WHERE c.courseId IN :courseIds", Course.class)
                            .setParameter("courseIds", studentDto.getCourseIds())
                            .getResultList();

                    if (coursesToAdd.size() != studentDto.getCourseIds().size()) {
                        throw new IllegalArgumentException("One or more Course IDs are invalid.");
                    }

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
                }
                else {
                    studentToUpdate.setCourses(new ArrayList<>());
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

}
