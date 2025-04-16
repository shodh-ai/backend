package com.shodhAI.ShodhAI.Service;

import com.shodhAI.ShodhAI.Component.Constant;
import com.shodhAI.ShodhAI.Dto.CourseSemesterDegreeDto;
import com.shodhAI.ShodhAI.Dto.SemesterDto;
import com.shodhAI.ShodhAI.Entity.AcademicDegree;
import com.shodhAI.ShodhAI.Entity.Course;
import com.shodhAI.ShodhAI.Entity.CourseSemesterDegree;
import com.shodhAI.ShodhAI.Entity.Role;
import com.shodhAI.ShodhAI.Entity.Semester;
import com.shodhAI.ShodhAI.Entity.Student;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import java.util.stream.Collectors;

@Service
public class SemesterService {
    @Autowired
    private SharedUtilityService sharedUtilityService;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private ExceptionHandlingService exceptionHandlingService;

    String DATE_FORMAT = "dd-MM-yyyy";

    @Autowired
    RoleService roleService;

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
    public Semester saveSemester(SemesterDto semesterDto) throws Exception {
        Semester semesterToAdd = new Semester();
       long semesterCount= findSemesterCount();
        semesterToAdd.setSemesterId(semesterCount+1);
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
    try
    {
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
            if(semesterDto.getAcademicDegreeIds()!=null)
            {
                List<AcademicDegree> academicDegrees = new ArrayList<>();
                for (Long degreeId : semesterDto.getAcademicDegreeIds()) {
                    AcademicDegree degree = entityManager.find(AcademicDegree.class, degreeId);
                    if (degree == null) {
                        throw new IllegalArgumentException("Academic degree not found with id: " + degreeId);
                    }
                    academicDegrees.add(degree);
                }

                // Clear existing degrees first to avoid duplicates
                for (AcademicDegree existingDegree : semesterToUpdate.getAcademicDegrees()) {
                    existingDegree.getSemesters().remove(semesterToUpdate);
                    entityManager.merge(existingDegree);
                }

                semesterToUpdate.getAcademicDegrees().clear();

                // Add new degrees
                for (AcademicDegree academicDegree : academicDegrees) {
                    semesterToUpdate.getAcademicDegrees().add(academicDegree);
                    academicDegree.getSemesters().add(semesterToUpdate);
                    entityManager.merge(academicDegree);
                }

            }
            return entityManager.merge(semesterToUpdate);
        }catch (IllegalArgumentException illegalArgumentException) {
            exceptionHandlingService.handleException(illegalArgumentException);
            throw new IllegalArgumentException(illegalArgumentException.getMessage());
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            throw new Exception(exception.getMessage());
        }
    }

    @Transactional
    public Semester updateSemester(Long semesterId, SemesterDto semesterDto) throws Exception {
        try
        {
            Semester semesterToUpdate = entityManager.find(Semester.class, semesterId);
            if (semesterToUpdate == null) {
                throw new IllegalArgumentException("Semester with id " + semesterId + " does not exist");
            }

            // Store the original degree associations before updating
            Set<Long> originalDegreeIds = semesterToUpdate.getAcademicDegrees().stream()
                    .map(AcademicDegree::getDegreeId)
                    .collect(Collectors.toSet());

            // Update the semester
            Semester updatedSemester = validateAndSaveSemesterForUpdate(semesterDto, semesterToUpdate);

            // Now handle the cascading effect on course associations
            if (semesterDto.getAcademicDegreeIds() != null) {
                Set<Long> newDegreeIds = new HashSet<>(semesterDto.getAcademicDegreeIds());

                // Find degree associations that were removed
                Set<Long> removedDegreeIds = new HashSet<>(originalDegreeIds);
                removedDegreeIds.removeAll(newDegreeIds);

                if (!removedDegreeIds.isEmpty()) {
                    // Remove course associations for removed degree-semester pairs
                    deleteCourseSemesterDegreeAssociations(semesterId, removedDegreeIds);
                }
            }

            // Handle course associations - checking if the field exists in the request
            if (semesterDto.getCourseAssociations() != null) {
                // If course_associations is explicitly set to an empty list, clear all existing associations
                if (semesterDto.getCourseAssociations().isEmpty()) {
                    // Delete all course associations for this semester
                    deleteAllCourseSemesterDegreeAssociations(semesterId);
                } else {
                    // First remove all existing associations to avoid duplicates and stale data
                    deleteAllCourseSemesterDegreeAssociations(semesterId);
                    // Then add the new associations
                    addCoursesToSemester(semesterId, semesterDto.getCourseAssociations());
                }
            }

            return updatedSemester;
        }catch (IllegalArgumentException illegalArgumentException) {
            exceptionHandlingService.handleException(illegalArgumentException);
            throw new IllegalArgumentException(illegalArgumentException.getMessage());
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            throw new Exception(exception.getMessage());
        }

    }

    @Transactional
    private void deleteAllCourseSemesterDegreeAssociations(Long semesterId) throws Exception {
        try
        {
            entityManager.createQuery(
                            "DELETE FROM CourseSemesterDegree csd WHERE csd.semester.semesterId = :semesterId")
                    .setParameter("semesterId", semesterId)
                    .executeUpdate();
        }catch (IllegalArgumentException illegalArgumentException) {
            exceptionHandlingService.handleException(illegalArgumentException);
            throw new IllegalArgumentException(illegalArgumentException.getMessage());
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            throw new Exception(exception.getMessage());
        }

    }

