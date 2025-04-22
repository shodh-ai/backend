package com.shodhAI.ShodhAI.entity;

import com.shodhAI.ShodhAI.Entity.Assignment;
import com.shodhAI.ShodhAI.Entity.StudentAssignment;
import com.shodhAI.ShodhAI.Entity.Student;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.Date;
 import static org.junit.jupiter.api.Assertions.assertEquals;

public class StudentAssignmentTest {
    private Long studentAssignmentId;
    private String feedback;
    private String submittedFileUrl;
    private String submittedText;
    private Double score;
    private Boolean completionStatus;
    private Date submissionDate;
    private Date createdDate;
    private Date updatedDate;
    private Student student;
    private Assignment assignment;

    @BeforeEach
    void setUp() {
        studentAssignmentId = 1L;
        feedback = "studentAssignmentName";
        submittedFileUrl = "studentAssignmentDescription";
        submittedText = "studentAssignmentDuration";
        score = 12D;
        student = new Student();
        assignment = new Assignment();
        completionStatus = true;
        submissionDate = new Date();
        createdDate=new Date();
        updatedDate=new Date();
    }

    @Test
    @DisplayName("testStudentAssignmentConstructor")
    void testStudentAssignmentConstructor(){
        StudentAssignment studentAssignmentByConstructor =  StudentAssignment.builder().id(studentAssignmentId).feedback(feedback).submittedFileUrl(submittedFileUrl).score(score).submittedText(submittedText).completionStatus(completionStatus).submissionDate(submissionDate).student(student).createdDate(createdDate).updatedDate(updatedDate).assignment(assignment).build();

        assertEquals(studentAssignmentId, studentAssignmentByConstructor.getId());
        assertEquals(feedback, studentAssignmentByConstructor.getFeedback());
        assertEquals(submittedFileUrl, studentAssignmentByConstructor.getSubmittedFileUrl());
        assertEquals(submittedText, studentAssignmentByConstructor.getSubmittedText());
        assertEquals(score, studentAssignmentByConstructor.getScore());
        assertEquals(completionStatus, studentAssignmentByConstructor.getCompletionStatus());
        assertEquals(submissionDate, studentAssignmentByConstructor.getSubmissionDate());
        assertEquals(student, studentAssignmentByConstructor.getStudent());
        assertEquals(assignment, studentAssignmentByConstructor.getAssignment());
        assertEquals(createdDate, studentAssignmentByConstructor.getCreatedDate());
        assertEquals(updatedDate, studentAssignmentByConstructor.getUpdatedDate());
    }

    @Test
    @DisplayName("testStudentAssignmentSettersAndGetters")
    void testStudentAssignmentSettersAndGetters(){
        StudentAssignment studentAssignment = getStudentAssignment();
        assertEquals(studentAssignmentId, studentAssignment.getId());
        assertEquals(feedback, studentAssignment.getFeedback());
        assertEquals(submittedFileUrl, studentAssignment.getSubmittedFileUrl());
        assertEquals(submittedText, studentAssignment.getSubmittedText());
        assertEquals(score, studentAssignment.getScore());
        assertEquals(completionStatus, studentAssignment.getCompletionStatus());
        assertEquals(submissionDate, studentAssignment.getSubmissionDate());
        assertEquals(assignment, studentAssignment.getAssignment());
        assertEquals(createdDate, studentAssignment.getCreatedDate());
        assertEquals(updatedDate, studentAssignment.getUpdatedDate());
    }

    private @NotNull StudentAssignment getStudentAssignment() {
        StudentAssignment studentAssignment = new StudentAssignment();
        studentAssignment.setId(studentAssignmentId);
        studentAssignment.setFeedback(feedback);
        studentAssignment.setSubmittedFileUrl(submittedFileUrl);
        studentAssignment.setSubmittedText(submittedText);
        studentAssignment.setScore(score);
        studentAssignment.setCompletionStatus(completionStatus);
        studentAssignment.setSubmissionDate(submissionDate);
        studentAssignment.setAssignment(assignment);
        studentAssignment.setCreatedDate(createdDate);
        studentAssignment.setUpdatedDate(updatedDate);
        return studentAssignment;
    }
}

