package com.example.Project.model.recenzie;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Entitate ce reprezinta un comentariu specific despre un actor in contextul unei recenzii
 * (tabelul {@code recenzii_actori}).
 */
@Data
@AllArgsConstructor
public class RecenziiActori {
    /** Identificatorul recenziei parinte. */
    private Long idRecenzie;
    /** Identificatorul actorului comentat. */
    private Long idActor;
    /** Textul comentariului despre actor; poate fi {@code null}. */
    private String comentariu;
}
