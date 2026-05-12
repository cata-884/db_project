package com.example.Project.model.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDate;

/**
 * Entitate ce reprezinta o inregistrare de vizualizare a unui film de catre un client
 * (tabelul {@code vizualizari}).
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Vizualizari {
    /** Identificatorul unic al vizualizarii. */
    private Long id;
    /** Identificatorul clientului care a vizionat filmul. */
    private Long idClient;
    /** Identificatorul versiunii de film vizionate (rezolutie/format). */
    private Long idVersiune;
    /** Data la care a avut loc vizionarea. */
    private LocalDate dataVizualizare;
    /** Durata vizionarii in minute; poate fi {@code null}. */
    private Integer durata;
    /** Starea curenta a vizionarii; poate fi {@code null}. */
    private StareVizualizare stare;
}
