package com.shodhAI.ShodhAI.Service;

import com.shodhAI.ShodhAI.Component.Constant;
import com.shodhAI.ShodhAI.Dto.DoubtDto;
import com.shodhAI.ShodhAI.Entity.Doubt;
import com.shodhAI.ShodhAI.Entity.DoubtLevel;
import com.shodhAI.ShodhAI.Entity.FileType;
import com.shodhAI.ShodhAI.Entity.Role;
import com.shodhAI.ShodhAI.Entity.Topic;
import com.shodhAI.ShodhAI.Entity.UserDoubt;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class DoubtService {

    @Autowired
    EntityManager entityManager;

    @Autowired
    TopicService topicService;

    @Autowired
    RoleService roleService;

    @Autowired
    ExceptionHandlingService exceptionHandlingService;

    public void validateDoubt(DoubtDto doubtDto) throws Exception {
        try {

            if (doubtDto.getDoubt() == null || doubtDto.getDoubt().isEmpty()) {
                throw new IllegalArgumentException("Doubt cannot be null or empty");
            }
            doubtDto.setDoubt(doubtDto.getDoubt().trim());

        } catch (IllegalArgumentException illegalArgumentException) {
            exceptionHandlingService.handleException(illegalArgumentException);
            throw new IllegalArgumentException(illegalArgumentException.getMessage());
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            throw new Exception(exception.getMessage());
        }

    }

    @Transactional
    public Doubt saveDoubt(DoubtDto doubtDto) throws Exception {
        try {

            Doubt doubt = new Doubt();
            Date currentDate = new Date();

            Topic topic = topicService.getTopicById(doubtDto.getTopicId());

            doubt.setCreatedDate(currentDate);
            doubt.setUpdatedDate(currentDate);
            doubt.setTopic(topic);
            doubt.setDoubt(doubt.getDoubt());

            return entityManager.merge(doubt);

        } catch (PersistenceException persistenceException) {
            exceptionHandlingService.handleException(persistenceException);
            throw new PersistenceException(persistenceException.getMessage());
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            throw new Exception(exception.getMessage());
        }
    }

    @Transactional
    public UserDoubt saveStudentDoubtLinkage(Long userId, Long roleId, Doubt doubt) throws Exception {
        try {

            Role role = roleService.getRoleById(roleId);

            UserDoubt userDoubt = new UserDoubt();
            userDoubt.setUserId(userId);
            userDoubt.setRole(role);
            userDoubt.setDoubt(doubt);

            Date currentDate = new Date();
            userDoubt.setAskedAt(currentDate);
            return entityManager.merge(userDoubt);

        } catch (PersistenceException persistenceException) {
            exceptionHandlingService.handleException(persistenceException);
            throw new PersistenceException(persistenceException.getMessage());
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            throw new Exception(exception.getMessage());
        }
    }

    public List<DoubtLevel> getAllDoubtLevels() throws Exception {
        try {

            TypedQuery<DoubtLevel> query = entityManager.createQuery(Constant.GET_ALL_DOUBT_LEVEL, DoubtLevel.class);
            return query.getResultList();

        } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
            exceptionHandlingService.handleException(indexOutOfBoundsException);
            throw new IndexOutOfBoundsException(indexOutOfBoundsException.getMessage());
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            throw new Exception(exception);
        }
    }

    public DoubtLevel getDoubtLevelById(Long doubtLevelId) throws Exception {
        try {

            TypedQuery<DoubtLevel> query = entityManager.createQuery(Constant.GET_DOUBT_LEVEL_BY_ID, DoubtLevel.class);
            query.setParameter("doubtLevelId", doubtLevelId);
            return query.getResultList().get(0);

        } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
            exceptionHandlingService.handleException(indexOutOfBoundsException);
            throw new IndexOutOfBoundsException(indexOutOfBoundsException.getMessage());
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            throw new Exception(exception);
        }
    }

    public DoubtLevel getDoubtLevelByDoubtLevel(String doubtLevel) throws Exception {
        try {

            TypedQuery<DoubtLevel> query = entityManager.createQuery(Constant.GET_DOUBT_LEVEL_BY_DOUBT_LEVEL, DoubtLevel.class);
            query.setParameter("doubtLevel", doubtLevel.toUpperCase());
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
    public DoubtLevel deleteDoubtLevelById(Long doubtLevelId) throws Exception {
        try {
            DoubtLevel doubtLevelToDelete = entityManager.find(DoubtLevel.class, doubtLevelId);
            if (doubtLevelToDelete == null)
            {
                throw new IllegalArgumentException("Doubt level with id " + doubtLevelId + " not found");
            }
            doubtLevelToDelete.setArchived('Y');
            entityManager.merge(doubtLevelToDelete);
            return doubtLevelToDelete;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @Transactional
    public List<FileType> fileTypeFilter() throws Exception {
        try {
            String jpql = "SELECT f FROM FileType f WHERE f.archived = 'N' ORDER BY f.fileTypeId ASC";
            TypedQuery<FileType> query = entityManager.createQuery(jpql, FileType.class);
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
