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
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "user_topic_progress")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserTopicProgress {

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
    @JoinColumn(name = "topic_id", nullable = false)
    @JsonProperty("topic")
    private Topic topic;

    @Column(name = "is_completed", nullable = false)
    @JsonProperty("is_completed")
    private boolean isCompleted = false;

    // 🔹 Linking to UserModuleProgress
    @ManyToOne
    @JoinColumn(name = "user_module_progress_id"/*, nullable = false*/)
    @JsonProperty("user_module_progress")
    private UserModuleProgress userModuleProgress;

    @OneToMany(mappedBy = "userTopicProgress", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonProperty("subtopic_progress")
    @JsonBackReference
    private List<UserSubTopicProgress> subTopicProgress;

    @Column(name = "created_date", nullable = false, updatable = false)
    @JsonProperty("created_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private Date createdDate;

    @Column(name = "modified_date", nullable = false)
    @JsonProperty("modified_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private Date updatedDate;

}
