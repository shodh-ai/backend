package com.shodhAI.ShodhAI.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ReportWrapper {

    @JsonProperty("critical_thinking")
    private Double criticalThinking;

    @JsonProperty("understanding")
    private Double understanding;

    @JsonProperty("memory")
    private Double memory;

    @JsonProperty("weakness")
    private List<String> weakness;

    @JsonProperty("strength")
    private List<String> strength;

    @JsonProperty("summary")
    private String summary;

    @JsonProperty("comments")
    private String comments;

}
