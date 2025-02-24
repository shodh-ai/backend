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
@Table(name = "edges")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Edge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    @Column(name = "source_id")
    @JsonProperty("source_id")
    private String source;

    @Column(name = "target")
    @JsonProperty("target")
    private String target;

    @Column(name = "type")
    @JsonProperty("type")
    private String type;

    @Column(name = "description")
    @JsonProperty("description")
    private String description;

    @ManyToOne
    @JoinColumn(name = "topic_id")
    @JsonIgnore
    private Topic topic;

}

