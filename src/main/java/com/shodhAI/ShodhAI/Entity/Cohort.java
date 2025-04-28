package com.shodhAI.ShodhAI.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Builder
@Entity
@Table(name = "cohort")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Cohort {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cohort_id")
    private Long cohortId;

    @Column(name = "title", unique = true)
    @JsonProperty("title")
    private String cohortTitle;

    @Column(name = "description")
    @JsonProperty("description")
    private String cohortDescription;

    @NonNull
    @Column(name = "archived")
    @JsonProperty("archived")
    private Character archived = 'N';

    @Column(name = "created_date")
    @JsonProperty("created_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private Date createdDate;

    @Column(name = "modified_date")
    @JsonProperty("modified_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private Date updatedDate;

    @Column(name = "creator_user_id")
    @JsonProperty("creator_user_id")
    private Long creatorUserId;

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

    @ManyToMany
    @JoinTable(
            name = "cohort_student",
            joinColumns = @JoinColumn(name = "cohort_id"),
            inverseJoinColumns = @JoinColumn(name = "student_id")
    )
    @JsonBackReference(value = "cohort-student")
    @JsonProperty("students")
    private List<Student> students = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "course_id")
    @JsonProperty("course")
    private Course course;

}
