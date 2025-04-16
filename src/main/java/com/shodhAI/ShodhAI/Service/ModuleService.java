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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Transactional
    public List<Module> moduleFilter(Long moduleId, Long userId, Long roleId, Long courseId, Long academicDegreeId) throws Exception {
        try {
            StringBuilder jpql = new StringBuilder("SELECT DISTINCT m FROM Module m WHERE 1=1 ");
            Map<String, Object> params = new HashMap<>();

            if (moduleId != null) {
                jpql.append("AND m.moduleId = :moduleId ");
                params.put("moduleId", moduleId);
            }

            if (courseId != null) {
                jpql.append("AND m.course.archived='N' AND m.course.courseId = :courseId ");
                params.put("courseId", courseId);
            }

            if (academicDegreeId != null) {
                jpql.append("AND m.course.academicDegree.archived='N' AND m.course.academicDegree.degreeId = :academicDegreeId ");
                params.put("academicDegreeId", academicDegreeId);
            }

            jpql.append("AND m.archived = 'N'");

            TypedQuery<Module> query = entityManager.createQuery(jpql.toString(), Module.class);

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
    public Module deleteModuleById(Long moduleId) throws Exception {
        try {
            Module moduleToDelete = entityManager.find(Module.class, moduleId);
            if (moduleToDelete == null)
            {
                throw new IllegalArgumentException("Module with id " + moduleId + " not found");
            }
            moduleToDelete.setArchived('Y');
            entityManager.merge(moduleToDelete);
            return moduleToDelete;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public void validateAndSaveModuleForUpdate(ModuleDto moduleDto, Module moduleToUpdate) throws Exception {
        try {
            if(moduleDto.getModuleTitle()!=null)
            {
                if (moduleDto.getModuleTitle().isEmpty()) {
                    throw new IllegalArgumentException("Module title cannot be null or empty");
                }
                moduleDto.setModuleTitle(moduleDto.getModuleTitle().trim());
                moduleToUpdate.setModuleTitle(moduleDto.getModuleTitle());
            }

            if (moduleDto.getModuleDescription() != null) {
                if (moduleDto.getModuleDescription().isEmpty() || moduleDto.getModuleDescription().trim().isEmpty()) {
                    throw new IllegalArgumentException("Module Description cannot be empty");
                }
                moduleDto.setModuleDescription(moduleDto.getModuleDescription().trim());
                moduleToUpdate.setModuleDescription(moduleDto.getModuleDescription());
            }

            if (moduleDto.getModuleDuration() != null) {
                if (moduleDto.getModuleDuration().isEmpty()) {
                    throw new IllegalArgumentException("Module Duration cannot be null or empty");
                }
                moduleDto.setModuleDuration(moduleDto.getModuleDuration().trim());
                moduleToUpdate.setModuleDuration(moduleDto.getModuleDuration());
            }

            if (moduleDto.getCourseId() != null) {
                Course course = entityManager.find(Course.class, moduleDto.getCourseId());
                if (course == null) {
                    throw new IllegalArgumentException("Course with id " + moduleDto.getCourseId() + " not found");
                }
                moduleToUpdate.setCourse(course);
            }

            moduleToUpdate.setUpdatedDate(new Date());

        } catch (IllegalArgumentException illegalArgumentException) {
            exceptionHandlingService.handleException(illegalArgumentException);
            throw new IllegalArgumentException(illegalArgumentException.getMessage());
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            throw new Exception(exception.getMessage());
        }
    }

    @Transactional
    public Module updateModule(Long moduleId, ModuleDto moduleDto) throws Exception {
        Module moduleToUpdate = entityManager.find(Module.class,moduleId);
        if(moduleToUpdate ==null)
        {
            throw new IllegalArgumentException("Module with id " + moduleId+ " does not found");
        }
        validateAndSaveModuleForUpdate(moduleDto,moduleToUpdate);
        return entityManager.merge(moduleToUpdate);
    }

}
