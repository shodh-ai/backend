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
//@Table(name="course")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="course_id")
    private int courseId;

    @Column(name = "title")
    @JsonProperty("title")
    private String courseTitle;

    @Column(name = "description")
    @JsonProperty("description")
    private String courseDescription;

    @NonNull
    @Column(name = "archived")
    @JsonProperty("archived")
    private Character archived = 'N';

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

    @Column(name = "course_duration")
    @JsonProperty("course_duration")
    private String courseDuration;

    @Column(name = "start_date")
    @JsonProperty("start_date")
    private Date startDate;

    @Column(name = "end_date")
    @JsonProperty("end_date")
    private Date endDate;

}
