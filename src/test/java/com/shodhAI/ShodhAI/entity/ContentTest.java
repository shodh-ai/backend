package com.shodhAI.ShodhAI.entity;

import com.shodhAI.ShodhAI.Entity.Content;
import com.shodhAI.ShodhAI.Entity.ContentType;
import com.shodhAI.ShodhAI.Entity.FileType;
import com.shodhAI.ShodhAI.Entity.Topic;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ContentTest {
    private Long contentId;
    private String url;
    private String jsCode;
    private String jsonData;
    private Date createdDate;
    private Date updatedDate;
    private FileType fileType;
    private Topic topic;
    private ContentType contentType;

    @BeforeEach
    void setUp() {
        contentId = 1L;
        url = "contentName";
        jsCode = "programName";
        jsonData = "json data";
        fileType = new FileType();
        createdDate= new Date();
        updatedDate= new Date();
        topic= new Topic();
        contentType= new ContentType();
    }

    @Test
    @DisplayName("testContentConstructor")
    void testContentConstructor(){
        Content contentByConstructor =  Content.builder().contentId(contentId).url(url).jsCode(jsCode).jsonData(jsonData).createdDate(createdDate).updatedDate(updatedDate).fileType(fileType).topic(topic).contentType(contentType).build();

        assertEquals(contentId, contentByConstructor.getContentId());
        assertEquals(url, contentByConstructor.getUrl());
        assertEquals(jsCode, contentByConstructor.getJsCode());
        assertEquals(jsonData, contentByConstructor.getJsonData());
        assertEquals(createdDate, contentByConstructor.getCreatedDate());
        assertEquals(updatedDate, contentByConstructor.getUpdatedDate());
        assertEquals(fileType, contentByConstructor.getFileType());
        assertEquals(topic, contentByConstructor.getTopic());
        assertEquals(contentType, contentByConstructor.getContentType());
    }

    @Test
    @DisplayName("testContentSettersAndGetters")
    void testContentSettersAndGetters(){
        Content content = getContent();
        assertEquals(contentId, content.getContentId());
        assertEquals(url, content.getUrl());
        assertEquals(jsCode, content.getJsCode());
        assertEquals(jsonData, content.getJsonData());
        assertEquals(topic, content.getTopic());
        assertEquals(contentType, content.getContentType());
        assertEquals(fileType, content.getFileType());
        assertEquals(createdDate, content.getCreatedDate());
        assertEquals(updatedDate, content.getUpdatedDate());
    }

    private @NotNull Content getContent() {
        Content content = new Content();
        content.setContentId(contentId);
        content.setUrl(url);
        content.setJsCode(jsCode);
        content.setJsonData(jsonData);
        content.setCreatedDate(createdDate);
        content.setUpdatedDate(updatedDate);
        content.setTopic(topic);
        content.setContentType(contentType);
        content.setFileType(fileType);
        return content;
    }
}

