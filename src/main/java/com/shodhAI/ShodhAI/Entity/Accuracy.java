package com.shodhAI.ShodhAI.Entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "accuracy")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Accuracy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "accuracy_id")
    @JsonProperty("accuracy_id")
    private Long id;

    @Column(name = "accuracy")
    @JsonProperty("accuracy")
    @Min(value = 0, message = "Accuracy must be at least 0.0")
    @Max(value = 100, message = "Accuracy must not exceed 100.0")
    private Double accuracy = 100.0;

    @Column(name = "accuracy_improvement_flag")
    @JsonProperty("accuracy_improvement_flag")
    private Boolean accuracyImprovementFlag;

    @Column(name = "accuracy_improvement")
    @JsonProperty("accuracy_improvement")
    private Double accuracyImprovement = 0.0;

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
