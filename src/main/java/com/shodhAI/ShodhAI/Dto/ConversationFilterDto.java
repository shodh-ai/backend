package com.shodhAI.ShodhAI.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConversationFilterDto {

    @JsonProperty("session_id")
    private Long sessionId;

    @JsonProperty("topic_id")
    private Long topicId;

}
