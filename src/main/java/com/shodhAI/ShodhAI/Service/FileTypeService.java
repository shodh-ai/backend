package com.shodhAI.ShodhAI.Service;

import com.shodhAI.ShodhAI.Component.Constant;
import com.shodhAI.ShodhAI.Entity.FileType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FileTypeService {

    @Autowired
    EntityManager entityManager;

    @Autowired
    ExceptionHandlingService exceptionHandlingService;

    public List<FileType> getAllFileType() throws Exception {
        try {

            TypedQuery<FileType> query = entityManager.createQuery(Constant.GET_ALL_FILE_TYPES, FileType.class);
            return query.getResultList();

        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            throw new Exception(exception.getMessage());
        }
    }

    public FileType getFileTypeById(Long fileTypeId) throws Exception {
        try {

            TypedQuery<FileType> query = entityManager.createQuery(Constant.GET_FILE_TYPE_BY_ID, FileType.class);
            query.setParameter("fileTypeId", fileTypeId);
            return query.getResultList().get(0);

        } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
            exceptionHandlingService.handleException(indexOutOfBoundsException);
            throw new IndexOutOfBoundsException(indexOutOfBoundsException.getMessage());
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            throw new Exception(exception);
        }
    }

    public FileType getFileTypeByType(String fileType) throws Exception {
        try {

            TypedQuery<FileType> query = entityManager.createQuery(Constant.GET_FILE_TYPE_BY_TYPE, FileType.class);
            query.setParameter("fileType", fileType);
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
