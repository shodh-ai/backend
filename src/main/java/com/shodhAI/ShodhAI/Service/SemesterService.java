package com.shodhAI.ShodhAI.Service;

import com.shodhAI.ShodhAI.Component.Constant;
import com.shodhAI.ShodhAI.Dto.SemesterDto;
import com.shodhAI.ShodhAI.Entity.AcademicDegree;
import com.shodhAI.ShodhAI.Entity.Course;
import com.shodhAI.ShodhAI.Entity.Semester;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

@Service
public class SemesterService {
    @Autowired
    private SharedUtilityService sharedUtilityService;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private ExceptionHandlingService exceptionHandlingService;

    String DATE_FORMAT = "dd-MM-yyyy";

    public void validateSemester(SemesterDto semesterDto) throws Exception {
        try {
            if (semesterDto.getSemesterName() == null || semesterDto.getSemesterName().isEmpty()) {
                throw new IllegalArgumentException("Semester name cannot be null or empty");
            }
            semesterDto.setSemesterName(semesterDto.getSemesterName().trim());
            if (semesterDto.getStartDate() == null || semesterDto.getStartDate().isEmpty()) {
                throw new IllegalArgumentException("Start date of a semester cannot be null or empty");
            }
            semesterDto.setStartDate(semesterDto.getStartDate().trim());
            sharedUtilityService.validateDate(semesterDto.getStartDate(), DATE_FORMAT, "Start date");

            if (semesterDto.getEndDate() == null || semesterDto.getEndDate().isEmpty()) {
                throw new IllegalArgumentException("End date of a semester cannot be null or empty");
            }
            sharedUtilityService.validateDate(semesterDto.getEndDate(), DATE_FORMAT, "End date");
            sharedUtilityService.compareTwoDates(convertStringToDate(semesterDto.getStartDate(), DATE_FORMAT), convertStringToDate(semesterDto.getEndDate(), DATE_FORMAT), "Semester");
            semesterDto.setEndDate(semesterDto.getEndDate().trim());
        } catch (IllegalArgumentException illegalArgumentException) {
            exceptionHandlingService.handleException(illegalArgumentException);
            throw new IllegalArgumentException(illegalArgumentException.getMessage());
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            throw new Exception(exception.getMessage());
        }

    }

    @Transactional
    public Semester saveSemester(SemesterDto semesterDto) throws ParseException {
        Semester semesterToAdd = new Semester();
        semesterToAdd.setSemesterName(semesterDto.getSemesterName());

        semesterToAdd.setStartDate(convertStringToDate(semesterDto.getStartDate(), DATE_FORMAT));
        semesterToAdd.setEndDate(convertStringToDate(semesterDto.getEndDate(), DATE_FORMAT));

        entityManager.persist(semesterToAdd);
        return semesterToAdd;
    }

