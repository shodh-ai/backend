package com.shodhAI.ShodhAI.entity;

import com.shodhAI.ShodhAI.Entity.Memory;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MemoryTest {
    private Long memoryId;
    private Double memory;
    private Double memoryImprovement;
    private Boolean memoryImprovementFlag;
    private Character archived;
    private Date createdDate;
    private Date updatedDate;

    @BeforeEach
    void setUp() {
        memoryId = 1L;
        memory = 5D;
        memoryImprovement = 5D;
        memoryImprovementFlag= true;
        createdDate= new Date();
        updatedDate= new Date();
    }

    @Test
    @DisplayName("testMemoryConstructor")
    void testMemoryConstructor(){
        Memory memoryByConstructor =  Memory.builder().id(memoryId).memory(memory).memoryImprovement(memoryImprovement).createdDate(createdDate).updatedDate(updatedDate).memoryImprovementFlag(memoryImprovementFlag).build();

        assertEquals(memoryId, memoryByConstructor.getId());
        assertEquals(memory, memoryByConstructor.getMemory());
        assertEquals(memoryImprovement, memoryByConstructor.getMemoryImprovement());
        assertEquals(memoryImprovementFlag, memoryByConstructor.getMemoryImprovementFlag());
        assertEquals(createdDate, memoryByConstructor.getCreatedDate());
        assertEquals(updatedDate, memoryByConstructor.getUpdatedDate());
    }

    @Test
    @DisplayName("testMemorySettersAndGetters")
    void testMemorySettersAndGetters(){
        Memory memory = getMemory();
        assertEquals(memoryId, memory.getId());
        assertEquals(this.memory, memory.getMemory());
        assertEquals(memoryImprovement, memory.getMemoryImprovement());
        assertEquals(memoryImprovementFlag, memory.getMemoryImprovementFlag());
        assertEquals(createdDate, memory.getCreatedDate());
        assertEquals(updatedDate, memory.getUpdatedDate());
    }

    private @NotNull Memory getMemory() {
        Memory memory = new Memory();
        memory.setId(memoryId);
        memory.setMemory(this.memory);
        memory.setMemoryImprovement(memoryImprovement);
        memory.setMemoryImprovementFlag(memoryImprovementFlag);
        memory.setCreatedDate(createdDate);
        memory.setUpdatedDate(updatedDate);
        return memory;
    }
}

