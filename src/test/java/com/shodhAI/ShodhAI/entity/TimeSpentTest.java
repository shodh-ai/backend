package com.shodhAI.ShodhAI.entity;

import com.shodhAI.ShodhAI.Entity.TimeSpent;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TimeSpentTest {
    private Long timeSpentId;
    private Double timeSpent;
    private Double timeSpentIncreased;
    private Date createdDate;
    private Date updatedDate;

    @BeforeEach
    void setUp() {
        timeSpentId = 1L;
        timeSpent = 5D;
        timeSpentIncreased = 5D;
        createdDate= new Date();
        updatedDate= new Date();
    }

    @Test
    @DisplayName("testTimeSpentConstructor")
    void testTimeSpentConstructor(){
        TimeSpent timeSpentByConstructor =  TimeSpent.builder().id(timeSpentId).timeSpent(timeSpent).timeSpentIncreased(timeSpentIncreased).createdDate(createdDate).updatedDate(updatedDate).build();

        assertEquals(timeSpentId, timeSpentByConstructor.getId());
        assertEquals(timeSpent, timeSpentByConstructor.getTimeSpent());
        assertEquals(timeSpentIncreased, timeSpentByConstructor.getTimeSpentIncreased());
        assertEquals(createdDate, timeSpentByConstructor.getCreatedDate());
        assertEquals(updatedDate, timeSpentByConstructor.getUpdatedDate());
    }

    @Test
    @DisplayName("testTimeSpentSettersAndGetters")
    void testTimeSpentSettersAndGetters(){
        TimeSpent timeSpent = getTimeSpent();
        assertEquals(timeSpentId, timeSpent.getId());
        assertEquals(this.timeSpent, timeSpent.getTimeSpent());
        assertEquals(timeSpentIncreased, timeSpent.getTimeSpentIncreased());
        assertEquals(createdDate, timeSpent.getCreatedDate());
        assertEquals(updatedDate, timeSpent.getUpdatedDate());
    }

    private @NotNull TimeSpent getTimeSpent() {
        TimeSpent timeSpent = new TimeSpent();
        timeSpent.setId(timeSpentId);
        timeSpent.setTimeSpent(this.timeSpent);
        timeSpent.setTimeSpentIncreased(timeSpentIncreased);
        timeSpent.setCreatedDate(createdDate);
        timeSpent.setUpdatedDate(updatedDate);
        return timeSpent;
    }
}

