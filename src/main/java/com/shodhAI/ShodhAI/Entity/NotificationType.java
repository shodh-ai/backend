package com.shodhAI.ShodhAI.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "notification_type")
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties("notifications") // Prevents infinite recursion issue
public class NotificationType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_type_id")
    @JsonProperty("notification_type_id")
    private Long id;

    @Column(name = "code", unique = true, nullable = false)
    @JsonProperty("code")
    private String code;

    @Column(name = "description")
    @JsonProperty("description")
    private String description;

    @ManyToMany(mappedBy = "notificationTypes")
    private List<Notification> notifications;
}
