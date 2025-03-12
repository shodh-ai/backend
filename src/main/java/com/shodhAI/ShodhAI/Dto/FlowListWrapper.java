package com.shodhAI.ShodhAI.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class FlowListWrapper {

    @JsonProperty("teaching_flow")
    private List<String> teachingFlow;

}
