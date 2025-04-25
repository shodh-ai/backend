package com.shodhAI.ShodhAI.Service;

import com.shodhAI.ShodhAI.Component.Constant;
import com.shodhAI.ShodhAI.Entity.NotificationType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class NotificationTypeService
{
    @Autowired
    EntityManager entityManager;

    @Autowired
    ExceptionHandlingService exceptionHandlingService;

    @Transactional(readOnly = true)
    public NotificationType getNotificationTypeById(Long notificationTypeId) throws Exception {
        try {

            TypedQuery<NotificationType> query = entityManager.createQuery(Constant.GET_NOTIFICATION_TYPE_BY_ID, NotificationType.class);
            query.setParameter("notificationTypeId", notificationTypeId);
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
    public NotificationType addNotificationType(NotificationType notificationType) throws Exception {
        try
        {
            if(notificationType.getTypeName()==null|| notificationType.getTypeName().trim().isEmpty())
            {
                throw new IllegalArgumentException("Notification type name cannot be null or empty");
            }
            List<NotificationType> notificationTypes= getAllNotificationType();
            for(NotificationType notificationTypeToGet : notificationTypes)
            {
                if (notificationTypeToGet.getTypeName().equalsIgnoreCase(notificationType.getTypeName().trim()))
                {
                    throw new IllegalArgumentException("Notification type already exists with name " + notificationType.getTypeName().trim());
                }
            }
            if(notificationType.getDescription()!=null && notificationType.getDescription().trim().isEmpty())
            {
                throw new IllegalArgumentException("Notification type description cannot be empty");
            }
            NotificationType notificationTypeToAdd= new NotificationType();
            notificationTypeToAdd.setTypeName(notificationType.getTypeName().trim());
            entityManager.persist(notificationTypeToAdd);
            return notificationTypeToAdd;
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
    public NotificationType updateNotificationType(Long notificationTypeId,NotificationType notificationType) throws Exception {
        try
        {
            NotificationType notificationTypeToUpdate= entityManager.find(NotificationType.class,notificationTypeId);
            if(notificationTypeToUpdate==null)
            {
                throw new IllegalArgumentException("Notification type with id "+ notificationTypeId+ " not found");
            }
            if(notificationType.getTypeName()!=null)
            {
                if(notificationType.getTypeName().trim().isEmpty())
                {
                    throw new IllegalArgumentException("Notification type name cannot be null or empty");
                }
                List<NotificationType> notificationTypes= getAllNotificationType();
                for(NotificationType notificationTypeToGet : notificationTypes)
                {
                    if (!Objects.equals(notificationTypeToGet.getId(), notificationTypeId) && notificationTypeToGet.getTypeName().equalsIgnoreCase(notificationType.getTypeName().trim()))
                    {
                        throw new IllegalArgumentException("Notification type already exists with name " + notificationType.getTypeName().trim());
                    }
                }
                notificationTypeToUpdate.setTypeName(notificationType.getTypeName().trim());
            }
            if(notificationType.getDescription()!=null)
            {
                if(notificationType.getDescription().trim().isEmpty())
                {
                    throw new IllegalArgumentException("Notification type description cannot be empty");
                }
                notificationTypeToUpdate.setDescription(notificationType.getDescription());
            }
            entityManager.merge(notificationTypeToUpdate);
            return notificationTypeToUpdate;
        }
        catch (IllegalArgumentException illegalArgumentException)
        {
            throw new IllegalArgumentException(illegalArgumentException.getMessage());
        }
        catch (Exception exception)
        {
            throw new Exception(exception.getMessage());
        }
    }

    @Transactional
    public NotificationType deleteNotificationTypeById(Long notificationTypeId) throws Exception {
        try {
            NotificationType notificationTypeToDelete = entityManager.find(NotificationType.class, notificationTypeId);
            if (notificationTypeToDelete == null)
            {
                throw new IllegalArgumentException("Notification type with id " + notificationTypeId + " not found");
            }
            notificationTypeToDelete.setArchived('Y');
            entityManager.merge(notificationTypeToDelete);
            return notificationTypeToDelete;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public List<NotificationType> notificationTypeFilter() throws Exception {
        try {
            String jpql = "SELECT f FROM NotificationType f WHERE f.archived = 'N' ORDER BY f.id ASC";
            TypedQuery<NotificationType> query = entityManager.createQuery(jpql, NotificationType.class);
            return query.getResultList();
        } catch (PersistenceException persistenceException) {
            exceptionHandlingService.handleException(persistenceException);
            throw new PersistenceException(persistenceException.getMessage());
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            throw new Exception(exception.getMessage());
        }
    }
    public List<NotificationType> getAllNotificationType() throws Exception {
        try {

            TypedQuery<NotificationType> query = entityManager.createQuery(Constant.GET_ALL_NOTIFICATION_TYPE, NotificationType.class);
            return query.getResultList();

        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            throw new Exception(exception.getMessage());
        }
    }

}
