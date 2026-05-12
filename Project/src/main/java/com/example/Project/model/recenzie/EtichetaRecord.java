package com.example.Project.model.recenzie;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Entitate ce reprezinta o eticheta din baza de date (tabelul {@code etichete}).
 * Contine tipul etichetei si clasificarea sa sentimentala.
 */
@Data
@AllArgsConstructor
public class EtichetaRecord {
    /** Identificatorul unic al etichetei. */
    private Long id;
    /** Tipul etichetei reprezentat ca valoare a enum-ului {@link Eticheta}. */
    private Eticheta denumire;
    /** Clasificarea sentimentala a etichetei: {@code "POZITIV"}, {@code "NEGATIV"} sau {@code "NEUTRU"}. */
    private String sentiment;
}
