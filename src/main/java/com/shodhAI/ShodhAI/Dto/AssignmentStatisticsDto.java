package com.shodhAI.ShodhAI.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentStatisticsDto {
    private Long assignmentId;
    private String assignmentName;
    private Integer totalStudents;
    private Integer completedCount;
    private Double completionPercentage;
    private List<StudentCompletionDto> studentCompletionList;
    private Map<String, Integer> completionByDate;
}

