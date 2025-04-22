package com.shodhAI.ShodhAI.entity;

import com.shodhAI.ShodhAI.Entity.TopicType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TopicTypeTest {
    private Long topicTypeId;
    private String topicTypeName;
    private Character archived;
    private Date createdDate;
    private Date updatedDate;

    @BeforeEach
    void setUp() {
        topicTypeId = 1L;
        topicTypeName = "topicTypeName";
        archived = 'N';
        createdDate= new Date();
        updatedDate= new Date();
    }

    @Test
    @DisplayName("testTopicTypeConstructor")
    void testTopicTypeConstructor(){
        TopicType topicTypeByConstructor =  TopicType.builder().topicTypeId(topicTypeId).topicTypeName(topicTypeName).archived(archived).createdDate(createdDate).updatedDate(updatedDate).build();

        assertEquals(topicTypeId, topicTypeByConstructor.getTopicTypeId());
        assertEquals(topicTypeName, topicTypeByConstructor.getTopicTypeName());
        assertEquals(archived, topicTypeByConstructor.getArchived());
        assertEquals(createdDate, topicTypeByConstructor.getCreatedDate());
        assertEquals(updatedDate, topicTypeByConstructor.getUpdatedDate());
    }

    @Test
    @DisplayName("testTopicTypeSettersAndGetters")
    void testTopicTypeSettersAndGetters(){
        TopicType topicType = new TopicType();
        topicType.setTopicTypeId(topicTypeId);
        topicType.setTopicTypeName(topicTypeName);
        topicType.setArchived(archived);
        topicType.setCreatedDate(createdDate);
        topicType.setUpdatedDate(updatedDate);
        assertEquals(topicTypeId, topicType.getTopicTypeId());
        assertEquals(topicTypeName, topicType.getTopicTypeName());
        assertEquals(archived, topicType.getArchived());
        assertEquals(createdDate, topicType.getCreatedDate());
        assertEquals(updatedDate, topicType.getUpdatedDate());
    }
}






