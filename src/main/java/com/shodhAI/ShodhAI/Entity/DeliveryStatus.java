package com.shodhAI.ShodhAI.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.OneToMany;
import jakarta.persistence.FetchType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "delivery_status")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class  DeliveryStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "delivery_status_id")
    @JsonProperty("delivery_status_id")
    private Long id;

    @Column(name = "code")
    @JsonProperty("code")
    private String code;

    @Column(name = "description")
    @JsonProperty("description")
    private String description;

    @JsonIgnore
    @OneToMany(mappedBy = "deliveryStatus", fetch = FetchType.LAZY)
    private List<NotificationRecipient> notificationRecipients;
}