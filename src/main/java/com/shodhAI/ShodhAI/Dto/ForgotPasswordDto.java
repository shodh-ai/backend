package com.shodhAI.ShodhAI.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ForgotPasswordDto {

    @JsonProperty("email")
    private String email;

    @JsonProperty("role_id")
    private Long roleId;

    @JsonProperty("new_password")
    private String newPassword;

    @JsonProperty("confirm_password")
    private String confirmPassword;

}
