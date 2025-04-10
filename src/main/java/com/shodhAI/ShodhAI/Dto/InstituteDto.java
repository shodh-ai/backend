package com.shodhAI.ShodhAI.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InstituteDto {

    @JsonProperty("institution_name")
    private String institutionName;

    @JsonProperty("academic_degree_ids")
    List<Long> academicDegreeIds = new ArrayList<>();

}