    public static Date convertStringToDate(String dateStr, String s) throws ParseException {
        if (dateStr == null || dateStr.isEmpty()) {
            throw new IllegalArgumentException("Date string cannot be null or empty");
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat(s);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        dateFormat.setLenient(false);
        return dateFormat.parse(dateStr);
    }


    public List<Semester> getAllSemesters() throws Exception {
        try {

            TypedQuery<Semester> query = entityManager.createQuery(Constant.GET_ALL_SEMESTERS, Semester.class);
            return query.getResultList();

        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            throw new Exception(exception.getMessage());
        }
    }

    public Semester getSemesterById(Long semesterId) throws Exception {
        try {

            TypedQuery<Semester> query = entityManager.createQuery(Constant.GET_SEMESTER_BY_ID, Semester.class);
            query.setParameter("semesterId", semesterId);
            if (query.getResultList().isEmpty()) {
                throw new IllegalArgumentException("Semester with id " + semesterId + " not found");
            }
            return query.getResultList().get(0);

        } catch (IllegalArgumentException illegalArgumentException) {
            exceptionHandlingService.handleException(illegalArgumentException);
            throw new IllegalArgumentException(illegalArgumentException.getMessage());
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            throw new Exception(exception);
        }
    }

    @Transactional
    public Semester validateAndSaveSemesterForUpdate(SemesterDto semesterDto, Semester semesterToUpdate) throws Exception {

        Date startDate = null;
        Date endDate = null;
        if (semesterDto.getSemesterName() != null) {
            if (semesterDto.getSemesterName().isEmpty()) {
                throw new IllegalArgumentException("Semester name cannot be empty");
            }
            semesterDto.setSemesterName(semesterDto.getSemesterName().trim());
            semesterToUpdate.setSemesterName(semesterDto.getSemesterName());
        }

        if (semesterDto.getStartDate() != null && semesterDto.getEndDate() != null) {
            if (semesterDto.getStartDate().isEmpty()) {
                throw new IllegalArgumentException("Start date of a semester cannot be empty");
            }
            sharedUtilityService.validateDate(semesterDto.getStartDate(), DATE_FORMAT, "Start date");
            semesterDto.setStartDate(semesterDto.getStartDate().trim());
            if (semesterDto.getEndDate().isEmpty()) {
                throw new IllegalArgumentException("End date of a semester cannot be null or empty");
            }
            sharedUtilityService.validateDate(semesterDto.getEndDate(), DATE_FORMAT, "End date");
            semesterDto.setEndDate(semesterDto.getEndDate().trim());
            sharedUtilityService.compareTwoDates(convertStringToDate(semesterDto.getStartDate(), DATE_FORMAT), convertStringToDate(semesterDto.getEndDate(), DATE_FORMAT), "Semester");
            semesterToUpdate.setStartDate(convertStringToDate(semesterDto.getStartDate(), DATE_FORMAT));
            semesterToUpdate.setEndDate(convertStringToDate(semesterDto.getEndDate(), DATE_FORMAT));
        } else {
            if (semesterDto.getStartDate() != null) {
                if (semesterDto.getStartDate().isEmpty()) {
                    throw new IllegalArgumentException("Start date of a semester cannot be empty");
                }
                sharedUtilityService.validateDate(semesterDto.getStartDate(), DATE_FORMAT, "Start date");
                semesterDto.setStartDate(semesterDto.getStartDate().trim());
                semesterToUpdate.setStartDate(convertStringToDate(semesterDto.getStartDate(), DATE_FORMAT));
                startDate = convertStringToDate(semesterDto.getStartDate(), DATE_FORMAT);
            } else {
                startDate = semesterToUpdate.getStartDate();
            }

            if (semesterDto.getEndDate() != null) {
                if (semesterDto.getEndDate().isEmpty()) {
                    throw new IllegalArgumentException("End date of a semester cannot be null or empty");
                }
                sharedUtilityService.validateDate(semesterDto.getEndDate(), DATE_FORMAT, "End date");
                semesterDto.setEndDate(semesterDto.getEndDate().trim());
                semesterToUpdate.setEndDate(convertStringToDate(semesterDto.getEndDate(), DATE_FORMAT));
                endDate = convertStringToDate(semesterDto.getEndDate(), DATE_FORMAT);
            } else {
                endDate = semesterToUpdate.getEndDate();
            }
            sharedUtilityService.compareTwoDates(startDate, endDate, "Semester");

        }

       /* if (semesterDto.getAcademicDegreeId() != null) {
            AcademicDegree academicDegree = entityManager.find(AcademicDegree.class, semesterDto.getAcademicDegreeId());
            if (academicDegree == null) {
                throw new IllegalArgumentException("Academic degree with id " + semesterDto.getAcademicDegreeId() + " does not found");
            }
            semesterToUpdate.setAcademicDegree(academicDegree);
        }

        if (semesterDto.getCourseIds() != null) {
            // Clear existing courses
            List<Course> existingCourses = new ArrayList<>(semesterToUpdate.getCourses());
            for (Course course : existingCourses) {
                course.setSemester(null);  // Detach previous semester association
                semesterToUpdate.getCourses().remove(course);
            }

            // Add new courses
            List<Course> courseListToAdd = new ArrayList<>();
            for (Long courseId : semesterDto.getCourseIds()) {
                Course course = entityManager.find(Course.class, courseId);
                if (course == null) {
                    throw new IllegalArgumentException("Course with id " + courseId + " does not exist");
                }
                course.setSemester(semesterToUpdate); // Set semester reference in Course entity
                courseListToAdd.add(course);
            }

            semesterToUpdate.getCourses().addAll(courseListToAdd);
        }*/

        return entityManager.merge(semesterToUpdate);
    }

    @Transactional
    public List<Semester> semesterFilter(Long semesterId, Long userId, Long roleId, Long academicDegreeId) throws Exception {
        try {
            StringBuilder jpql = new StringBuilder("SELECT DISTINCT s FROM Semester s ");

            // If filtering by academic degree, we need to join the academic_degrees
            if (academicDegreeId != null) {
                jpql.append("JOIN s.academicDegrees ad WHERE 1=1 ");
            } else {
                jpql.append("WHERE 1=1 ");
            }

            if (semesterId != null) {
                jpql.append("AND s.semesterId = :semesterId ");
            }

            if (academicDegreeId != null) {
                jpql.append("AND ad.degreeId = :academicDegreeId ");
            }

            // Add ORDER BY clause to sort by semesterId
            jpql.append("ORDER BY s.semesterId ASC");

            // Create the query
            TypedQuery<Semester> query = entityManager.createQuery(jpql.toString(), Semester.class);

            // Set parameters
            if (semesterId != null) {
                query.setParameter("semesterId", semesterId);
            }

            if (academicDegreeId != null) {
                query.setParameter("academicDegreeId", academicDegreeId);
            }

            return query.getResultList();

        } catch (PersistenceException persistenceException) {
            exceptionHandlingService.handleException(persistenceException);
            throw new PersistenceException(persistenceException.getMessage());
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            throw new Exception(exception.getMessage());
        }
    }
}
