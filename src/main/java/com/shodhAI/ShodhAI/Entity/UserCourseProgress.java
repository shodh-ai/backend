package com.shodhAI.ShodhAI.Entity;

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
@Table(name = "user_course_progress")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserCourseProgress {

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

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    @JsonProperty("course")
    private Course course;

    @OneToMany(mappedBy = "userCourseProgress", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonProperty("module_progress")
    private List<UserModuleProgress> moduleProgress;

    @Column(name = "created_date", nullable = false, updatable = false)
    @JsonProperty("created_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private Date createdDate;

    @Column(name = "modified_date", nullable = false)
    @JsonProperty("modified_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private Date updatedDate;

}
