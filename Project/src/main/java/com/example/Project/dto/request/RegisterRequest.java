package com.example.Project.dto.request;

import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String parola;
    private String nume;
    private String prenume;
    private String email;
}
