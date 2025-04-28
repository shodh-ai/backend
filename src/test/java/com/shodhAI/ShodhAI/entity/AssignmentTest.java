package com.shodhAI.ShodhAI.entity;

import com.shodhAI.ShodhAI.Entity.Assignment;
import com.shodhAI.ShodhAI.Entity.PriorityLevel;
import com.shodhAI.ShodhAI.Entity.StudentAssignment;
import com.shodhAI.ShodhAI.Entity.Topic;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AssignmentTest {
    private Long assignmentId;
    private String assignmentName;
    private String assignmentDescription;
    private String fileUrl;
    private Character archived;
    private Date createdDate;
    private Date updatedDate;
    private Date activeStartDate;
    private Date activeEndDate;
    private Topic topic;
    private PriorityLevel priorityLevel;
    private List<StudentAssignment> studentAssignments;

    @BeforeEach
    void setUp() {
        assignmentId = 1L;
        assignmentName = "assignmentName";
        assignmentDescription = "programName";
        fileUrl= "fileUrl";
        activeStartDate= new Date();
        activeEndDate= new Date();
        topic= new Topic();
        priorityLevel= new PriorityLevel();
        archived = 'N';
        studentAssignments = new ArrayList<>() {};
        createdDate= new Date();
        updatedDate= new Date();
    }

    @Test
    @DisplayName("testAssignmentConstructor")
    void testAssignmentConstructor(){
        Assignment assignmentByConstructor =  Assignment.builder().assignmentId(assignmentId).assignmentName(assignmentName).assignmentDescription(assignmentDescription).fileUrl(fileUrl).activeEndDate(activeEndDate).activeStartDate(activeStartDate).topic(topic).priorityLevel(priorityLevel).archived(archived).createdDate(createdDate).updatedDate(updatedDate).studentAssignments(studentAssignments).build();

        assertEquals(assignmentId, assignmentByConstructor.getAssignmentId());
        assertEquals(assignmentName, assignmentByConstructor.getAssignmentName());
        assertEquals(assignmentDescription, assignmentByConstructor.getAssignmentDescription());
        assertEquals(fileUrl, assignmentByConstructor.getFileUrl());
        assertEquals(activeStartDate, assignmentByConstructor.getActiveStartDate());
        assertEquals(activeEndDate, assignmentByConstructor.getActiveEndDate());
        assertEquals(priorityLevel, assignmentByConstructor.getPriorityLevel());
        assertEquals(topic, assignmentByConstructor.getTopic());
        assertEquals(archived, assignmentByConstructor.getArchived());
        assertEquals(createdDate, assignmentByConstructor.getCreatedDate());
        assertEquals(updatedDate, assignmentByConstructor.getUpdatedDate());
    }

    @Test
    @DisplayName("testAssignmentSettersAndGetters")
    void testAssignmentSettersAndGetters(){
        Assignment assignment = getAssignment();
        assertEquals(assignmentId, assignment.getAssignmentId());
        assertEquals(assignmentName, assignment.getAssignmentName());
        assertEquals(assignmentDescription, assignment.getAssignmentDescription());
        assertEquals(archived, assignment.getArchived());
        assertEquals(fileUrl, assignment.getFileUrl());
        assertEquals(topic, assignment.getTopic());
        assertEquals(priorityLevel, assignment.getPriorityLevel());
        assertEquals(studentAssignments, assignment.getStudentAssignments());
        assertEquals(activeEndDate, assignment.getActiveEndDate());
        assertEquals(activeStartDate, assignment.getActiveStartDate());
        assertEquals(createdDate, assignment.getCreatedDate());
        assertEquals(updatedDate, assignment.getUpdatedDate());
    }

    private @NotNull Assignment getAssignment() {
        Assignment assignment = new Assignment();
        assignment.setAssignmentId(assignmentId);
        assignment.setAssignmentName(assignmentName);
        assignment.setAssignmentDescription(assignmentDescription);
        assignment.setArchived(archived);
        assignment.setTopic(topic);
        assignment.setPriorityLevel(priorityLevel);
        assignment.setActiveEndDate(activeEndDate);
        assignment.setActiveStartDate(activeStartDate);
        assignment.setFileUrl(fileUrl);
        assignment.setCreatedDate(createdDate);
        assignment.setUpdatedDate(updatedDate);
        return assignment;
    }
}

