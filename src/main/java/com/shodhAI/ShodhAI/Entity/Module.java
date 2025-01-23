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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

import java.util.Date;

//@Entity
//@Table(name="module")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Module {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="module_id")
    private int moduleId;

    @Column(name = "title")
    @JsonProperty("title")
    private String moduleTitle;

    @Column(name = "description")
    @JsonProperty("description")
    private String moduleDescription;

    @NonNull
    @Column(name = "archived")
    @JsonProperty("archived")
    private Character archived = 'N';

    @Column(name = "description")
    @JsonProperty("description")
    private String description;

    @Column(name = "created_date")
    @JsonProperty("created_date")
    private Date createdDate;

    @Column(name = "modified_date")
    @JsonProperty("modified_date")
    private Date modifiedDate;

    @Column(name = "creator_user_id")
    @JsonProperty("creator_user_id")
    private Long creatorUserId;

    @Column(name = "creator_role_id")
    @JsonProperty("creator_role_id")
    private Role creatorRole;

    @Column(name = "modifier_user_id")
    @JsonProperty("modifier_user_id")
    private Long modifierUserId;

    @Column(name = "modifier_role_id")
    @JsonProperty("modifier_role_id")
    private Role modifierRole;

    @Column(name = "module_duration")
    @JsonProperty("module_duration")
    private String moduleDuration;

}
