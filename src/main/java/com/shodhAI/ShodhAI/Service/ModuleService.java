package com.shodhAI.ShodhAI.Service;

import com.shodhAI.ShodhAI.Component.Constant;
import com.shodhAI.ShodhAI.Dto.ModuleDto;
import com.shodhAI.ShodhAI.Entity.Course;
import com.shodhAI.ShodhAI.Entity.Module;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class ModuleService {

    @Autowired
    EntityManager entityManager;

    @Autowired
    ExceptionHandlingService exceptionHandlingService;

    @Autowired
    CourseService courseService;

    public void validateModule(ModuleDto moduleDto) throws Exception {
        try {

            if (moduleDto.getModuleTitle() == null || moduleDto.getModuleTitle().isEmpty()) {
                throw new IllegalArgumentException("Module title cannot be null or empty");
            }
            moduleDto.setModuleTitle(moduleDto.getModuleTitle().trim());

            if (moduleDto.getModuleDescription() != null) {
                if (moduleDto.getModuleDescription().isEmpty() || moduleDto.getModuleDescription().trim().isEmpty()) {
                    throw new IllegalArgumentException("Module Description cannot be empty");
                }
                moduleDto.setModuleDescription(moduleDto.getModuleDescription().trim());
            }

            if (moduleDto.getModuleDuration() != null) {
                if (moduleDto.getModuleDuration().isEmpty()) {
                    throw new IllegalArgumentException("Module Duration cannot be null or empty");
                }
                moduleDto.setModuleDuration(moduleDto.getModuleDescription().trim());
            }

            if (moduleDto.getCourseId() == null || moduleDto.getCourseId() <= 0) {
                throw new IllegalArgumentException(("Course Id cannot be null or <= 0"));
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
    public Module saveModule(ModuleDto moduleDto) throws Exception {
        try {

            Module module = new Module();
            Course course = courseService.getCourseById(moduleDto.getCourseId());

            Date currentDate = new Date();

            module.setCreatedDate(currentDate);
            module.setUpdatedDate(currentDate);
            module.setModuleTitle(moduleDto.getModuleTitle());
            module.setModuleDescription(moduleDto.getModuleDescription());
            module.setModuleDuration(moduleDto.getModuleDuration());
            module.setCourse(course);

            return entityManager.merge(module);

        } catch (PersistenceException persistenceException) {
            exceptionHandlingService.handleException(persistenceException);
            throw new PersistenceException(persistenceException.getMessage());
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            throw new Exception(exception.getMessage());
        }
    }

    public Module getModuleById(Long moduleId) throws Exception {
        try {

            TypedQuery<Module> query = entityManager.createQuery(Constant.GET_MODULE_BY_ID, Module.class);
            query.setParameter("moduleId", moduleId);
            return query.getResultList().get(0);

        } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
            exceptionHandlingService.handleException(indexOutOfBoundsException);
            throw new IndexOutOfBoundsException(indexOutOfBoundsException.getMessage());
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            throw new Exception(exception);
        }
    }

}
