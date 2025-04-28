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
@Table(name = "user_module_progress")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserModuleProgress {

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

    @Column(name = "is_completed", nullable = false)
    @JsonProperty("is_completed")
    private boolean isCompleted = false;

    // 🔹 Linking to UserCourseProgress
    @ManyToOne
    @JoinColumn(name = "user_course_progress_id"/*, nullable = false*/)
    @JsonProperty("user_course_progress")
    private UserCourseProgress userCourseProgress;

    @ManyToOne
    @JoinColumn(name = "module_id", nullable = false)
    @JsonProperty("module")
    private Module module;

    @OneToMany(mappedBy = "userModuleProgress", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonProperty("topic_progress")
    @JsonBackReference
    private List<UserTopicProgress> topicProgress;

    @Column(name = "created_date", nullable = false, updatable = false)
    @JsonProperty("created_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private Date createdDate;

    @Column(name = "modified_date", nullable = false)
    @JsonProperty("modified_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private Date updatedDate;

}
