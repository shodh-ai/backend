package com.shodhAI.ShodhAI.Service;

import com.shodhAI.ShodhAI.Component.Constant;
import com.shodhAI.ShodhAI.Entity.ContentType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class ContentTypeService
{
    @Autowired
    EntityManager entityManager;

    @Autowired
    ExceptionHandlingService exceptionHandlingService;

    public List<ContentType> getAllContentType() throws Exception {
        try {

            TypedQuery<ContentType> query = entityManager.createQuery(Constant.GET_ALL_CONTENT_TYPE, ContentType.class);
            return query.getResultList();

        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            throw new Exception(exception.getMessage());
        }
    }
    public ContentType getContentTypeById(Long contentTypeId) throws Exception {
        try {

            TypedQuery<ContentType> query = entityManager.createQuery(Constant.GET_CONTENT_TYPE_BY_ID, ContentType.class);
            query.setParameter("contentTypeId", contentTypeId);
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
    public ContentType addContentType(ContentType contentType) throws Exception {
        try
        {
            if(contentType.getContentTypeName()==null|| contentType.getContentTypeName().trim().isEmpty())
            {
                throw new IllegalArgumentException("Content type name cannot be null or empty");
            }
            List<ContentType> contentTypes= getAllContentType();
            for(ContentType contentTypeToGet : contentTypes)
            {
                if (contentTypeToGet.getContentTypeName().equalsIgnoreCase(contentType.getContentTypeName().trim()))
                {
                    throw new IllegalArgumentException("Content type already exists with name " + contentType.getContentTypeName().trim());
                }
            }
            ContentType contentTypeToAdd= new ContentType();
            contentTypeToAdd.setContentTypeName(contentType.getContentTypeName().trim());
            contentTypeToAdd.setCreatedDate(new Date());
            entityManager.persist(contentTypeToAdd);
            return contentTypeToAdd;
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
    public ContentType deleteContentTypeById(Long contentTypeId) throws Exception {
        try {
            ContentType contentTypeToDelete = entityManager.find(ContentType.class, contentTypeId);
            if (contentTypeToDelete == null)
            {
                throw new IllegalArgumentException("Content type with id " + contentTypeId + " not found");
            }
            contentTypeToDelete.setArchived('Y');
            entityManager.merge(contentTypeToDelete);
            return contentTypeToDelete;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @Transactional
    public List<ContentType> contentTypeFilter() throws Exception {
        try {
            String jpql = "SELECT f FROM ContentType f WHERE f.archived = 'N' ORDER BY f.contentTypeId ASC";
            TypedQuery<ContentType> query = entityManager.createQuery(jpql, ContentType.class);
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
