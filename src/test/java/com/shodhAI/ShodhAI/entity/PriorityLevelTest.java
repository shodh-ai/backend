package com.shodhAI.ShodhAI.entity;

import com.shodhAI.ShodhAI.Entity.PriorityLevel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PriorityLevelTest {
    private Long priorityLevelId;
    private String priorityLevelName;
    private Character archived;
    private Date createdDate;
    private Date updatedDate;

    @BeforeEach
    void setUp() {
        priorityLevelId = 1L;
        priorityLevelName = "priorityLevelName";
        archived = 'N';
        createdDate= new Date();
        updatedDate= new Date();
    }

    @Test
    @DisplayName("testPriorityLevelConstructor")
    void testPriorityLevelConstructor(){
        PriorityLevel priorityLevelByConstructor =  PriorityLevel.builder().priorityLevelId(priorityLevelId).priorityLevel(priorityLevelName).archived(archived).createdDate(createdDate).updatedDate(updatedDate).build();

        assertEquals(priorityLevelId, priorityLevelByConstructor.getPriorityLevelId());
        assertEquals(priorityLevelName, priorityLevelByConstructor.getPriorityLevel());
        assertEquals(archived, priorityLevelByConstructor.getArchived());
        assertEquals(createdDate, priorityLevelByConstructor.getCreatedDate());
        assertEquals(updatedDate, priorityLevelByConstructor.getUpdatedDate());
    }

    @Test
    @DisplayName("testPriorityLevelSettersAndGetters")
    void testPriorityLevelSettersAndGetters(){
        PriorityLevel priorityLevel = new PriorityLevel();
        priorityLevel.setPriorityLevelId(priorityLevelId);
        priorityLevel.setPriorityLevel(priorityLevelName);
        priorityLevel.setArchived(archived);
        priorityLevel.setCreatedDate(createdDate);
        priorityLevel.setUpdatedDate(updatedDate);
        assertEquals(priorityLevelId, priorityLevel.getPriorityLevelId());
        assertEquals(priorityLevelName, priorityLevel.getPriorityLevel());
        assertEquals(archived, priorityLevel.getArchived());
        assertEquals(createdDate, priorityLevel.getCreatedDate());
        assertEquals(updatedDate, priorityLevel.getUpdatedDate());
    }
}




