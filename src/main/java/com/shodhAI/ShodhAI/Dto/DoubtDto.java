package com.shodhAI.ShodhAI.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DoubtDto {

    @JsonProperty("doubt")
    private String doubt;

    @JsonProperty("topic_id")
    private Long topicId;

}
