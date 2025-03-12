package com.shodhAI.ShodhAI.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.shodhAI.ShodhAI.Entity.Topic;

import java.util.List;

public class ParentTopicWrapper {

    @JsonProperty("topic")
    Topic parentTopic;

    @JsonProperty("sub_topic")
    List<Topic> subTopics;

    public void wrapDetails(Topic topic, List<Topic> subTopics) {
        parentTopic = topic;
        this.subTopics = subTopics;
    }

}
