package com.shodhAI.ShodhAI.Service;

import com.shodhAI.ShodhAI.Component.Constant;
import com.shodhAI.ShodhAI.Entity.Content;
import com.shodhAI.ShodhAI.Entity.ContentType;
import com.shodhAI.ShodhAI.Entity.FileType;
import com.shodhAI.ShodhAI.Entity.Topic;
import com.shodhAI.ShodhAI.Entity.TopicType;
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

    public void validateContent(Long topicId, Long contentTypeId) throws Exception {
        try {

            if (topicId == null || topicId <= 0) {
                throw new IllegalArgumentException(("Topic Id cannot be null or <= 0"));
            }

            if(contentTypeId == null || contentTypeId <=0) {
                throw new IllegalArgumentException(("Content Type Id cannot be null or <= 0"));
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
    public Content saveContent(Long topicId, Map<String, Object> uploadResult, Long contentTypeId, String jsCode, String jsonData) throws Exception {
        try {

            Content content = new Content();

            Date currentDate = new Date();

            Topic topic = topicService.getTopicById(topicId);
            String format = (String) uploadResult.get("format");

            if(contentTypeId != 4) {
                FileType fileType = fileTypeService.getFileTypeByType(format);
                ContentType contentType = getContentTypeById(contentTypeId);

                if (fileType.getArchived() == 'Y') {
                    throw new IllegalArgumentException("File Type not supported");
                }
                if(topic.getTopicType().getTopicTypeName().equals(Constant.GET_TOPIC_TYPE_ASSIGNMENT) && !contentType.getContentTypeName().equals(Constant.GET_CONTENT_TYPE_ASSIGNMENT)) {
                    throw new IllegalArgumentException("For Assignment content type and topic type must be same");
                }

                content.setFileType(fileType);
                content.setContentType(contentType);
            } else {
                content.setJsCode(jsCode);
                content.setJsonData(jsonData);
            }

            content.setCreatedDate(currentDate);
            content.setUpdatedDate(currentDate);
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

    public List<Content> getContentByTopicId(Long topicId) throws Exception {
        try {

            Topic topic = topicService.getTopicById(topicId);

            TypedQuery<Content> query = entityManager.createQuery(Constant.GET_CONTENT_BY_TOPIC_ID, Content.class);
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

    public List<ContentType> getAllContentTypes() throws Exception {
        try {

            TypedQuery<ContentType> query = entityManager.createQuery(Constant.GET_ALL_CONTENT_TYPE, ContentType.class);
            return query.getResultList();

        } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
            exceptionHandlingService.handleException(indexOutOfBoundsException);
            throw new IndexOutOfBoundsException(indexOutOfBoundsException.getMessage());
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            throw new Exception(exception);
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

}
