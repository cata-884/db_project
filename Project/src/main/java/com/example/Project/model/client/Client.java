package com.example.Project.model.client;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class Client {
    private Long id;
    private String nume;
    private String prenume;
    private PhoneNumber telefonFix;
    private String adresa;
    private String oras;
    private String email;
    private PhoneNumber telefonMobil;
    private LocalDate dataNastere;

    private String username;
    private String parola;
}
