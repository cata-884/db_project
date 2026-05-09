package com.example.Project.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class IstoricVizionareResponse {
    private Long idVizualizare;
    private Long idFilm;
    private String titluFilm;
    private String denumireVersiune;
    private LocalDate dataVizualizare;
    private Float durata;
    private String stare;
}
