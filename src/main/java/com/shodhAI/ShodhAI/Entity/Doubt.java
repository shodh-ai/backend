package com.shodhAI.ShodhAI.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Doubt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "doubt_id")
    @JsonProperty("doubt_id")
    private Long id;

    @Column(name="doubt", columnDefinition = "TEXT")
    @JsonProperty("doubt")
    private String doubt;

    @Column(name = "jsx_code", columnDefinition = "TEXT")
    @JsonProperty("jsx_code")
    private String jsxCode;

    @Column(name = "narration", columnDefinition = "TEXT")
    @JsonProperty("narration")
    private String narration;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "topic_id")
    @JsonProperty("topic_id")
    private Topic topic;

}
