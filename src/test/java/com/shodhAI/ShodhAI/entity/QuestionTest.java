package com.shodhAI.ShodhAI.entity;

import com.shodhAI.ShodhAI.Entity.Hint;
import com.shodhAI.ShodhAI.Entity.Question;
import com.shodhAI.ShodhAI.Entity.Course;
import com.shodhAI.ShodhAI.Entity.QuestionType;
import com.shodhAI.ShodhAI.Entity.Topic;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class QuestionTest {
    private Long questionId;
    private String question;
    private List<String> answers;
    private List<String> answerTemplates;
    private String cognitiveDomain;
    private Date createdDate;
    private Date updatedDate;
    private List<Hint> hints;
    private Topic topic;
    private QuestionType questionType;

    @BeforeEach
    void setUp() {
        questionId = 1L;
        question = "questionName";
        cognitiveDomain = "programName";
        answerTemplates = new ArrayList<>();
        answers=new ArrayList<>(){};
        questionType= new QuestionType();
        hints = new ArrayList<>() {};
        topic = new Topic();
        createdDate= new Date();
        updatedDate= new Date();
    }

    @Test
    @DisplayName("testQuestionConstructor")
    void testQuestionConstructor(){
        Question questionByConstructor =  Question.builder().id(questionId).question(question).cognitiveDomain(cognitiveDomain).questionType(questionType).createdDate(createdDate).updatedDate(updatedDate).hints(hints).topic(topic).answers(answers).answerTemplates(answerTemplates).build();

        assertEquals(questionId, questionByConstructor.getId());
        assertEquals(question, questionByConstructor.getQuestion());
        assertEquals(cognitiveDomain, questionByConstructor.getCognitiveDomain());
        assertEquals(questionType, questionByConstructor.getQuestionType());
        assertEquals(createdDate, questionByConstructor.getCreatedDate());
        assertEquals(updatedDate, questionByConstructor.getUpdatedDate());
        assertEquals(topic, questionByConstructor.getTopic());
    }

    @Test
    @DisplayName("testQuestionSettersAndGetters")
    void testQuestionSettersAndGetters(){
        Question question = getQuestion();
        assertEquals(questionId, question.getId());
        assertEquals(this.question, question.getQuestion());
        assertEquals(cognitiveDomain, question.getCognitiveDomain());
        assertEquals(questionType, question.getQuestionType());
        assertEquals(hints, question.getHints());
        assertEquals(topic, question.getTopic());
        assertEquals(createdDate, question.getCreatedDate());
        assertEquals(updatedDate, question.getUpdatedDate());
    }

    private @NotNull Question getQuestion() {
        Question question = new Question();
        question.setId(questionId);
        question.setQuestion(this.question);
        question.setCognitiveDomain(cognitiveDomain);
        question.setQuestionType(questionType);
        question.setCreatedDate(createdDate);
        question.setUpdatedDate(updatedDate);
        question.setHints(hints);
        question.setTopic(topic);
        return question;
    }
}

