package com.shodhAI.ShodhAI.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CohortDto {

    @JsonProperty("cohort_title")
    private String cohortTitle;

    @JsonProperty("cohort_description")
    private String cohortDescription;

    @JsonProperty("course_id")
    private Long courseId;

}
