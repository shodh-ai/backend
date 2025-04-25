package com.shodhAI.ShodhAI.Service;

import com.shodhAI.ShodhAI.Component.Constant;
import com.shodhAI.ShodhAI.Dto.CohortDto;
import com.shodhAI.ShodhAI.Dto.InstituteDto;
import com.shodhAI.ShodhAI.Entity.AcademicDegree;
import com.shodhAI.ShodhAI.Entity.Cohort;
import com.shodhAI.ShodhAI.Entity.Course;
import com.shodhAI.ShodhAI.Entity.Institute;
import com.shodhAI.ShodhAI.Entity.Role;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class CohortService {

    @Autowired
    EntityManager entityManager;

    @Autowired
    RoleService roleService;

    @Autowired
    CourseService courseService;

    @Autowired
    ExceptionHandlingService exceptionHandlingService;

    public void validateCohort(CohortDto cohortDto) throws Exception {
        try {
            if (cohortDto.getCohortTitle() == null || cohortDto.getCohortTitle().isEmpty()) {
                throw new IllegalArgumentException("Cohort title cannot be null or empty");
            }
            cohortDto.setCohortTitle(cohortDto.getCohortTitle().trim());

            if (cohortDto.getCohortDescription() != null) {
                if (cohortDto.getCohortDescription().isEmpty() || cohortDto.getCohortDescription().trim().isEmpty()) {
                    throw new IllegalArgumentException("Cohort Description cannot be empty");
                }
                cohortDto.setCohortDescription(cohortDto.getCohortDescription().trim());
            }

            if (cohortDto.getCourseId() == null || cohortDto.getCourseId() <= 0) {
                throw new IllegalArgumentException("Course Id cannot be null or <= 0");
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
    public Cohort saveCohort(CohortDto cohortDto, Long userId, Long roleId) throws Exception {
        try {

            Cohort cohort = new Cohort();
            Course course = courseService.getCourseById(cohortDto.getCourseId());
            Role role = roleService.getRoleById(roleId);

            Date currentDate = new Date();

            cohort.setCreatedDate(currentDate);
            cohort.setUpdatedDate(currentDate);
            cohort.setCohortTitle(cohortDto.getCohortTitle());
            if(cohortDto.getCohortDescription() != null) {
                cohort.setCohortDescription(cohortDto.getCohortDescription());
            }
            cohort.setCreatorUserId(userId);
            cohort.setCreatorRole(role);
            cohort.setCourse(course);

            return entityManager.merge(cohort);

        } catch (PersistenceException persistenceException) {
            exceptionHandlingService.handleException(persistenceException);
            throw new PersistenceException(persistenceException.getMessage());
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            throw new Exception(exception.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public List<Cohort> getAllCohort() throws Exception {
        try {

            TypedQuery<Cohort> query = entityManager.createQuery(Constant.GET_ALL_COHORTS, Cohort.class);
            return query.getResultList();

        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            throw new Exception(exception.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public List<Cohort> filterCohorts(Long courseId) throws Exception {
        try{
            StringBuilder queryString = new StringBuilder("SELECT c FROM Cohort c WHERE 1 = 1 AND c.archived != 'Y'");

            Course course = null;
            if(courseId != null) {
                course = courseService.getCourseById(courseId);
                queryString.append(" AND c.course = :course");
            }

            TypedQuery<Cohort> query = entityManager.createQuery(queryString.toString(), Cohort.class);

            if (courseId != null) {
                query.setParameter("course", course);
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

    @Transactional(readOnly = true)
    public Cohort getCohortById(Long cohortId) throws Exception {
        try {

            TypedQuery<Cohort> query = entityManager.createQuery(Constant.GET_COHORT_BY_ID, Cohort.class);
            query.setParameter("cohortId", cohortId);
            return query.getResultList().get(0);

        } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
            exceptionHandlingService.handleException(indexOutOfBoundsException);
            throw new IndexOutOfBoundsException("Cohort not found with given Id");
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            throw new Exception(exception);
        }
    }

    @Transactional
    public Cohort removeCohortById(Cohort cohort) throws Exception {
        try {

            cohort.setArchived('Y');
            return entityManager.merge(cohort);

        } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
            exceptionHandlingService.handleException(indexOutOfBoundsException);
            throw new IndexOutOfBoundsException("Institute not found with given Id");
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            throw new Exception(exception);
        }
    }

    @Transactional
    public Cohort updateCohort(Cohort cohort, CohortDto cohortDto, Long userId, Long roleId) throws Exception {
        try {
            Date modifiedDate = new Date();

            if(cohortDto.getCourseId() != null) {
                Course course = courseService.getCourseById(cohortDto.getCourseId());
                cohort.setCourse(course);
            }
            Role role = roleService.getRoleById(roleId);

            Date currentDate = new Date();

            cohort.setUpdatedDate(currentDate);
            if(cohortDto.getCohortTitle() != null) {
                if(cohortDto.getCohortTitle().trim().isEmpty()) {
                    throw new IllegalArgumentException("Cohort title cannot be empty");
                }
                cohort.setCohortTitle(cohortDto.getCohortTitle().trim());
            }
            if(cohortDto.getCohortDescription() != null) {
                if(cohortDto.getCohortDescription().trim().isEmpty()) {
                    throw new IllegalArgumentException("Cohort Description cannot be empty");
                }
                cohort.setCohortDescription(cohortDto.getCohortDescription().trim());
            }

            cohort.setModifierUserId(userId);
            cohort.setModifierRole(role);
            return entityManager.merge(cohort);

        } catch (PersistenceException persistenceException) {
            exceptionHandlingService.handleException(persistenceException);
            throw new PersistenceException(persistenceException.getMessage());
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            throw new Exception(exception.getMessage());
        }
    }

}
