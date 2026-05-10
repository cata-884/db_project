package com.example.Project.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class ClientResponse {
    private Long id;
    private String username;
    private String nume;
    private String prenume;
    private String email;
    private String telefonFixCod;
    private String telefonFixNr;
    private String telefonMobilCod;
    private String telefonMobilNr;
    private String telefonFixFormatted;
    private String telefonMobilFormatted;
    private String adresa;
    private String oras;
    private LocalDate dataNastere;
}
