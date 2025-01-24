package com.shodhAI.ShodhAI.Entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class Hint {

    @Column(name = "level")
    @JsonProperty("level")
    private String level = "basic";

    @Column(name = "text")
    @JsonProperty("text")
    private String text;

}