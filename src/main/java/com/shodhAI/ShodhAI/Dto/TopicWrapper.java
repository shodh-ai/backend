package com.shodhAI.ShodhAI.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.shodhAI.ShodhAI.Entity.Question;
import com.shodhAI.ShodhAI.Entity.Topic;

import java.util.List;

public class TopicWrapper {

    @JsonProperty("teaching")
    private Topic teaching;

    @JsonProperty("practice_question")
    private List<Question> practiceQuestion;

    @JsonProperty("example_question")
    private List<Question> exampleQuestion;

    @JsonProperty("testing_question")
    private List<Question> testingQuestion;

    @JsonProperty("quiz")
    private List<Question> quiz;

    public void wrapDetails(Topic topic, List<Question> practiceQuestion, List<Question> exampleQuestion, List<Question> testingQuestion, List<Question> quiz) {
        this.teaching = topic;
        this.practiceQuestion = practiceQuestion;
        this.exampleQuestion = exampleQuestion;
        this.testingQuestion = testingQuestion;
        this.quiz = quiz;
    }
}
