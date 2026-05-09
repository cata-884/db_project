package com.example.Project.model.film;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VersiuneFilm {
    private Long id;
    private Long idFilm;
    private String rezolutie;
    private String limbi;
    private String format;
}

