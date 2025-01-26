package com.shodhAI.ShodhAI.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.shodhAI.ShodhAI.Entity.Content;
import com.shodhAI.ShodhAI.Entity.Question;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FlowRequestDto {

    @JsonProperty("module")
    private String module;

    @JsonProperty("topic")
    private String topic;

    @JsonProperty("content_list")
    private List<Content> contentList;

    @JsonProperty("question_list")
    private List<Question> questionList;

}
