package com.shodhAI.ShodhAI.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentCompletionDto {
    private Long studentId;
    private String studentName;
    private Boolean completed;
    private Double score;
    private String submissionDate;
}