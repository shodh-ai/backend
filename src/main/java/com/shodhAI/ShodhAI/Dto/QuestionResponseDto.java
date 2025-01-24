package com.shodhAI.ShodhAI.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionResponseDto {

    @JsonProperty("question")
    private String question;

    @JsonProperty("answer")
    private String answer;

    @JsonProperty("cognitive_domain")
    private String cognitiveDomain;

    @JsonProperty("hints")
    private List<String> hints;

    @JsonProperty("created_date")
    private Date createdDate;

    @JsonProperty("modified_date")
    private Date updatedDate;

}
