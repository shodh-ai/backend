package com.shodhAI.ShodhAI.entity;

import com.shodhAI.ShodhAI.Entity.DoubtLevel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DoubtLevelTest {
    private Long doubtLevelId;
    private String doubtLevelName;
    private Character archived;
    private Date createdDate;
    private Date updatedDate;

    @BeforeEach
    void setUp() {
        doubtLevelId = 1L;
        doubtLevelName = "doubtLevelName";
        archived = 'N';
        createdDate= new Date();
        updatedDate= new Date();
    }

    @Test
    @DisplayName("testDoubtLevelConstructor")
    void testDoubtLevelConstructor(){
        DoubtLevel doubtLevelByConstructor =  DoubtLevel.builder().doubtLevelId(doubtLevelId).doubtLevel(doubtLevelName).archived(archived).createdDate(createdDate).updatedDate(updatedDate).build();

        assertEquals(doubtLevelId, doubtLevelByConstructor.getDoubtLevelId());
        assertEquals(doubtLevelName, doubtLevelByConstructor.getDoubtLevel());
        assertEquals(archived, doubtLevelByConstructor.getArchived());
        assertEquals(createdDate, doubtLevelByConstructor.getCreatedDate());
        assertEquals(updatedDate, doubtLevelByConstructor.getUpdatedDate());
    }

    @Test
    @DisplayName("testDoubtLevelSettersAndGetters")
    void testDoubtLevelSettersAndGetters(){
        DoubtLevel doubtLevel = new DoubtLevel();
        doubtLevel.setDoubtLevelId(doubtLevelId);
        doubtLevel.setDoubtLevel(doubtLevelName);
        doubtLevel.setArchived(archived);
        doubtLevel.setCreatedDate(createdDate);
        doubtLevel.setUpdatedDate(updatedDate);
        assertEquals(doubtLevelId, doubtLevel.getDoubtLevelId());
        assertEquals(doubtLevelName, doubtLevel.getDoubtLevel());
        assertEquals(archived, doubtLevel.getArchived());
        assertEquals(createdDate, doubtLevel.getCreatedDate());
        assertEquals(updatedDate, doubtLevel.getUpdatedDate());
    }
}



