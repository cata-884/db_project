package com.example.Project.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
    private Long id;
    private String username;
    private String nume;
    private String prenume;
    private String email;
}
