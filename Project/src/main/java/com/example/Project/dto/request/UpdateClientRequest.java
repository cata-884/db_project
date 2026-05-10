package com.example.Project.dto.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateClientRequest {
    private String telefonFixCod;
    private String telefonFixNr;
    private String telefonMobilCod;
    private String telefonMobilNr;
    private String adresa;
    private String oras;
    private LocalDate dataNastere;
}

