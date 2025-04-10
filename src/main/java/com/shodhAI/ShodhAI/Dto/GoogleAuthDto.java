package com.shodhAI.ShodhAI.Dto;

import lombok.Data;

@Data
public class GoogleAuthDto {
    private String token; // This is the ID token from Google
    private Long roleId;  // To identify if user is Student or Faculty
}