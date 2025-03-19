package com.shodhAI.ShodhAI.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @Column(name = "message", nullable = false)
    @JsonProperty("message")
    private String message;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "sender_id")
    @JsonProperty("sender")
    private Faculty sender;

    @Column(name = "created_date")
    @JsonProperty("created_date")
    private Date createdDate;

    @Column(name = "scheduled_date")
    @JsonProperty("scheduled_date")
    private Date scheduledDate;

    @Column(name = "is_sent")
    @JsonProperty("is_sent")
    private Boolean isSent;

    @Column(name = "archived", length = 1)
    private String archived = "N";

    @ManyToMany(fetch = FetchType.EAGER)
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
}