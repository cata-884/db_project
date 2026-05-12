package com.example.Project.model.film;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entitate ce reprezinta o categorie de film (tabelul {@code categorii}).
 * Exemple: Actiune, Drama, Comedie.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Categorie {
    /** Identificatorul unic al categoriei. */
    private Long id;
    /** Denumirea categoriei (ex. Actiune, Drama, Comedie). */
    private String nume;
}
