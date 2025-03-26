package com.shodhAI.ShodhAI.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "concept_clarification")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConceptClarification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "clarification_id")
    private Long clarificationId;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(name = "term", nullable = false)
    private String term;

    @Column(name = "definition", columnDefinition = "TEXT")
    private String definition;

    @ElementCollection
    @CollectionTable(name = "clarification_examples", joinColumns = @JoinColumn(name = "clarification_id"))
    @Column(name = "example")
    private List<String> examples;

    @ElementCollection
    @CollectionTable(name = "clarification_resources", joinColumns = @JoinColumn(name = "clarification_id"))
    @Column(name = "resource_link")
    private List<String> resourceLinks;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    @Column(name = "created_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    @ManyToOne
    @JoinColumn(name = "clarification_status_id", nullable = false)
    private ClarificationStatus clarificationStatus;
}
