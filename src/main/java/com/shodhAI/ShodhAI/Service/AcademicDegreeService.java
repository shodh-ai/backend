package com.shodhAI.ShodhAI.Service;

import com.shodhAI.ShodhAI.Component.Constant;
import com.shodhAI.ShodhAI.Dto.AcademicDegreeDto;
import com.shodhAI.ShodhAI.Entity.AcademicDegree;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

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

}
