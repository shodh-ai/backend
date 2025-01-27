package com.shodhAI.ShodhAI.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionRequestDto {

    @JsonProperty("module_id")
    private Long moduleId;

    @JsonProperty("topic_id")
    private Long topicId;

    @JsonProperty("question_material")
    private List<QuestionMaterialDto> questionMaterial;

}
