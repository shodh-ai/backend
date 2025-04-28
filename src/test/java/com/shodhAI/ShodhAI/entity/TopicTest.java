package com.shodhAI.ShodhAI.entity;

import com.shodhAI.ShodhAI.Entity.Course;
import com.shodhAI.ShodhAI.Entity.Topic;
import com.shodhAI.ShodhAI.Entity.TopicType;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class TopicTest {
    private Long topicId;
    private String topicTitle;
    private String topicDescription;
    private String topicDuration;
    private String jsxCode;
    private String jsonCode;
    private String narration;
    private Character archived;
    private Date createdDate;
    private Date updatedDate;
    private Course course;
    private TopicType topicType;


    @BeforeEach
    void setUp() {
        topicId = 1L;
        topicTitle = "topicName";
        topicDescription = "topicDescription";
        topicDuration = "topicDuration";
        archived = 'N';
        course= new Course();
        createdDate= new Date();
        updatedDate= new Date();
    }

    @Test
    @DisplayName("testTopicConstructor")
    void testTopicConstructor(){
        Topic topicByConstructor =  Topic.builder().topicId(topicId).topicTitle(topicTitle).topicDescription(topicDescription).archived(archived).topicDuration(topicDuration).createdDate(createdDate).updatedDate(updatedDate).course(course).jsxCode(jsxCode).jsonCode(jsonCode).narration(narration).topicType(topicType).build();

        assertEquals(topicId, topicByConstructor.getTopicId());
        assertEquals(topicTitle, topicByConstructor.getTopicTitle());
        assertEquals(topicDescription, topicByConstructor.getTopicDescription());
        assertEquals(topicDuration, topicByConstructor.getTopicDuration());
        assertEquals(archived, topicByConstructor.getArchived());
        assertEquals(createdDate, topicByConstructor.getCreatedDate());
        assertEquals(updatedDate, topicByConstructor.getUpdatedDate());
        assertEquals(course, topicByConstructor.getCourse());
        assertEquals(topicType, topicByConstructor.getTopicType());
        assertEquals(jsxCode, topicByConstructor.getJsxCode());
        assertEquals(jsonCode, topicByConstructor.getJsonCode());
        assertEquals(narration, topicByConstructor.getNarration());
    }

    @Test
    @DisplayName("testTopicSettersAndGetters")
    void testTopicSettersAndGetters(){
        Topic topic = getTopic();
        assertEquals(topicId, topic.getTopicId());
        assertEquals(topicTitle, topic.getTopicTitle());
        assertEquals(topicDescription, topic.getTopicDescription());
        assertEquals(topicDuration, topic.getTopicDuration());
        assertEquals(archived, topic.getArchived());
        assertEquals(course, topic.getCourse());
        assertEquals(jsonCode, topic.getJsonCode());
        assertEquals(jsxCode, topic.getJsxCode());
        assertEquals(narration, topic.getNarration());
        assertEquals(topicType, topic.getTopicType());
        assertEquals(createdDate, topic.getCreatedDate());
        assertEquals(updatedDate, topic.getUpdatedDate());
    }

    private @NotNull Topic getTopic() {
        Topic topic = new Topic();
        topic.setTopicId(topicId);
        topic.setTopicTitle(topicTitle);
        topic.setTopicDescription(topicDescription);
        topic.setTopicDuration(topicDuration);
        topic.setJsonCode(jsonCode);
        topic.setJsxCode(jsxCode);
        topic.setNarration(narration);
        topic.setTopicType(topicType);
        topic.setArchived(archived);
        topic.setCourse(course);
        topic.setCreatedDate(createdDate);
        topic.setUpdatedDate(updatedDate);
        return topic;
    }
}


