package com.shodhAI.ShodhAI.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.shodhAI.ShodhAI.Entity.Student;

public class LeaderboardWrapper {

    @JsonProperty("student_name")
    private String studentName;

    @JsonProperty("accuracy")
    private Double accuracy;

    @JsonProperty("critical_thinking")
    private Double criticalThinking;

    @JsonProperty("overall_score")
    private Double overallScore;

    public void wrapDetails(Student student) {

        if (student.getLastName() != null) {
            this.studentName = student.getFirstName() + " " + student.getLastName();
        } else {
            this.studentName = student.getFirstName();
        }
        this.overallScore = student.getMarksObtained();
        this.accuracy = student.getAccuracy().getAccuracy();
        this.criticalThinking = student.getCriticalThinking().getCriticalThinking();
    }

}
