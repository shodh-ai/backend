package com.shodhAI.ShodhAI.Entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="gender")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Gender {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "gender_id")
    @JsonProperty("gender_id")
    protected Long genderId;

    @Column(name="gender_symbol")
    @JsonProperty("gender_symbol")
    protected Character genderSymbol;

    @Column(name="gender_name")
    @JsonProperty("gender_name")
    protected String genderName;

}
