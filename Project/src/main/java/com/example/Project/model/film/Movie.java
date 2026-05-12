package com.example.Project.model.film;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Entitate ce reprezinta un film din catalog (tabelul {@code filme}).
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Movie {
    /** Identificatorul unic al filmului. */
    private Long id;
    /** Titlul filmului. */
    private String titlu;
    /** Descrierea/sinopsisul filmului; poate fi {@code null}. */
    private String descriere;
    /** Data lansarii oficiale; poate fi {@code null}. */
    private LocalDate dataLansare;
    /** Identificatorul categoriei careia ii apartine filmul. */
    private Long idCategorie;
    /** Ratingul mediu calculat; poate fi {@code null} daca nu exista recenzii. */
    private Double rating;
}
