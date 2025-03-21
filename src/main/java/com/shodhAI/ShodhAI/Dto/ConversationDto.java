package com.shodhAI.ShodhAI.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConversationDto {

    @JsonProperty("session_id")
    private Long sessionId;

    @JsonProperty("user_dialogue")
    private String userDialogue;

}
