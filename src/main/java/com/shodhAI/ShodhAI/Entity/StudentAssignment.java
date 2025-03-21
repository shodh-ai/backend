package com.shodhAI.ShodhAI.Entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "student_assignment")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "student_assignment_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id")
    @JsonProperty("student")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "assignment_id")
    @JsonProperty("assignment")
    private Assignment assignment;

    @Column(name = "completion_status")
    @JsonProperty("completion_status")
    private Boolean completionStatus = false;

    @Column(name = "submission_date")
    @JsonProperty("submission_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private Date submissionDate;

    @Column(name = "score")
    @JsonProperty("score")
    private Double score;

    @Column(name = "feedback", columnDefinition = "TEXT")
    @JsonProperty("feedback")
    private String feedback;

    @Column(name = "created_date")
    @JsonProperty("created_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private Date createdDate;

    @Column(name = "modified_date")
    @JsonProperty("modified_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private Date updatedDate;
}