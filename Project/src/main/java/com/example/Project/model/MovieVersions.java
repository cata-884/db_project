package com.example.Project.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor

public class MovieVersions {
    Long id;
    Long idFilm;
    Rezolutie rezolutie;
    Limbi limbi;
    Format format;
}
