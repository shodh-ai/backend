package com.shodhAI.ShodhAI.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TopicDto {

    @JsonProperty("module_id")
    private Long moduleId;

    @JsonProperty("course_id")
    private Long courseId;

    @JsonProperty("topic_type_id")
    private Long topicTypeId;

    @JsonProperty("title")
    private String topicTitle;

    @JsonProperty("description")
    private String topicDescription;

    @JsonProperty("topic_duration")
    private String topicDuration;

}
