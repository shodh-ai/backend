package com.shodhAI.ShodhAI.Service;

import com.shodhAI.ShodhAI.Dto.TimeSpentDto;
import com.shodhAI.ShodhAI.Entity.TimeSpent;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class TimeSpentService {

    @Autowired
    EntityManager entityManager;

    @Autowired
    ExceptionHandlingService exceptionHandlingService;

    @Transactional
    public TimeSpent saveTimeSpent(TimeSpentDto timeSpentDto) throws Exception {
        try {

            Date currentDate = new Date();
            TimeSpent timeSpent = new TimeSpent();
            timeSpent.setCreatedDate(currentDate);
            timeSpent.setUpdatedDate(currentDate);

            return entityManager.merge(timeSpent);

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
