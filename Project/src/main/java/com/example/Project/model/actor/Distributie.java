package com.example.Project.model.actor;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Entitate ce reprezinta legatura dintre un film si un actor (tabelul {@code distributie}).
 * Stocheaza rolul jucat de actor in filmul respectiv.
 */
@Data
@AllArgsConstructor
public class Distributie {
    /** Identificatorul filmului din distributie. */
    private Long idFilm;
    /** Identificatorul actorului din distributie. */
    private Long idActor;
    /** Tipul de rol jucat de actor in film. */
    private RolActor role;
}
