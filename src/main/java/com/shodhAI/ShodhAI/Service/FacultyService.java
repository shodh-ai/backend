package com.shodhAI.ShodhAI.Service;

import com.shodhAI.ShodhAI.Component.Constant;
import com.shodhAI.ShodhAI.Dto.FacultyDto;
import com.shodhAI.ShodhAI.Entity.Course;
import com.shodhAI.ShodhAI.Entity.Faculty;
import com.shodhAI.ShodhAI.Entity.Gender;
import com.shodhAI.ShodhAI.Entity.Role;
import com.shodhAI.ShodhAI.Entity.Student;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class FacultyService {

    @Autowired
    EntityManager entityManager;

    @Autowired
    GenderService genderService;

    @Autowired
    RoleService roleService;

    @Autowired
    CourseService courseService;

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


    public void validateFaculty(FacultyDto facultyDto) throws Exception {
        try {
            if(facultyDto.getFirstName() == null || facultyDto.getFirstName().isEmpty()) {
                throw new IllegalArgumentException("Faculty name cannot be null or empty");
            }
            facultyDto.setFirstName(facultyDto.getFirstName().trim());

            if(facultyDto.getLastName() != null) {
                if(facultyDto.getLastName().isEmpty() || facultyDto.getLastName().trim().isEmpty()) {
                    throw new IllegalArgumentException("Last name cannot be empty");
                }
                facultyDto.setLastName(facultyDto.getLastName().trim());
            }

            if(facultyDto.getCountryCode() != null) {
                if(facultyDto.getCountryCode().isEmpty() || facultyDto.getCountryCode().trim().isEmpty()) {
                    throw new IllegalArgumentException("Country code cannot be empty");
                }
                facultyDto.setCountryCode(facultyDto.getCountryCode().trim());
            }

            if(facultyDto.getMobileNumber() == null || facultyDto.getMobileNumber().trim().isEmpty()) {
                throw new IllegalArgumentException("Faculty Mobile Number cannot be null or empty");
            }
            facultyDto.setMobileNumber(facultyDto.getMobileNumber().trim());

            if(facultyDto.getUserName() == null || facultyDto.getUserName().trim().isEmpty()) {
                throw new IllegalArgumentException("User name cannot be null or empty");
            }
            facultyDto.setUserName(facultyDto.getUserName().trim());

            if(facultyDto.getPassword() == null) {
                throw new IllegalArgumentException("Password cannot be null");
            }
            String hashedPassword = passwordEncoder.encode(facultyDto.getPassword());
            facultyDto.setPassword(hashedPassword);
            facultyDto.setUserName(facultyDto.getUserName().trim());

            if(facultyDto.getGenderId() == null || facultyDto.getGenderId() <= 0) {
                throw new IllegalArgumentException(("Gender Id cannot be null or <= 0"));
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
    public Faculty saveFaculty(FacultyDto facultyDto) throws Exception {
        try {

            Gender gender = genderService.getGenderById(facultyDto.getGenderId());
            Role role = roleService.getRoleById(3L);

            Date currentDate = new Date();

            Faculty faculty = new Faculty();
            faculty.setFirstName(facultyDto.getFirstName());
            faculty.setLastName(facultyDto.getLastName());
            faculty.setUserName(facultyDto.getUserName());
            faculty.setCountryCode(facultyDto.getCountryCode());
            faculty.setCreatedDate(currentDate);
            faculty.setUpdatedDate(currentDate);
            faculty.setMobileNumber(facultyDto.getMobileNumber());
            faculty.setCollegeEmail(facultyDto.getCollegeEmail());
            faculty.setPersonalEmail(facultyDto.getPersonalEmail());
            faculty.setDateOfBirth(facultyDto.getDateOfBirth());

            faculty.setUserName(facultyDto.getUserName());
            faculty.setPassword(facultyDto.getPassword());
            faculty.setGender(gender);
            faculty.setRole(role);

            return entityManager.merge(faculty);

        } catch (DataIntegrityViolationException dataIntegrityViolationException) {
            exceptionHandlingService.handleException(dataIntegrityViolationException);
            throw new IndexOutOfBoundsException(dataIntegrityViolationException.getMessage());
        } catch (PersistenceException persistenceException) {
            exceptionHandlingService.handleException(persistenceException);
            throw new PersistenceException(persistenceException.getMessage());
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            throw new Exception(exception.getMessage());
        }
    }

    @Transactional
    public Faculty uploadProfilePicture(Faculty faculty) throws Exception {
        try {

            return entityManager.merge(faculty);

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

    public List<Faculty> getAllFaculty() throws Exception {
        try {

            TypedQuery<Faculty> query = entityManager.createQuery(Constant.GET_ALL_FACULTY, Faculty.class);
            return query.getResultList();

        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            throw new Exception(exception.getMessage());
        }
    }

    public Faculty getFacultyById(Long studentId) throws Exception {
        try {

            TypedQuery<Faculty> query = entityManager.createQuery(Constant.GET_FACULTY_BY_ID, Faculty.class);
            query.setParameter("facultyId", studentId);
            return query.getResultList().get(0);

        } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
            exceptionHandlingService.handleException(indexOutOfBoundsException);
            throw new IndexOutOfBoundsException("Faculty not found with given Id");
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            throw new Exception(exception);
        }
    }

    public Faculty retrieveFacultyByUsername(String username) {

        // Execute the query using JdbcTemplate
        TypedQuery<Faculty> query = entityManager.createQuery("SELECT f FROM Faculty f WHERE f.collegeEmail = :username", Faculty.class);
        query.setParameter("username", username);
        List<Faculty> faculties = query.getResultList();

        // Check if the user exists
        if (faculties.isEmpty()) {
            throw new UsernameNotFoundException("Faculty not found with username: " + username);
        }

        Faculty faculty = faculties.get(0);  // Assuming only one user with this username
        return faculty;
    }

    public void validateAndSaveFacultyForUpdate(FacultyDto facultyDto,Faculty facultyToUpdate) throws Exception {
        try {
            if (Objects.nonNull(facultyDto.getFirstName())) {
                if(facultyDto.getFirstName().isEmpty()) {
                    throw new IllegalArgumentException("Faculty name cannot be empty");
                }
                facultyDto.setFirstName(facultyDto.getFirstName().trim());
                facultyToUpdate.setFirstName(facultyDto.getFirstName());
            }
            if(facultyDto.getLastName() != null) {
                if(facultyDto.getLastName().isEmpty() || facultyDto.getLastName().trim().isEmpty()) {
                    throw new IllegalArgumentException("Last name cannot be empty");
                }
                facultyDto.setLastName(facultyDto.getLastName().trim());
                facultyToUpdate.setFirstName(facultyDto.getLastName());
            }

            if(facultyDto.getCountryCode() != null) {
                if(facultyDto.getCountryCode().isEmpty() || facultyDto.getCountryCode().trim().isEmpty()) {
                    throw new IllegalArgumentException("Country code cannot be empty");
                }
                facultyDto.setCountryCode(facultyDto.getCountryCode().trim());
                facultyToUpdate.setCountryCode(facultyDto.getCountryCode());
            }

            if(facultyDto.getMobileNumber()!=null)
            {
                if(facultyDto.getMobileNumber().trim().isEmpty()) {
                    throw new IllegalArgumentException("Faculty Mobile Number cannot be empty");
                }
                facultyDto.setMobileNumber(facultyDto.getMobileNumber().trim());
                facultyToUpdate.setMobileNumber(facultyDto.getMobileNumber());
            }

            if(facultyDto.getUserName()!=null)
            {
                if(facultyDto.getUserName().trim().isEmpty()) {
                    throw new IllegalArgumentException("User name cannot be empty");
                }
                facultyDto.setUserName(facultyDto.getUserName().trim());
                facultyToUpdate.setUserName(facultyDto.getUserName());

            }
            if(facultyDto.getPassword()!=null)
            {
                String hashedPassword = passwordEncoder.encode(facultyDto.getPassword());
                facultyDto.setPassword(hashedPassword);
                facultyToUpdate.setPassword(facultyDto.getPassword());
            }
            if(facultyDto.getGenderId()!=null)
            {
                if(facultyDto.getGenderId() <= 0) {
                    throw new IllegalArgumentException(("Gender Id cannot be <= 0"));
                }
                Gender gender = genderService.getGenderById(facultyDto.getGenderId());
                facultyToUpdate.setGender(gender);
            }
            if (facultyDto.getCourseIds() != null) {
                if (!facultyDto.getCourseIds().isEmpty()) {
                    // Fetch valid courses
                    List<Course> coursesToAdd = entityManager.createQuery(
                                    "SELECT c FROM Course c WHERE c.courseId IN :courseIds", Course.class)
                            .setParameter("courseIds", facultyDto.getCourseIds())
                            .getResultList();

                    // Validate course IDs
                    if (coursesToAdd.size() != facultyDto.getCourseIds().size()) {
                        throw new IllegalArgumentException("One or more Course IDs are invalid.");
                    }

                    facultyToUpdate.setCourses(coursesToAdd);

                    List<Student> studentsToAdd = new ArrayList<>();
                    for (Course course : coursesToAdd) {
                        for (Student student : course.getStudents()) {
                            if (!studentsToAdd.contains(student)) { // Avoid duplicates
                                studentsToAdd.add(student);
                            }
                        }
                    }

                    facultyToUpdate.setStudents(studentsToAdd);

                    for (Student student : studentsToAdd) {
                        if (!student.getFacultyMembers().contains(facultyToUpdate)) {
                            student.getFacultyMembers().add(facultyToUpdate);
                        }
                    }

                    for (Course course : coursesToAdd) {
                        if (!course.getFacultyMembers().contains(facultyToUpdate)) {
                            course.getFacultyMembers().add(facultyToUpdate);
                        }
                    }
                }
                else {
                    facultyToUpdate.getCourses().clear();
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
    public Faculty updateFaculty(Long facultyId, FacultyDto facultyDto) throws Exception {
        Faculty facultyToUpdate= entityManager.find(Faculty.class,facultyId);
        if(facultyToUpdate==null)
        {
            throw new IllegalArgumentException("Faculty with id "+ facultyId+" not found");
        }
        validateAndSaveFacultyForUpdate(facultyDto,facultyToUpdate);
        return entityManager.merge(facultyToUpdate);
    }

}
