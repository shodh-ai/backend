package com.shodhAI.ShodhAI.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SessionDto {

    @JsonProperty("topic_id")
    private Long topicId;

    @JsonProperty("question_type_id")
    private Long questionTypeId;

}
