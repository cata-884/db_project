package com.example.Project.model.actor;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Actor {
    private Long id;
    private String numeScena;
    private String nume;
    private String prenume;
    private LocalDate dataNastere;
}
