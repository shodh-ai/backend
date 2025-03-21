package com.shodhAI.ShodhAI.Service;

import com.shodhAI.ShodhAI.Dto.UnderstandingDto;
import com.shodhAI.ShodhAI.Entity.Understanding;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class UnderstandingService {

    @Autowired
    EntityManager entityManager;

    @Autowired
    ExceptionHandlingService exceptionHandlingService;

    @Transactional
    public Understanding saveUnderstanding(UnderstandingDto understandingDto) throws Exception {
        try {

            Date currentDate = new Date();
            Understanding understanding = new Understanding();
            understanding.setCreatedDate(currentDate);
            understanding.setUpdatedDate(currentDate);

            return entityManager.merge(understanding);

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
