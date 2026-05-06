package com.skeli.dashboardservice.dto;

import lombok.Data;

@Data
public class AuthResponseDto {
    private String token;
    private String username;
    private String email;
    private String role;
}
