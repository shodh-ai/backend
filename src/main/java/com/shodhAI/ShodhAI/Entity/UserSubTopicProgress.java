package com.shodhAI.ShodhAI.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Builder
@Entity
@Table(name = "user_subtopic_progress")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserSubTopicProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    @JsonProperty("user_id")
    private Long userId;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    @JsonProperty("role")
    private Role role;

    @ManyToOne
    @JoinColumn(name = "subtopic_id", nullable = false)
    @JsonProperty("subtopic")
    private Topic subTopic;

    @Column(name = "is_completed")
    @JsonProperty("is_completed")
    private boolean isCompleted = false;

    @ManyToOne
    @JoinColumn(name = "user_topic_progress_id"/*, nullable = false*/)
    @JsonProperty("user_topic_progress")
    private UserTopicProgress userTopicProgress;

    @OneToMany(mappedBy = "userSubTopicProgress", cascade = CascadeType.ALL)
    @JsonProperty("subcomponent_progress")
    @JsonBackReference
    private List<UserSubComponentProgress> subComponentProgress;

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
