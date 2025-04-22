package com.shodhAI.ShodhAI.entity;

import com.shodhAI.ShodhAI.Entity.QuestionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class QuestionTypeTest {
    private Long questionTypeId;
    private String questionTypeName;
    private Character archived;
    private Date createdDate;
    private Date updatedDate;

    @BeforeEach
    void setUp() {
        questionTypeId = 1L;
        questionTypeName= "questionTypeName";
        archived = 'N';
        createdDate= new Date();
        updatedDate= new Date();
    }

    @Test
    @DisplayName("testQuestionTypeConstructor")
    void testQuestionTypeConstructor(){
        QuestionType questionTypeByConstructor =  QuestionType.builder().questionTypeId(questionTypeId).questionType(questionTypeName).archived(archived).createdDate(createdDate).updatedDate(updatedDate).build();

        assertEquals(questionTypeId, questionTypeByConstructor.getQuestionTypeId());
        assertEquals(questionTypeName, questionTypeByConstructor.getQuestionType());
        assertEquals(archived, questionTypeByConstructor.getArchived());
        assertEquals(createdDate, questionTypeByConstructor.getCreatedDate());
        assertEquals(updatedDate, questionTypeByConstructor.getUpdatedDate());
    }

    @Test
    @DisplayName("testQuestionTypeSettersAndGetters")
    void testQuestionTypeSettersAndGetters(){
        QuestionType questionType = new QuestionType();
        questionType.setQuestionTypeId(questionTypeId);
        questionType.setQuestionType(questionTypeName);
        questionType.setArchived(archived);
        questionType.setCreatedDate(createdDate);
        questionType.setUpdatedDate(updatedDate);
        assertEquals(questionTypeId, questionType.getQuestionTypeId());
        assertEquals(questionTypeName, questionType.getQuestionType());
        assertEquals(archived, questionType.getArchived());
        assertEquals(createdDate, questionType.getCreatedDate());
        assertEquals(updatedDate, questionType.getUpdatedDate());
    }
}




