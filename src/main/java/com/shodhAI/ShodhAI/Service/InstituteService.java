package com.shodhAI.ShodhAI.Service;

import com.shodhAI.ShodhAI.Component.Constant;
import com.shodhAI.ShodhAI.Dto.InstituteDto;
import com.shodhAI.ShodhAI.Entity.AcademicDegree;
import com.shodhAI.ShodhAI.Entity.Institute;
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
public class InstituteService {

    @Autowired
    EntityManager entityManager;

    @Autowired
    ExceptionHandlingService exceptionHandlingService;

    @Autowired
    AcademicDegreeService academicDegreeService;

    public void validateInstitute(InstituteDto instituteDto) throws Exception {
        try {
            if (instituteDto.getInstitutionName() == null || instituteDto.getInstitutionName().isEmpty()) {
                throw new IllegalArgumentException("Institute name cannot be null or empty");
            }
            instituteDto.setInstitutionName(instituteDto.getInstitutionName().trim());

            for(Long academicDegreeId: instituteDto.getAcademicDegreeIds()) {
                if(academicDegreeId <= 0) {
                    throw new IllegalArgumentException("Academic Degree Id cannot be <= 0");
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
    public Institute saveInstitute(InstituteDto instituteDto) throws Exception {
        try {
            Institute institute = new Institute();

            Date currentDate = new Date();

            institute.setCreatedDate(currentDate);
            institute.setUpdatedDate(currentDate);
            institute.setInstitutionName(instituteDto.getInstitutionName());

            List<AcademicDegree> academicDegreeList = new ArrayList<>();
            AcademicDegree academicDegree = null;
            for(Long academicDegreeId: instituteDto.getAcademicDegreeIds()) {
                academicDegree = academicDegreeService.getAcademicDegreeById(academicDegreeId);
                academicDegreeList.add(academicDegree);
            }
            institute.setDegrees(academicDegreeList);
            return entityManager.merge(institute);

        } catch (PersistenceException persistenceException) {
            exceptionHandlingService.handleException(persistenceException);
            throw new PersistenceException(persistenceException.getMessage());
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            throw new Exception(exception.getMessage());
        }
    }

    public Institute getInstituteById(Long instituteId) throws Exception {
        try {

            TypedQuery<Institute> query = entityManager.createQuery(Constant.GET_INSTITUTE_BY_ID, Institute.class);
            query.setParameter("instituteId", instituteId);
            return query.getResultList().get(0);

        } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
            exceptionHandlingService.handleException(indexOutOfBoundsException);
            throw new IndexOutOfBoundsException("Institute not found with given Id");
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            throw new Exception(exception);
        }
    }

}
