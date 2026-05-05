package com.example.Project.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class Actori {
    private Long id;
    private String numeScena;
    private String nume;
    private String prenume;
    LocalDate dataNastere;
}
