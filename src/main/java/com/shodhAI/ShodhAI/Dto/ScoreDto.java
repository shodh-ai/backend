package com.shodhAI.ShodhAI.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.shodhAI.ShodhAI.Entity.Accuracy;
import com.shodhAI.ShodhAI.Entity.CriticalThinking;
import com.shodhAI.ShodhAI.Entity.Student;
import com.shodhAI.ShodhAI.Entity.TimeSpent;

public class ScoreDto {

    @JsonProperty("label")
    private String label;

    @JsonProperty("value")
    private Double value;

    @JsonProperty("improved_value")
    private Double improvedValue;

    @JsonProperty("improved_flag")
    private Boolean improvedFlag;

    public void wrapDetails(Accuracy accuracy) {
        this.label = "Accuracy";
        this.value = accuracy.getAccuracy();
        this.improvedValue = accuracy.getAccuracyImprovement();
        this.improvedFlag = accuracy.getAccuracyImprovementFlag();
    }

    public void wrapDetails(CriticalThinking criticalThinking) {
        this.label = "Critical Thinking";
        this.value = criticalThinking.getCriticalThinking();
        this.improvedValue = criticalThinking.getCriticalThinkingImprovement();
        this.improvedFlag = criticalThinking.getCriticalThinkingImprovementFlag();
    }

    public void wrapDetails(TimeSpent timeSpent) {
        this.label = "Time Spent";
        this.value = timeSpent.getTimeSpent();
        this.improvedValue = timeSpent.getTimeSpentIncreased();
        this.improvedFlag = true;
    }

    public void wrapDetails(Student student) {
        this.label = "Overall Score";
        this.value = student.getMarksObtained();
        this.improvedValue = student.getMarksImprovement();
        this.improvedFlag = true;
    }

}
