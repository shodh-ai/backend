package com.shodhAI.ShodhAI.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionResponseDto {

    @JsonProperty("question")
    private String question;

    @JsonProperty("question_type")
    private String questionType;

    @JsonProperty("answer")
    private String answer;

    @JsonProperty("cognitive_domain")
    private String cognitiveDomain;

    @JsonProperty("hints")
    private List<String> hints;

}
