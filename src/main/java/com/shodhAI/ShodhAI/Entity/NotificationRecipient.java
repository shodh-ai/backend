package com.shodhAI.ShodhAI.Entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.Date;

@Entity
@Table(name = "notification_recipient")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationRecipient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_recipient_id")
    @JsonProperty("notification_recipient_id")
    private Long id;

    @NonNull
    @Column(name = "archived")
    @JsonProperty("archived")
    private Character archived = 'N';

    @ManyToOne
    @NotNull
    @JsonIgnore
    @JoinColumn(name = "notification_id")
    @JsonProperty("notification")
    private Notification notification;

    @ManyToOne
    @JoinColumn(name = "student_id")
    @JsonProperty("recipient")
    private Student recipient;

    @Column(name = "read_status")
    @JsonProperty("read_status")
    private Boolean readStatus = false;

    @Column(name = "read_date")
    @JsonProperty("read_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private Date readDate;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "delivery_status_id")
    @JsonProperty("delivery_status")
    private DeliveryStatus deliveryStatus;
}