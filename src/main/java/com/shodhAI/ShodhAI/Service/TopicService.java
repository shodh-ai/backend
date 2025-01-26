package com.shodhAI.ShodhAI.Service;

import com.shodhAI.ShodhAI.Component.Constant;
import com.shodhAI.ShodhAI.Dto.TopicDto;
import com.shodhAI.ShodhAI.Entity.Course;
import com.shodhAI.ShodhAI.Entity.Module;
import com.shodhAI.ShodhAI.Entity.Topic;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class TopicService {

    @Autowired
    EntityManager entityManager;

    @Autowired
    ExceptionHandlingService exceptionHandlingService;

    @Autowired
    CourseService courseService;

    @Autowired
    ModuleService moduleService;

    public void validateTopic(TopicDto topicDto) throws Exception {
        try {

            if (topicDto.getTopicTitle() == null || topicDto.getTopicTitle().isEmpty()) {
                throw new IllegalArgumentException("Topic title cannot be null or empty");
            }
            topicDto.setTopicTitle(topicDto.getTopicTitle().trim());

            if (topicDto.getTopicDescription() != null) {
                if (topicDto.getTopicDescription().isEmpty() || topicDto.getTopicDescription().trim().isEmpty()) {
                    throw new IllegalArgumentException("Topic Description cannot be empty");
                }
                topicDto.setTopicDescription(topicDto.getTopicDescription().trim());
            }

            if (topicDto.getTopicDuration() != null) {
                if (topicDto.getTopicDuration().isEmpty()) {
                    throw new IllegalArgumentException("Topic Duration cannot be null or empty");
                }
                topicDto.setTopicDuration(topicDto.getTopicDuration().trim());
            }

            if(topicDto.getCourseId() == null || topicDto.getCourseId() <= 0) {
                throw new IllegalArgumentException(("Course Id cannot be null or <= 0"));
            }
            if(topicDto.getModuleId() == null || topicDto.getModuleId() <= 0) {
                throw new IllegalArgumentException(("Module Id cannot be null or <= 0"));
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
    public Topic saveTopic(TopicDto topicDto) throws Exception {
        try {

            Topic topic = new Topic();
            Course course = courseService.getCourseById(topicDto.getCourseId());
            Module module = moduleService.getModuleById(topicDto.getModuleId());

            Date currentDate = new Date();

            topic.setCreatedDate(currentDate);
            topic.setUpdatedDate(currentDate);
            topic.setTopicTitle(topicDto.getTopicTitle());
            topic.setTopicDescription(topicDto.getTopicDescription());
            topic.setTopicDuration(topicDto.getTopicDuration());
            topic.setCourse(course);
            topic.setModule(module);

            return entityManager.merge(topic);

        } catch (PersistenceException persistenceException) {
            exceptionHandlingService.handleException(persistenceException);
            throw new PersistenceException(persistenceException.getMessage());
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            throw new Exception(exception.getMessage());
        }
    }

    public Topic getTopicById(Long topicId) throws Exception {
        try {

            TypedQuery<Topic> query = entityManager.createQuery(Constant.GET_TOPIC_BY_ID, Topic.class);
            query.setParameter("topicId", topicId);
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
