package com.shodhAI.ShodhAI.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionMaterialDto {

    @JsonProperty("file_type_id")
    private Long fileTypeId;

    @URL(message = "Invalid URL format")
    @JsonProperty("url")
    private String url;

}
