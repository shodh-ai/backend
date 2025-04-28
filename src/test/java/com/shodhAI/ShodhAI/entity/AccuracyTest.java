package com.shodhAI.ShodhAI.entity;

import com.shodhAI.ShodhAI.Entity.Accuracy;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AccuracyTest {
    private Long accuracyId;
    private Double accuracy;
    private Double accuracyImprovement;
    private Boolean accuracyImprovementFlag;
    private Character archived;
    private Date createdDate;
    private Date updatedDate;

    @BeforeEach
    void setUp() {
        accuracyId = 1L;
        accuracy = 5D;
        accuracyImprovement = 5D;
        accuracyImprovementFlag= true;
        createdDate= new Date();
        updatedDate= new Date();
    }

    @Test
    @DisplayName("testAccuracyConstructor")
    void testAccuracyConstructor(){
        Accuracy accuracyByConstructor =  Accuracy.builder().id(accuracyId).accuracy(accuracy).accuracyImprovement(accuracyImprovement).createdDate(createdDate).updatedDate(updatedDate).accuracyImprovementFlag(accuracyImprovementFlag).build();

        assertEquals(accuracyId, accuracyByConstructor.getId());
        assertEquals(accuracy, accuracyByConstructor.getAccuracy());
        assertEquals(accuracyImprovement, accuracyByConstructor.getAccuracyImprovement());
        assertEquals(accuracyImprovementFlag, accuracyByConstructor.getAccuracyImprovementFlag());
        assertEquals(createdDate, accuracyByConstructor.getCreatedDate());
        assertEquals(updatedDate, accuracyByConstructor.getUpdatedDate());
    }

    @Test
    @DisplayName("testAccuracySettersAndGetters")
    void testAccuracySettersAndGetters(){
        Accuracy accuracy = getAccuracy();
        assertEquals(accuracyId, accuracy.getId());
        assertEquals(this.accuracy, accuracy.getAccuracy());
        assertEquals(accuracyImprovement, accuracy.getAccuracyImprovement());
        assertEquals(accuracyImprovementFlag, accuracy.getAccuracyImprovementFlag());
        assertEquals(createdDate, accuracy.getCreatedDate());
        assertEquals(updatedDate, accuracy.getUpdatedDate());
    }

    private @NotNull Accuracy getAccuracy() {
        Accuracy accuracy = new Accuracy();
        accuracy.setId(accuracyId);
        accuracy.setAccuracy(this.accuracy);
        accuracy.setAccuracyImprovement(accuracyImprovement);
        accuracy.setAccuracyImprovementFlag(accuracyImprovementFlag);
        accuracy.setCreatedDate(createdDate);
        accuracy.setUpdatedDate(updatedDate);
        return accuracy;
    }
}