    @Transactional
    private void deleteCourseSemesterDegreeAssociations(Long semesterId, Set<Long> degreeIds) throws Exception {
        try
        {
            Query query = entityManager.createQuery(
                    "DELETE FROM CourseSemesterDegree csd WHERE csd.semester.semesterId = :semesterId " +
                            "AND csd.academicDegree.degreeId IN :degreeIds");

            query.setParameter("semesterId", semesterId);
            query.setParameter("degreeIds", degreeIds);
            query.executeUpdate();
        }catch (IllegalArgumentException illegalArgumentException) {
            exceptionHandlingService.handleException(illegalArgumentException);
            throw new IllegalArgumentException(illegalArgumentException.getMessage());
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            throw new Exception(exception.getMessage());
        }

    }

    @Transactional
    public void addCoursesToSemester(Long semesterId, List<CourseSemesterDegreeDto> courseAssociations) throws Exception {
        try
        {
            Semester semester = entityManager.find(Semester.class, semesterId);
            if (semester == null) {
                throw new IllegalArgumentException("Semester with id " + semesterId + " does not exist");
            }

            for (CourseSemesterDegreeDto association : courseAssociations) {
                Course course = entityManager.find(Course.class, association.getCourseId());
                if (course == null) {
                    throw new IllegalArgumentException("Course not found with id: " + association.getCourseId());
                }

                AcademicDegree degree = entityManager.find(AcademicDegree.class, association.getAcademicDegreeId());
                if (degree == null) {
                    throw new IllegalArgumentException("Academic degree not found with id: " + association.getAcademicDegreeId());
                }

                // Verify the semester is associated with the degree
                boolean isAssociated = semester.getAcademicDegrees().stream()
                        .anyMatch(ad -> ad.getDegreeId().equals(degree.getDegreeId()));

                if (!isAssociated) {
                    throw new IllegalArgumentException(
                            "Semester " + semester.getSemesterName() + " is not associated with degree " +
                                    degree.getDegreeName());
                }

                // Check if association already exists
                List<CourseSemesterDegree> existingAssociations = entityManager.createQuery(
                                "SELECT csd FROM CourseSemesterDegree csd " +
                                        "WHERE csd.course.courseId = :courseId " +
                                        "AND csd.semester.semesterId = :semesterId " +
                                        "AND csd.academicDegree.degreeId = :degreeId", CourseSemesterDegree.class)
                        .setParameter("courseId", course.getCourseId())
                        .setParameter("semesterId", semesterId)
                        .setParameter("degreeId", degree.getDegreeId())
                        .getResultList();

                if (existingAssociations.isEmpty()) {
                    // Create new association
                    CourseSemesterDegree newAssociation = new CourseSemesterDegree();
                    long courseDegreeSemesterCount=findCourseDegreeSemesterCount();
                    newAssociation.setId( (courseDegreeSemesterCount+1));
                    newAssociation.setCourse(course);
                    newAssociation.setSemester(semester);
                    newAssociation.setAcademicDegree(degree);
                    newAssociation.setCreatedDate(new Date());
                    newAssociation.setUpdatedDate(new Date());

                    // Don't set the ID manually, let JPA/Hibernate generate it
                    entityManager.persist(newAssociation);
                }
            }
        }catch (IllegalArgumentException illegalArgumentException) {
            exceptionHandlingService.handleException(illegalArgumentException);
            throw new IllegalArgumentException(illegalArgumentException.getMessage());
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            throw new Exception(exception.getMessage());
        }

    }

    @Transactional
    public List<Semester> semesterFilter(Long semesterId, Long userId, Long roleId, Long academicDegreeId) throws Exception {
        try {
            Role role= roleService.getRoleById(roleId);
            if(role.getRoleName().equals(Constant.ROLE_USER))
            {
                Student student= entityManager.find(Student.class,userId);
                academicDegreeId = student.getAcademicDegree().getDegreeId();
            }
            StringBuilder jpql = new StringBuilder("SELECT DISTINCT s FROM Semester s ");

            // If filtering by academic degree, we need to join the academic_degrees
            if (academicDegreeId != null) {
                jpql.append("JOIN s.academicDegrees ad WHERE 1=1 ");
            } else {
                jpql.append("WHERE 1=1 ");
            }

            if (semesterId != null) {
                jpql.append("AND s.archived='N' AND s.semesterId = :semesterId ");
            }

            if (academicDegreeId != null) {
                jpql.append("AND ad.archived='N' AND ad.degreeId = :academicDegreeId ");
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

    public long findCourseDegreeSemesterCount() throws Exception {
        try {
            String queryString = "SELECT MAX(c.id) FROM CourseSemesterDegree c";
            TypedQuery<Long> query = entityManager.createQuery(queryString, Long.class);
            Long maxId = query.getSingleResult();

            return (maxId != null) ? maxId : 0;  // If no records exist, return 0
        } catch (NoResultException e) {
            exceptionHandlingService.handleException(e);
            throw new NoResultException("No course_semester_degree association is found");
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            throw new Exception("SOMETHING WENT WRONG: " + exception.getMessage());
        }
    }

    public long findSemesterCount() throws Exception {
        try {
            String queryString = "SELECT MAX(c.id) FROM Semester c";
            TypedQuery<Long> query = entityManager.createQuery(queryString, Long.class);
            Long maxId = query.getSingleResult();

            return (maxId != null) ? maxId : 0;  // If no records exist, return 0
        } catch (NoResultException e) {
            exceptionHandlingService.handleException(e);
            throw new NoResultException("No semester is found");
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            throw new Exception("SOMETHING WENT WRONG: " + exception.getMessage());
        }
    }
}
