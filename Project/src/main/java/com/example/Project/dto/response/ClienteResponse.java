package com.example.Project.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class ClienteResponse {
    private Long id;
    private String username;
    private String nume;
    private String prenume;
    private String email;
    private String telefonFix;
    private String telefonMobil;
    private String adresa;
    private String oras;
    private LocalDate dataNastere;
}
