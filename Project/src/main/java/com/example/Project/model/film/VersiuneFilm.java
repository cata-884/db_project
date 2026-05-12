package com.example.Project.model.film;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entitate ce reprezinta o versiune specifica a unui film (tabelul {@code versiuni_film}).
 * O versiune descrie combinatia rezolutie/limbi/format pentru un film dat.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VersiuneFilm {
    /** Identificatorul unic al versiunii. */
    private Long id;
    /** Identificatorul filmului parinte. */
    private Long idFilm;
    /** Rezolutia versiunii (ex. 1080p, 4K). */
    private String rezolutie;
    /** Limbile audio disponibile in aceasta versiune. */
    private String limbi;
    /** Formatul fisierului (ex. MP4, MKV). */
    private String format;
}

