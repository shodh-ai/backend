package com.shodhAI.ShodhAI.entity;

import com.shodhAI.ShodhAI.Entity.Doubt;
import com.shodhAI.ShodhAI.Entity.FileType;
import com.shodhAI.ShodhAI.Entity.Topic;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DoubtTest {
    private Long doubtId;
    private String doubt;
    private String jsxCode;
    private String narration;
    private Date createdDate;
    private Date updatedDate;
    private FileType fileType;
    private Topic topic;

    @BeforeEach
    void setUp() {
        doubtId = 1L;
        doubt = "doubtName";
        jsxCode = "jsx code";
        narration = "json data";
        createdDate= new Date();
        updatedDate= new Date();
        topic= new Topic();
    }

    @Test
    @DisplayName("testDoubtConstructor")
    void testDoubtConstructor(){
        Doubt doubtByConstructor =  Doubt.builder().id(doubtId).doubt(doubt).jsxCode(jsxCode).narration(narration).createdDate(createdDate).updatedDate(updatedDate).topic(topic).build();

        assertEquals(doubtId, doubtByConstructor.getId());
        assertEquals(doubt, doubtByConstructor.getDoubt());
        assertEquals(jsxCode, doubtByConstructor.getJsxCode());
        assertEquals(narration, doubtByConstructor.getNarration());
        assertEquals(createdDate, doubtByConstructor.getCreatedDate());
        assertEquals(updatedDate, doubtByConstructor.getUpdatedDate());
        assertEquals(topic, doubtByConstructor.getTopic());
    }

    @Test
    @DisplayName("testDoubtSettersAndGetters")
    void testDoubtSettersAndGetters(){
        Doubt doubt = getDoubt();
        assertEquals(doubtId, doubt.getId());
        assertEquals(this.doubt, doubt.getDoubt());
        assertEquals(jsxCode, doubt.getJsxCode());
        assertEquals(narration, doubt.getNarration());
        assertEquals(topic, doubt.getTopic());
        assertEquals(createdDate, doubt.getCreatedDate());
        assertEquals(updatedDate, doubt.getUpdatedDate());
    }

    private @NotNull Doubt getDoubt() {
        Doubt doubt = new Doubt();
        doubt.setId(doubtId);
        doubt.setDoubt(this.doubt);
        doubt.setJsxCode(jsxCode);
        doubt.setNarration(narration);
        doubt.setCreatedDate(createdDate);
        doubt.setUpdatedDate(updatedDate);
        doubt.setTopic(topic);
        return doubt;
    }
}

