package com.shodhAI.ShodhAI.Entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "assignment")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Assignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "assignment_id")
    private Long assignmentId;

    @NonNull
    @Column(name = "assignment_name")
    @JsonProperty("assignment_name")
    private String assignmentName;

    @Column(name = "assignment_description")
    @JsonProperty("assignment_description")
    private String assignmentDescription;

    @Column(name = "file_url")
    @JsonProperty("file_url")
    private String fileUrl;

    @NonNull
    @ManyToOne
    @JoinColumn(name = "priority_level_id")
    @JsonProperty("priority_level")
    private PriorityLevel priorityLevel;

    @NonNull
    @Column(name = "archived")
    @JsonProperty("archived")
    private Character archived = 'N';

    @Column(name = "active_start_date")
    @JsonProperty("active_start_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    // TODO (MIGHT HAVE TO CHANGE IN FUTURE) this won't work with instant as instant does not have calendar features like LocalDateTime etc.
    private Date activeStartDate;

    @Column(name = "active_end_date")
    @JsonProperty("active_end_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    // TODO (MIGHT HAVE TO CHANGE IN FUTURE) this won't work with instant as instant does not have calendar features like LocalDateTime etc.
    private Date activeEndDate;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "topic_id")
    @JsonProperty("topic")
    private Topic topic;

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

    @JsonIgnore
    @JsonManagedReference("assignment-student")
    @OneToMany(mappedBy = "assignment")
    @JsonProperty("student_assignments")
    private List<StudentAssignment> studentAssignments = new ArrayList<>();

}
