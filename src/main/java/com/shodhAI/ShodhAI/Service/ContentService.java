package com.shodhAI.ShodhAI.Service;

import com.shodhAI.ShodhAI.Component.Constant;
import com.shodhAI.ShodhAI.Entity.Content;
import com.shodhAI.ShodhAI.Entity.Course;
import com.shodhAI.ShodhAI.Entity.FileType;
import com.shodhAI.ShodhAI.Entity.Student;
import com.shodhAI.ShodhAI.Entity.Topic;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class ContentService {

    @Autowired
    EntityManager entityManager;

    @Autowired
    ExceptionHandlingService exceptionHandlingService;

    @Autowired
    FileTypeService fileTypeService;

    @Autowired
    TopicService topicService;

    public void validateContent(Long topicId) throws Exception {
        try {

            if (topicId == null || topicId <= 0) {
                throw new IllegalArgumentException(("Topic Id cannot be null or <= 0"));
            }

        } catch (IllegalArgumentException illegalArgumentException) {
            exceptionHandlingService.handleException(illegalArgumentException);
            throw new IllegalArgumentException(illegalArgumentException.getMessage());
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            throw new Exception(exception.getMessage());
        }
    }

    @Transactional
    public Content saveContent(Long topicId, Map<String, Object> uploadResult) throws Exception {
        try {

            Content content = new Content();

            Date currentDate = new Date();

            Topic topic = topicService.getTopicById(topicId);
            String format = (String) uploadResult.get("format");
            FileType fileType = fileTypeService.getFileTypeByType(format);

            if (fileType.getArchived() == 'Y') {
                throw new IllegalArgumentException("File Type not supported");
            }
            content.setCreatedDate(currentDate);
            content.setUpdatedDate(currentDate);
            content.setFileType(fileType);
            content.setTopic(topic);
            content.setUrl(uploadResult.get("url").toString());

            return entityManager.merge(content);

        } catch (PersistenceException persistenceException) {
            exceptionHandlingService.handleException(persistenceException);
            throw new PersistenceException(persistenceException.getMessage());
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            throw new Exception(exception.getMessage());
        }
    }

    @Transactional
    public List<Content> getContentByTopic(Topic topic) throws Exception {
        try {

            TypedQuery<Content> query = entityManager.createQuery(Constant.GET_CONTENT_BY_TOPIC, Content.class);
            query.setParameter("topic", topic);
            return query.getResultList();

        } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
            exceptionHandlingService.handleException(indexOutOfBoundsException);
            throw new IndexOutOfBoundsException("Content not found with given Id");
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            throw new Exception(exception);
        }
    }

    public Content getContentById(Long contentId) throws Exception {
        try {

            TypedQuery<Content> query = entityManager.createQuery(Constant.GET_CONTENT_BY_ID, Content.class);
            query.setParameter("contentId", contentId);
            return query.getResultList().get(0);

        } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
            exceptionHandlingService.handleException(indexOutOfBoundsException);
            throw new IndexOutOfBoundsException("Content not found with given Id");
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            throw new Exception(exception);
        }
    }

}
