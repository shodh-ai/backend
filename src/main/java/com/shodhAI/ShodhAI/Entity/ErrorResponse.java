package com.shodhAI.ShodhAI.Entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ErrorResponse {

    @JsonProperty("status")
    private HttpStatus status;

    @JsonProperty("status_code")
    private int status_code;

    @JsonProperty("message")
    private String message;

}
