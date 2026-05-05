package com.example.Project.model.film;

import com.example.Project.model.shared.Limbi;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MovieVersions {
    private Long id;
    private Long idFilm;
    private String denumireVersiune;
    private Rezolutie rezolutie;
    private Limbi limbi;
    private Format format;
}
