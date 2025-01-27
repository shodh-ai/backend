package com.shodhAI.ShodhAI.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.shodhAI.ShodhAI.Entity.Module;
import com.shodhAI.ShodhAI.Entity.Question;
import com.shodhAI.ShodhAI.Entity.Student;
import com.shodhAI.ShodhAI.Entity.Topic;

import java.util.List;

public class QuestionResponseWrapper {

    @JsonProperty("module_name")
    private String moduleName;

    @JsonProperty("topic_name")
    private String topicName;

    @JsonProperty("question_material")
    private List<QuestionMaterialDto> questionMaterialDtoList;

    @JsonProperty("question_list")
    private List<QuestionResponseDto> questionResponseDtoList;

    public void wrapDetails(List<QuestionResponseDto> questionResponseDtoList, List<QuestionMaterialDto> questionMaterialDtoList, Topic topic, Module module) {

        this.moduleName = module.getModuleTitle();
        this.topicName = module.getModuleTitle();
        this.questionMaterialDtoList = questionMaterialDtoList;
        this.questionResponseDtoList = questionResponseDtoList;

    }

}
