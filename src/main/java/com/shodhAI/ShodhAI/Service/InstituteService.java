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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public void validateUpdateInstitute(InstituteDto instituteDto) throws Exception {
        try {
            if (instituteDto.getInstitutionName() != null) {
                if (instituteDto.getInstitutionName().isEmpty()) {
                    throw new IllegalArgumentException("Institute name cannot be empty");
                }
                instituteDto.setInstitutionName(instituteDto.getInstitutionName().trim());
            }

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

    @Transactional
    public Institute updateInstituteById(Institute institute, InstituteDto instituteDto) throws Exception {
        try {
            Date modifiedDate = new Date();

            institute.setUpdatedDate(modifiedDate);
            if(instituteDto.getInstitutionName() != null) {
                institute.setInstitutionName(instituteDto.getInstitutionName());
            }

            List<AcademicDegree> academicDegreeList = new ArrayList<>();
            AcademicDegree academicDegree = null;
            for(Long academicDegreeId: instituteDto.getAcademicDegreeIds()) {
                academicDegree = academicDegreeService.getAcademicDegreeById(academicDegreeId);
                academicDegreeList.add(academicDegree);
            }
            if(!academicDegreeList.isEmpty()) {
                institute.setDegrees(academicDegreeList);
            }
            return entityManager.merge(institute);

        } catch (PersistenceException persistenceException) {
            exceptionHandlingService.handleException(persistenceException);
            throw new PersistenceException(persistenceException.getMessage());
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            throw new Exception(exception.getMessage());
        }
    }

    @Transactional(readOnly = true)
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

    @Transactional(readOnly = true)
    public List<Institute> filterInstitute(Long instituteId, String institutionName) throws Exception {
        try {
            StringBuilder jpql = new StringBuilder("SELECT DISTINCT i FROM Institute i WHERE 1=1 ");

            Map<String, Object> params = new HashMap<>();

            if (instituteId != null) {
                jpql.append("AND i.instituteId = :instituteId ");
                params.put("instituteId", instituteId);
            }

            if (institutionName != null) {
                jpql.append("AND i.institutionName = :institutionName ");
                params.put("institutionName", institutionName);
            }

            jpql.append("AND i.archived = 'N'");

            TypedQuery<Institute> query = entityManager.createQuery(jpql.toString(), Institute.class);

            for (Map.Entry<String, Object> entry : params.entrySet()) {
                query.setParameter(entry.getKey(), entry.getValue());
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

    @Transactional
    public Institute removeInstituteById(Institute institute) throws Exception {
        try {

            institute.setArchived('Y');
            return entityManager.merge(institute);

        } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
            exceptionHandlingService.handleException(indexOutOfBoundsException);
            throw new IndexOutOfBoundsException("Institute not found with given Id");
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            throw new Exception(exception);
        }
    }

}
