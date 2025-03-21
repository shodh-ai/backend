package com.shodhAI.ShodhAI.Service;

import com.shodhAI.ShodhAI.Dto.MemoryDto;
import com.shodhAI.ShodhAI.Entity.Memory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class MemoryService {

    @Autowired
    EntityManager entityManager;

    @Autowired
    ExceptionHandlingService exceptionHandlingService;

    @Transactional
    public Memory saveMemory(MemoryDto memoryDto) throws Exception {
        try {

            Date currentDate = new Date();
            Memory memory = new Memory();
            memory.setCreatedDate(currentDate);
            memory.setUpdatedDate(currentDate);

            return entityManager.merge(memory);

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
