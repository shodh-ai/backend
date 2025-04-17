package com.shodhAI.ShodhAI.Service;

import com.shodhAI.ShodhAI.Component.Constant;
import com.shodhAI.ShodhAI.Entity.FileType;
import com.shodhAI.ShodhAI.Entity.FileType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Objects;

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

    @Transactional
    public FileType addFileType(FileType fileType) throws Exception {
        try
        {
            if(fileType.getFileTypeName()==null|| fileType.getFileTypeName().trim().isEmpty())
            {
                throw new IllegalArgumentException("File type name cannot be null or empty");
            }
            List<FileType> fileTypes= getAllFileType();
            for(FileType fileTypeToGet : fileTypes)
            {
                if (fileTypeToGet.getFileTypeName().equalsIgnoreCase(fileType.getFileTypeName().trim()))
                {
                    throw new IllegalArgumentException("File type already exists with name " + fileType.getFileTypeName().trim());
                }
            }
            FileType fileTypeToAdd= new FileType();
            fileTypeToAdd.setFileTypeName(fileType.getFileTypeName().trim());
            entityManager.persist(fileTypeToAdd);
            return fileTypeToAdd;
        }
        catch (IllegalArgumentException illegalArgumentException)
        {
            exceptionHandlingService.handleException(illegalArgumentException);
            throw new IllegalArgumentException(illegalArgumentException.getMessage());
        }
        catch(Exception exception)
        {
            exceptionHandlingService.handleException(exception);
            throw new Exception(exception.getMessage());
        }
    }

    @Transactional
    public FileType deleteFileTypeById(Long fileTypeId) throws Exception {
        try {
            FileType fileTypeToDelete = entityManager.find(FileType.class, fileTypeId);
            if (fileTypeToDelete == null)
            {
                throw new IllegalArgumentException("File type with id " + fileTypeId + " not found");
            }
            fileTypeToDelete.setArchived('Y');
            entityManager.merge(fileTypeToDelete);
            return fileTypeToDelete;
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

    @Transactional
    public FileType updateFileType(Long fileTypeId, FileType fileType) throws Exception {
        try
        {
            FileType fileTypeToUpdate= entityManager.find(FileType.class,fileTypeId);
            if(fileTypeToUpdate==null)
            {
                throw new IllegalArgumentException("File type with id "+ fileTypeId+ " not found");
            }
            if(fileType.getFileTypeName()!=null)
            {
                if(fileType.getFileTypeName().trim().isEmpty())
                {
                    throw new IllegalArgumentException("File type name cannot be null or empty");
                }
                List<FileType> fileTypes= getAllFileType();
                for(FileType fileTypeToGet : fileTypes)
                {
                    if (!Objects.equals(fileTypeToGet.getFileTypeId(), fileTypeId) && fileTypeToGet.getFileTypeName().equalsIgnoreCase(fileType.getFileTypeName().trim()))
                    {
                        throw new DataIntegrityViolationException("File type already exists with name " + fileType.getFileTypeName().trim());
                    }
                }
                fileTypeToUpdate.setFileTypeName(fileType.getFileTypeName().trim());
            }
            entityManager.merge(fileTypeToUpdate);
            return fileTypeToUpdate;
        }
        catch (DataIntegrityViolationException e)
        {
            exceptionHandlingService.handleException(e);
            throw new DataIntegrityViolationException(e.getMessage());
        }
        catch (IllegalArgumentException illegalArgumentException)
        {
            exceptionHandlingService.handleException(illegalArgumentException);
            throw new IllegalArgumentException(illegalArgumentException.getMessage());
        }
        catch (Exception exception)
        {
            exceptionHandlingService.handleException(exception);
            throw new Exception(exception.getMessage());
        }
    }

}
