package com.shodhAI.ShodhAI.entity;
import com.shodhAI.ShodhAI.Entity.QuestionType;
import com.shodhAI.ShodhAI.Entity.Session;
import com.shodhAI.ShodhAI.Entity.Topic;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SessionTest {
    private Long sessionId;
    private Date startTime;
    private Date endTime;
    private Topic topic;
    private QuestionType questionType;

    @BeforeEach
    void setUp() {
        sessionId = 1L;
        questionType = new QuestionType();
        topic = new Topic();
        startTime = new Date();
        endTime = new Date();
    }

    @Test
    @DisplayName("testSessionConstructor")
    void testSessionConstructor(){
        Session sessionByConstructor =  Session.builder().sessionId(sessionId).questionType(questionType).startTime(startTime).endTime(endTime).topic(topic).build();

        assertEquals(sessionId, sessionByConstructor.getSessionId());
        assertEquals(questionType, sessionByConstructor.getQuestionType());
        assertEquals(startTime, sessionByConstructor.getStartTime());
        assertEquals(endTime, sessionByConstructor.getEndTime());
        assertEquals(topic, sessionByConstructor.getTopic());
    }

    @Test
    @DisplayName("testSessionSettersAndGetters")
    void testSessionSettersAndGetters(){
        Session session = getSession();
        assertEquals(sessionId, session.getSessionId());
        assertEquals(questionType, session.getQuestionType());
        assertEquals(topic, session.getTopic());
        assertEquals(startTime, session.getStartTime());
        assertEquals(endTime, session.getEndTime());
    }

    private @NotNull Session getSession() {
        Session session = new Session();
        session.setSessionId(sessionId);
        session.setQuestionType(questionType);
        session.setStartTime(startTime);
        session.setEndTime(endTime);
        session.setTopic(topic);
        return session;
    }
}

