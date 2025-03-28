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
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.Date;

@Entity
@Table(name = "topic")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Topic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "topic_id")
    private Long topicId;

    @JsonBackReference("module-topic")
    @ManyToOne
    @NotNull
    @JoinColumn(name = "module_id")
    @JsonProperty("module_id")
    private Module module;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "course_id")
    @JsonProperty("course_id")
    @JsonBackReference("course-topic")
    private Course course;

    @ManyToOne
    @JoinColumn(name = "topic_type_id")
    @JsonProperty("topic_type_id")
    private TopicType topicType;

    @Column(name = "title")
    @JsonProperty("title")
    private String topicTitle;

    @Column(name = "description")
    @JsonProperty("description")
    private String topicDescription;

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

    @Column(name = "topic_duration")
    @JsonProperty("topic_duration")
    private String topicDuration;

    @ManyToOne
    @JoinColumn(name = "default_parent_topic_id")
    @JsonProperty("default_parent_topic")
    @JsonBackReference("default-parent-topic")
    private Topic defaultParentTopic;

    @Column(name = "jsx_code", columnDefinition = "TEXT")
    @JsonProperty("jsx_code")
    private String jsxCode;

    @Column(name = "narration", columnDefinition = "TEXT")
    @JsonProperty("narration")
    private String narration;

}
