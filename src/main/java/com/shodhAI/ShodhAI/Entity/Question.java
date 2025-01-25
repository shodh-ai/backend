package com.shodhAI.ShodhAI.Entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "question")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id")
    @JsonProperty("question_id")
    private Long id;

    @Column(name = "question")
    @JsonProperty("question")
    private String question;

    @Column(name = "answer")
    @JsonProperty("answer")
    private String answer;

    @Column(name = "cognitive_domain")
    @JsonProperty("cognitive_domain")
    private String cognitiveDomain;

    @ElementCollection
    @CollectionTable(
            name = "question_hints",
            joinColumns = @JoinColumn(name = "question_id") // Foreign key to `question` table
    )
    @AttributeOverrides({
            @AttributeOverride(name = "level", column = @Column(name = "hint_level")),
            @AttributeOverride(name = "text", column = @Column(name = "hint_text"))
    })
    @JsonProperty("hints")
    private List<Hint> hints;

    @Column(name = "created_date")
    @JsonProperty("created_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    // TODO (MIGHT HAVE TO CHANGE IN FUTURE) this won't work with instant as instant does not have calendar features like LocalDateTime etc.
    private Date createdDate;

    @Column(name = "modified_date")
    @JsonProperty("modified_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    // TODO (MIGHT HAVE TO CHANGE IN FUTURE) this won't work with instant as instant does not have calendar features like LocalDateTime etc.
    private Date updatedDate;

}
