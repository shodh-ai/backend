package com.shodhAI.ShodhAI.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.shodhAI.ShodhAI.Entity.Edge;
import com.shodhAI.ShodhAI.Entity.Node;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VisualizationDto {

    @JsonProperty("nodes")
    private List<Node> nodes;

    @JsonProperty("edges")
    private List<Edge> edges;

    @JsonProperty("jsx_code")
    private String jsxCode;

    @JsonProperty("topic_id")
    private Long topicId;

    @JsonProperty("topic")
    private String topic;

    @JsonProperty("narration")
    private String narration;

}
