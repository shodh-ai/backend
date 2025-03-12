package com.shodhAI.ShodhAI.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class StudentSemesterDto {

    @JsonProperty("semester_score")
    List<ScoreDto> scoreDtoList;

    public void wrapDetails(List<ScoreDto> scoreDtoList) {
        this.scoreDtoList = scoreDtoList;
    }

}
