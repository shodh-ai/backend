package com.shodhAI.ShodhAI.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModuleDto {

    @JsonProperty("module_title")
    private String moduleTitle;

    @JsonProperty("module_description")
    private String moduleDescription;

    @JsonProperty("module_duration")
    private String moduleDuration;

    @JsonProperty("course_id")
    private Long courseId;

}
