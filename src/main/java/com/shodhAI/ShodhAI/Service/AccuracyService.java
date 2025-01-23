package com.shodhAI.ShodhAI.Service;

import com.shodhAI.ShodhAI.Dto.AccuracyDto;
import com.shodhAI.ShodhAI.Entity.Accuracy;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class AccuracyService {

    @Autowired
    EntityManager entityManager;

    @Autowired
    ExceptionHandlingService exceptionHandlingService;

    @Transactional
    public Accuracy saveAccuracy(AccuracyDto accuracyDto) throws Exception {
        try {

            Date currentDate = new Date();
            Accuracy accuracy = new Accuracy();
            accuracy.setCreatedDate(currentDate);
            accuracy.setUpdatedDate(currentDate);

            return entityManager.merge(accuracy);

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
