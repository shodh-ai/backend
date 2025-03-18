package com.shodhAI.ShodhAI.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AcademicDegreeDto {

    @JsonProperty("degree_name")
    private String degreeName;

    @JsonProperty("program_name")
    private String programName;

    @JsonProperty("institution_name")
    private String institutionName;

}
