package com.shodhAI.ShodhAI.Service;

import com.shodhAI.ShodhAI.Component.Constant;
import com.shodhAI.ShodhAI.Entity.PriorityLevel;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PriorityLevelService {

    @Autowired
    EntityManager entityManager;

    @Autowired
    ExceptionHandlingService exceptionHandlingService;

    @Transactional(readOnly = true)
    public List<PriorityLevel> getAllPriorityLevels() throws Exception {
        try {

            TypedQuery<PriorityLevel> query = entityManager.createQuery(Constant.GET_ALL_PRIORITY_LEVEL, PriorityLevel.class);
            return query.getResultList();

        } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
            exceptionHandlingService.handleException(indexOutOfBoundsException);
            throw new IndexOutOfBoundsException(indexOutOfBoundsException.getMessage());
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            throw new Exception(exception);
        }
    }

    @Transactional(readOnly = true)
    public PriorityLevel getPriorityLevelById(Long priorityLevelId) throws Exception {
        try {

            TypedQuery<PriorityLevel> query = entityManager.createQuery(Constant.GET_PRIORITY_LEVEL_BY_ID, PriorityLevel.class);
            query.setParameter("priorityLevelId", priorityLevelId);
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
    public PriorityLevel deletePriorityLevelById(Long priorityLevelId) throws Exception {
        try {
            PriorityLevel priorityLevelToDelete = entityManager.find(PriorityLevel.class, priorityLevelId);
            if (priorityLevelToDelete == null)
            {
                throw new IllegalArgumentException("Priority Level with id " + priorityLevelId + " not found");
            }
            priorityLevelToDelete.setArchived('Y');
            entityManager.merge(priorityLevelToDelete);
            return priorityLevelToDelete;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
}
