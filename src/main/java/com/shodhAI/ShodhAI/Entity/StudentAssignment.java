package com.shodhAI.ShodhAI.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
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
    @JoinColumn(name = "student_id", nullable = false)
    @JsonProperty("student")
    @JsonBackReference("student-assignment")
    private Student student;

    @JsonBackReference("assignment-student")
    @ManyToOne
    @JoinColumn(name = "assignment_id", nullable = false)
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

    @Lob
    @Column(name = "submitted_file",nullable = true)
    @JsonProperty("submitted_file")
    private byte[] submittedFile;

    @Column(name = "submitted_file_name",nullable = true)
    @JsonProperty("submitted_file_name")
    private String submittedFileName;

    @Column(name = "submitted_text", columnDefinition = "TEXT",nullable = true)
    @JsonProperty("submitted_text")
    private String submittedText;

    @Column(name = "created_date")
    @JsonProperty("created_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private Date createdDate;

    @Column(name = "modified_date")
    @JsonProperty("modified_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private Date updatedDate;
}