package com.shodhAI.ShodhAI.Entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "notification")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    @JsonProperty("notification_id")
    private Long id;

    @Column(name = "title")
    @JsonProperty("title")
    private String title;

    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    @JsonProperty("message")
    private String message;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    @JsonProperty("sender")
    private Faculty sender;

    @Column(name = "created_date")
    @JsonProperty("created_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private Date createdDate;

    @Column(name = "scheduled_date")
    @JsonProperty("scheduled_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private Date scheduledDate;

    @Column(name = "is_sent")
    @JsonProperty("is_sent")
    private Boolean isSent = false;

    @Column(name = "archived", length = 1)
    private Character archived = 'N';

    @ManyToMany
    @JoinTable(
            name = "notification_notification_type",
            joinColumns = @JoinColumn(name = "notification_id"),
            inverseJoinColumns = @JoinColumn(name = "notification_type_id")
    )
    @JsonProperty("notification_types")
    private List<NotificationType> notificationTypes;

    @OneToMany(mappedBy = "notification", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonProperty("recipients")  // Make sure this annotation is present
    private List<NotificationRecipient> recipients;

    @ManyToOne
    @JoinColumn(name = "course_id")
    @JsonProperty("course")
    private Course course;

    @Column(name = "is_course_announcement")
    @JsonProperty("is_course_announcement")
    private Boolean isCourseAnnouncement = false;
}