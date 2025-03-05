package com.shodhAI.ShodhAI.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
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

import java.util.List;

@Entity
@Table(name = "nodes")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Node {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    @Column(name = "node_id")
    @JsonProperty("id")
    private String nodeId;

    @Column(name = "name")
    @JsonProperty("name")
    private String name;

    @Column(name = "type")
    @JsonProperty("type")
    private String type; // e.g., "root", "branch", "leaf"

    @Column(name = "columns")
    @JsonProperty("columns")
    private String columns;

    @Column(name = "attributes")
    @JsonProperty("attributes")
    private String attributes;

    @Column(name = "document")
    @JsonProperty("document")
    private String document;

    @ElementCollection
    @JsonProperty("properties")
    private List<String> properties; // e.g., ["institution_id", "name", "location"]

    @ManyToOne
    @JoinColumn(name = "topic_id")
    @JsonIgnore
    private Topic topic;

    @ManyToOne
    @JoinColumn(name = "doubt_id")
    @JsonIgnore
    private Doubt doubt;

}
