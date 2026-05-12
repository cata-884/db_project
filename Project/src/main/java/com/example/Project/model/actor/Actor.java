package com.example.Project.model.actor;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Entitate ce reprezinta un actor din baza de date (tabelul {@code actori}).
 * Contine datele de identificare ale actorului: numele de scena, numele real si data nasterii.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Actor {
    /** Identificatorul unic generat de baza de date. */
    private Long id;
    /** Numele artistic/de scena al actorului. */
    private String numeScena;
    /** Numele de familie real al actorului. */
    private String nume;
    /** Prenumele real al actorului. */
    private String prenume;
    /** Data nasterii; poate fi {@code null} daca nu este cunoscuta. */
    private LocalDate dataNastere;
}
