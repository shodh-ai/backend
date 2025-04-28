package com.shodhAI.ShodhAI.entity;

import com.shodhAI.ShodhAI.Entity.CriticalThinking;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CriticalThinkingTest {
    private Long criticalThinkingId;
    private Double criticalThinking;
    private Double criticalThinkingImprovement;
    private Boolean criticalThinkingImprovementFlag;
    private Character archived;
    private Date createdDate;
    private Date updatedDate;

    @BeforeEach
    void setUp() {
        criticalThinkingId = 1L;
        criticalThinking = 5D;
        criticalThinkingImprovement = 5D;
        criticalThinkingImprovementFlag= true;
        createdDate= new Date();
        updatedDate= new Date();
    }

    @Test
    @DisplayName("testCriticalThinkingConstructor")
    void testCriticalThinkingConstructor(){
        CriticalThinking criticalThinkingByConstructor =  CriticalThinking.builder().id(criticalThinkingId).criticalThinking(criticalThinking).criticalThinkingImprovement(criticalThinkingImprovement).createdDate(createdDate).updatedDate(updatedDate).criticalThinkingImprovementFlag(criticalThinkingImprovementFlag).build();

        assertEquals(criticalThinkingId, criticalThinkingByConstructor.getId());
        assertEquals(criticalThinking, criticalThinkingByConstructor.getCriticalThinking());
        assertEquals(criticalThinkingImprovement, criticalThinkingByConstructor.getCriticalThinkingImprovement());
        assertEquals(criticalThinkingImprovementFlag, criticalThinkingByConstructor.getCriticalThinkingImprovementFlag());
        assertEquals(createdDate, criticalThinkingByConstructor.getCreatedDate());
        assertEquals(updatedDate, criticalThinkingByConstructor.getUpdatedDate());
    }

    @Test
    @DisplayName("testCriticalThinkingSettersAndGetters")
    void testCriticalThinkingSettersAndGetters(){
        CriticalThinking criticalThinking = getCriticalThinking();
        assertEquals(criticalThinkingId, criticalThinking.getId());
        assertEquals(this.criticalThinking, criticalThinking.getCriticalThinking());
        assertEquals(criticalThinkingImprovement, criticalThinking.getCriticalThinkingImprovement());
        assertEquals(criticalThinkingImprovementFlag, criticalThinking.getCriticalThinkingImprovementFlag());
        assertEquals(createdDate, criticalThinking.getCreatedDate());
        assertEquals(updatedDate, criticalThinking.getUpdatedDate());
    }

    private @NotNull CriticalThinking getCriticalThinking() {
        CriticalThinking criticalThinking = new CriticalThinking();
        criticalThinking.setId(criticalThinkingId);
        criticalThinking.setCriticalThinking(this.criticalThinking);
        criticalThinking.setCriticalThinkingImprovement(criticalThinkingImprovement);
        criticalThinking.setCriticalThinkingImprovementFlag(criticalThinkingImprovementFlag);
        criticalThinking.setCreatedDate(createdDate);
        criticalThinking.setUpdatedDate(updatedDate);
        return criticalThinking;
    }
}

