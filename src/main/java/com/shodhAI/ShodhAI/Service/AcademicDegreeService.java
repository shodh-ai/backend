package com.shodhAI.ShodhAI.Service;

import com.shodhAI.ShodhAI.Component.Constant;
import com.shodhAI.ShodhAI.Dto.AcademicDegreeDto;
import com.shodhAI.ShodhAI.Entity.AcademicDegree;
import com.shodhAI.ShodhAI.Entity.CourseSemesterDegree;
import com.shodhAI.ShodhAI.Entity.Semester;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AcademicDegreeService {

    @Autowired
    EntityManager entityManager;

    @Autowired
    ExceptionHandlingService exceptionHandlingService;

    public void validateAcademicDegree(AcademicDegreeDto academicDegreeDto) throws Exception {
        try {
            if (academicDegreeDto.getDegreeName() == null || academicDegreeDto.getDegreeName().isEmpty()) {
                throw new IllegalArgumentException("Degree name cannot be null or empty");
            }
            academicDegreeDto.setDegreeName(academicDegreeDto.getDegreeName().trim());

            if (academicDegreeDto.getInstitutionName() != null) {
                if (academicDegreeDto.getInstitutionName().isEmpty() || academicDegreeDto.getInstitutionName().trim().isEmpty()) {
                    throw new IllegalArgumentException("Institute name cannot be empty");
                }
                academicDegreeDto.setInstitutionName(academicDegreeDto.getInstitutionName().trim());
            }

            if (academicDegreeDto.getProgramName() != null) {
                if (academicDegreeDto.getProgramName().isEmpty()) {
                    throw new IllegalArgumentException("Program name cannot be null or empty");
                }
                academicDegreeDto.setProgramName(academicDegreeDto.getProgramName().trim());
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
    public AcademicDegree saveAcademicDegree(AcademicDegreeDto academicDegreeDto) throws Exception {
        try {
            AcademicDegree academicDegree = new AcademicDegree();

            Date currentDate = new Date();
            long academicDegreeCount=findAcademicDegreeCount();
            academicDegree.setDegreeId(academicDegreeCount+1);
            academicDegree.setCreatedDate(currentDate);
            academicDegree.setUpdatedDate(currentDate);
            academicDegree.setDegreeName(academicDegreeDto.getDegreeName());
            academicDegree.setInstitutionName(academicDegreeDto.getInstitutionName());
            academicDegree.setProgramName(academicDegreeDto.getProgramName());
            return entityManager.merge(academicDegree);

        } catch (PersistenceException persistenceException) {
            exceptionHandlingService.handleException(persistenceException);
            throw new PersistenceException(persistenceException.getMessage());
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            throw new Exception(exception.getMessage());
        }
    }

    public List<AcademicDegree> getAllAcademicDegree() throws Exception {
        try {

            TypedQuery<AcademicDegree> query = entityManager.createQuery(Constant.GET_ALL_ACADEMIC_DEGREES, AcademicDegree.class);
            return query.getResultList();

        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            throw new Exception(exception.getMessage());
        }
    }

    public AcademicDegree getAcademicDegreeById(Long degreeId) throws Exception {
        try {

            TypedQuery<AcademicDegree> query = entityManager.createQuery(Constant.GET_ACADEMIC_DEGREE_BY_ID, AcademicDegree.class);
            query.setParameter("degreeId", degreeId);
            return query.getResultList().get(0);

        } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
            exceptionHandlingService.handleException(indexOutOfBoundsException);
            throw new IndexOutOfBoundsException("Degree not found with given Id");
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            throw new Exception(exception);
        }
    }

    @Transactional
    public AcademicDegree updateAndSaveAcademicDegree(AcademicDegreeDto academicDegreeDto, AcademicDegree academicDegreeToUpdate)
    {
        if(academicDegreeDto.getDegreeName()!=null)
        {
            if (academicDegreeDto.getDegreeName().isEmpty()) {
                throw new IllegalArgumentException("Degree name cannot be null or empty");
            }
            academicDegreeDto.setDegreeName(academicDegreeDto.getDegreeName().trim());
            academicDegreeToUpdate.setDegreeName(academicDegreeDto.getDegreeName());
        }

        if (academicDegreeDto.getInstitutionName() != null) {
            if (academicDegreeDto.getInstitutionName().isEmpty() || academicDegreeDto.getInstitutionName().trim().isEmpty()) {
                throw new IllegalArgumentException("Institute name cannot be empty");
            }
            academicDegreeDto.setInstitutionName(academicDegreeDto.getInstitutionName().trim());
            academicDegreeToUpdate.setDegreeName(academicDegreeDto.getDegreeName());
        }

        if (academicDegreeDto.getProgramName() != null) {
            if (academicDegreeDto.getProgramName().isEmpty()) {
                throw new IllegalArgumentException("Program name cannot be null or empty");
            }
            academicDegreeDto.setProgramName(academicDegreeDto.getProgramName().trim());
            academicDegreeToUpdate.setProgramName(academicDegreeDto.getProgramName());
        }

        if (academicDegreeToUpdate.getCourseSemesterDegrees() != null) {
            for (CourseSemesterDegree csd : academicDegreeToUpdate.getCourseSemesterDegrees()) {
                csd.setAcademicDegree(academicDegreeToUpdate);
                entityManager.merge(csd);
            }
        }

        List<Semester> semesters = new ArrayList<>();
        for (Long semesterId : academicDegreeDto.getSemesterIds()) {
            Semester semester = entityManager.find(Semester.class, semesterId);
            if (semester == null) {
                throw new IllegalArgumentException("Semester not found with id: " + semesterId);
            }
            semesters.add(semester);
        }

        // Clear existing semesters first to avoid duplicates
        for (Semester existingSemester : academicDegreeToUpdate.getSemesters()) {
            existingSemester.getAcademicDegrees().remove(academicDegreeToUpdate);
            entityManager.merge(existingSemester);
        }

        academicDegreeToUpdate.getSemesters().clear();

        // Add new semesters
        for (Semester semester : semesters) {
            academicDegreeToUpdate.getSemesters().add(semester);
            semester.getAcademicDegrees().add(academicDegreeToUpdate);
            entityManager.merge(semester);
        }

        // Update modified date
        academicDegreeToUpdate.setUpdatedDate(new Date());
        entityManager.merge(academicDegreeToUpdate);
        return academicDegreeToUpdate;
    }

    @Transactional
    public AcademicDegree updateAcademicDegree(Long degreeId, AcademicDegreeDto academicDegreeDto) throws Exception {
        AcademicDegree academicDegreeToUpdate = entityManager.find(AcademicDegree.class, degreeId);
        if (academicDegreeToUpdate == null) {
            throw new IllegalArgumentException("Academic degree with id " + degreeId + " does not exist");
        }

        // Store the original semester associations before updating
        Set<Long> originalSemesterIds = academicDegreeToUpdate.getSemesters().stream()
                .map(Semester::getSemesterId)
                .collect(Collectors.toSet());

        // Update the degree
        AcademicDegree updatedDegree = updateAndSaveAcademicDegree(academicDegreeDto, academicDegreeToUpdate);

        // Now handle the cascading effect on course associations
        if (academicDegreeDto.getSemesterIds() != null) {
            Set<Long> newSemesterIds = new HashSet<>(academicDegreeDto.getSemesterIds());

            // Find semester associations that were removed
            Set<Long> removedSemesterIds = new HashSet<>(originalSemesterIds);
            removedSemesterIds.removeAll(newSemesterIds);

            if (!removedSemesterIds.isEmpty()) {
                // Remove course associations for removed semester-degree pairs
                deleteCourseDegreeAssociationsForSemesters(degreeId, removedSemesterIds);
            }
        }

        return updatedDegree;
    }

    @Transactional
    private void deleteCourseDegreeAssociationsForSemesters(Long degreeId, Set<Long> semesterIds) {
        Query query = entityManager.createQuery(
                "DELETE FROM CourseSemesterDegree csd WHERE csd.academicDegree.degreeId = :degreeId " +
                        "AND csd.semester.semesterId IN :semesterIds");

        query.setParameter("degreeId", degreeId);
        query.setParameter("semesterIds", semesterIds);
        query.executeUpdate();
    }

    public long findAcademicDegreeCount() throws Exception {
        try {
            String queryString = "SELECT MAX(c.id) FROM AcademicDegree c";
            TypedQuery<Long> query = entityManager.createQuery(queryString, Long.class);
            Long maxId = query.getSingleResult();

            return (maxId != null) ? maxId : 0;  // If no records exist, return 0
        } catch (NoResultException e) {
            exceptionHandlingService.handleException(e);
            throw new NoResultException("No degree is found");
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            throw new Exception("SOMETHING WENT WRONG: " + exception.getMessage());
        }
    }

}
