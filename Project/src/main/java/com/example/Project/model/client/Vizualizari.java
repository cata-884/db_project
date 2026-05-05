package com.example.Project.model.client;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class Vizualizari {
    private Long id;
    private Long idClient;
    private Long idVersiune;
    private LocalDate dataVizualizare;
    private Float durata;
    private StareVizualizare stare;
}
