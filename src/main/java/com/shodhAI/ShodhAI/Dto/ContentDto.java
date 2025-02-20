package com.shodhAI.ShodhAI.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.shodhAI.ShodhAI.Entity.FileType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContentDto {

    @JsonProperty("file_type_id")
    private FileType fileType;

    @JsonProperty("topic_id")
    private Long topicId;

    @JsonProperty("content_type_id")
    private Long contentTypeId;

}
