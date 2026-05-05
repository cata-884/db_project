package com.example.Project.model.actor;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class Actor {
    private Long id;
    private String numeScena;
    private String nume;
    private String prenume;
    private LocalDate dataNastere;
}
