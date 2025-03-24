package com.shodhAI.ShodhAI.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VerifyOtpDto {

    @Email
    @JsonProperty("email")
    private String email;

    @JsonProperty("role_id")
    private Long roleId;

    @JsonProperty("otp")
    private String otp;

}
