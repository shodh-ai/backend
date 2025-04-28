package com.shodhAI.ShodhAI.entity;

import com.shodhAI.ShodhAI.Entity.Understanding;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class UnderstandingTest {
    private Long understandingId;
    private Double understanding;
    private Double understandingImprovement;
    private Boolean understandingImprovementFlag;
    private Character archived;
    private Date createdDate;
    private Date updatedDate;

    @BeforeEach
    void setUp() {
        understandingId = 1L;
        understanding = 5D;
        understandingImprovement = 5D;
        understandingImprovementFlag= true;
        createdDate= new Date();
        updatedDate= new Date();
    }

    @Test
    @DisplayName("testUnderstandingConstructor")
    void testUnderstandingConstructor(){
        Understanding understandingByConstructor =  Understanding.builder().id(understandingId).understanding(understanding).understandingImprovement(understandingImprovement).createdDate(createdDate).updatedDate(updatedDate).understandingImprovementFlag(understandingImprovementFlag).build();

        assertEquals(understandingId, understandingByConstructor.getId());
        assertEquals(understanding, understandingByConstructor.getUnderstanding());
        assertEquals(understandingImprovement, understandingByConstructor.getUnderstandingImprovement());
        assertEquals(understandingImprovementFlag, understandingByConstructor.getUnderstandingImprovementFlag());
        assertEquals(createdDate, understandingByConstructor.getCreatedDate());
        assertEquals(updatedDate, understandingByConstructor.getUpdatedDate());
    }

    @Test
    @DisplayName("testUnderstandingSettersAndGetters")
    void testUnderstandingSettersAndGetters(){
        Understanding understanding = getUnderstanding();
        assertEquals(understandingId, understanding.getId());
        assertEquals(this.understanding, understanding.getUnderstanding());
        assertEquals(understandingImprovement, understanding.getUnderstandingImprovement());
        assertEquals(understandingImprovementFlag, understanding.getUnderstandingImprovementFlag());
        assertEquals(createdDate, understanding.getCreatedDate());
        assertEquals(updatedDate, understanding.getUpdatedDate());
    }

    private @NotNull Understanding getUnderstanding() {
        Understanding understanding = new Understanding();
        understanding.setId(understandingId);
        understanding.setUnderstanding(this.understanding);
        understanding.setUnderstandingImprovement(understandingImprovement);
        understanding.setUnderstandingImprovementFlag(understandingImprovementFlag);
        understanding.setCreatedDate(createdDate);
        understanding.setUpdatedDate(updatedDate);
        return understanding;
    }
}

