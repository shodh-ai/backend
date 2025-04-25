package com.shodhAI.ShodhAI.entity;

import com.shodhAI.ShodhAI.Entity.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ContentTypeTest {
    private Long contentTypeId;
    private String contentTypeName;
    private Character archived;
    private Date createdDate;
    private Date updatedDate;

    @BeforeEach
    void setUp() {
        contentTypeId = 1L;
        contentTypeName = "contentTypeName";
        archived = 'N';
        createdDate= new Date();
        updatedDate= new Date();
    }

    @Test
    @DisplayName("testContentTypeConstructor")
    void testContentTypeConstructor(){
        ContentType contentTypeByConstructor =  ContentType.builder().contentTypeId(contentTypeId).contentTypeName(contentTypeName).archived(archived).createdDate(createdDate).updatedDate(updatedDate).build();

        assertEquals(contentTypeId, contentTypeByConstructor.getContentTypeId());
        assertEquals(contentTypeName, contentTypeByConstructor.getContentTypeName());
        assertEquals(archived, contentTypeByConstructor.getArchived());
        assertEquals(createdDate, contentTypeByConstructor.getCreatedDate());
        assertEquals(updatedDate, contentTypeByConstructor.getUpdatedDate());
    }

    @Test
    @DisplayName("testContentTypeSettersAndGetters")
    void testContentTypeSettersAndGetters(){
        ContentType contentType = new ContentType();
        contentType.setContentTypeId(contentTypeId);
        contentType.setContentTypeName(contentTypeName);
        contentType.setArchived(archived);
        contentType.setCreatedDate(createdDate);
        contentType.setUpdatedDate(updatedDate);
        assertEquals(contentTypeId, contentType.getContentTypeId());
        assertEquals(contentTypeName, contentType.getContentTypeName());
        assertEquals(archived, contentType.getArchived());
        assertEquals(createdDate, contentType.getCreatedDate());
        assertEquals(updatedDate, contentType.getUpdatedDate());
    }
}


