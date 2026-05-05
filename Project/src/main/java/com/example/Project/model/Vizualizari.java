package com.example.Project.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class Vizualizari {
    Long id;
    Long idClient;
    Long idVersiune;
    LocalDate dataVizualizare;
    Float durata;
    Float stare;
}
