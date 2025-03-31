package com.shodhAI.ShodhAI.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
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
import lombok.NonNull;

import java.util.Date;

@Entity
@Table(name = "module")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Module {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "module_id")
    private Long moduleId;

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

    @Column(name = "created_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    @JsonProperty("created_date")
    private Date createdDate;

    @Column(name = "modified_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    @JsonProperty("modified_date")
    private Date updatedDate;

    @Column(name = "creator_user_id")
    @JsonProperty("creator_user_id")
    private Long creatorUserId;

    public Module(Long moduleId, String moduleTitle, String moduleDescription, @NonNull Character archived, Date createdDate, String moduleDuration) {
        this.moduleId = moduleId;
        this.moduleTitle = moduleTitle;
        this.moduleDescription = moduleDescription;
        this.archived = archived;
        this.createdDate = createdDate;
        this.moduleDuration = moduleDuration;
    }

    @ManyToOne
    @JoinColumn(name = "creator_role_id")
    @JsonProperty("creator_role")
    private Role creatorRole;

    @Column(name = "modifier_user_id")
    @JsonProperty("modifier_user_id")
    private Long modifierUserId;

    @ManyToOne
    @JoinColumn(name = "modifier_role_id")
    @JsonProperty("modifier_role")
    private Role modifierRole;

    @Column(name = "module_duration")
    @JsonProperty("module_duration")
    private String moduleDuration;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "course_id")
    @JsonProperty("course")
    private Course course;

}
