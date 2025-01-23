package com.shodhAI.ShodhAI.Service;

import com.shodhAI.ShodhAI.Dto.AccuracyDto;
import com.shodhAI.ShodhAI.Dto.CriticalThinkingDto;
import com.shodhAI.ShodhAI.Entity.Accuracy;
import com.shodhAI.ShodhAI.Entity.CriticalThinking;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class CriticalThinkingService {

    @Autowired
    EntityManager entityManager;

    @Autowired
    ExceptionHandlingService exceptionHandlingService;

    @Transactional
    public CriticalThinking saveCriticalThinking(CriticalThinkingDto criticalThinkingDto) throws Exception {
        try {

            Date currentDate = new Date();
            CriticalThinking criticalThinking = new CriticalThinking();

            criticalThinking.setCreatedDate(currentDate);
            criticalThinking.setUpdatedDate(currentDate);

            return entityManager.merge(criticalThinking);

        } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
            exceptionHandlingService.handleException(indexOutOfBoundsException);
            throw new IndexOutOfBoundsException(indexOutOfBoundsException.getMessage());
        } catch (PersistenceException persistenceException) {
            exceptionHandlingService.handleException(persistenceException);
            throw new PersistenceException(persistenceException.getMessage());
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            throw new Exception(exception.getMessage());
        }
    }

}
