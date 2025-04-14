package com.shodhAI.ShodhAI.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.List;

@Entity
@Table(name = "notification_type")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_type_id")
    @JsonProperty("notification_type_id")
    private Long id;

    @Column(name = "type_name", unique = true)
    @JsonProperty("type_name")
    private String typeName;

    @Column(name = "description")
    @JsonProperty("description")
    private String description;

    @NonNull
    @Column(name = "archived")
    @JsonProperty("archived")
    private Character archived = 'N';

    @JsonIgnore
    @ManyToMany(mappedBy = "notificationTypes")
    private List<Notification> notifications;
}