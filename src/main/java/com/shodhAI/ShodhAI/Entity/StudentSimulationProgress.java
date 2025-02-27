package com.shodhAI.ShodhAI.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

@Entity
@Table(name = "user_simulation_progress")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentSimulationProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "progress_id")
    @JsonIgnore
    private Long progressId;

    @ManyToOne
    @JoinColumn(name = "topic_id", nullable = false)
    @JsonProperty("topic_id")
    @JsonIgnore
    private Topic topic;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    @JsonProperty("student_id")
    @JsonIgnore
    private Student student;

    @Column(name = "timestamp")
    @JsonProperty("timestamp")
    private Double timestamp;  // Timestamp (in seconds) for where the user left off in the simulation/video
}