package com.shodhAI.ShodhAI.Entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name="conversation")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Conversation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "interaction_id")
    @JsonProperty("interaction_id")
    private Long id;

    @Column(name="user_text", columnDefinition = "TEXT")
    @JsonProperty("user_text")
    private String userText;

    @Column(name = "response", columnDefinition = "TEXT")
    @JsonProperty("response")
    private String response;

    @ManyToOne
    @JoinColumn(name = "session_id", nullable = false)
    @JsonProperty("session_id")
    private Session session;

    @Column(name = "user_id")
    @JsonProperty("user_id")
    private Long userId;

    @ManyToOne
    @JoinColumn(name = "user_role_id")
    @JsonProperty("user_role")
    private Role userRole;

    @Column(name = "user_text_timestamp")
    @JsonProperty("created_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    // TODO (MIGHT HAVE TO CHANGE IN FUTURE) this won't work with instant as instant does not have calendar features like LocalDateTime etc.
    private Date userTextTimestamp;

    @Column(name = "response_timestamp")
    @JsonProperty("response_timestamp")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    // TODO (MIGHT HAVE TO CHANGE IN FUTURE) this won't work with instant as instant does not have calendar features like LocalDateTime etc.
    private Date responseTimestamp;

}
