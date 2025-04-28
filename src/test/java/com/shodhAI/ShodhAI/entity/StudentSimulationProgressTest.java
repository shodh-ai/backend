package com.shodhAI.ShodhAI.entity;
import com.shodhAI.ShodhAI.Entity.Student;
import com.shodhAI.ShodhAI.Entity.StudentSimulationProgress;
import com.shodhAI.ShodhAI.Entity.Topic;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class StudentSimulationProgressTest {
    private Long studentSimulationProgressId;
    private Double timestamp;
    private Topic topic;
    private Student student;

    @BeforeEach
    void setUp() {
        studentSimulationProgressId = 1L;
        student = new Student();
        topic = new Topic();
        timestamp = 5D;
    }

    @Test
    @DisplayName("testStudentSimulationProgressConstructor")
    void testStudentSimulationProgressConstructor(){
        StudentSimulationProgress studentSimulationProgressByConstructor =  StudentSimulationProgress.builder().progressId(studentSimulationProgressId).student(student).timestamp(timestamp).topic(topic).build();

        assertEquals(studentSimulationProgressId, studentSimulationProgressByConstructor.getProgressId());
        assertEquals(student, studentSimulationProgressByConstructor.getStudent());
        assertEquals(timestamp, studentSimulationProgressByConstructor.getTimestamp());
        assertEquals(topic, studentSimulationProgressByConstructor.getTopic());
    }

    @Test
    @DisplayName("testStudentSimulationProgressSettersAndGetters")
    void testStudentSimulationProgressSettersAndGetters(){
        StudentSimulationProgress studentSimulationProgress = getStudentSimulationProgress();
        assertEquals(studentSimulationProgressId, studentSimulationProgress.getProgressId());
        assertEquals(student, studentSimulationProgress.getStudent());
        assertEquals(topic, studentSimulationProgress.getTopic());
        assertEquals(timestamp, studentSimulationProgress.getTimestamp());
    }

    private @NotNull StudentSimulationProgress getStudentSimulationProgress() {
        StudentSimulationProgress studentSimulationProgress = new StudentSimulationProgress();
        studentSimulationProgress.setProgressId(studentSimulationProgressId);
        studentSimulationProgress.setStudent(student);
        studentSimulationProgress.setTimestamp(timestamp);
        studentSimulationProgress.setTopic(topic);
        return studentSimulationProgress;
    }
}

