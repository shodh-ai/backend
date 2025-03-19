package com.shodhAI.ShodhAI.Entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
    @JsonProperty("student")
    private Student student;

    @Column(name = "is_read")
    @JsonProperty("is_read")
    private Boolean isRead = false;

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