package com.shodhAI.ShodhAI.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SessionFilterDto {

    @JsonProperty("session_id")
    private Long sessionId;

    @JsonProperty("topic_id")
    private Long topicId;

    @JsonProperty("question_type_id")
    private Long questionTypeId;

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("role_id")
    private Long roleId;

}
