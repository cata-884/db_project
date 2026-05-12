package com.example.Project.model.recenzie;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Entitate ce reprezinta o recenzie scrisa de un client pentru un film (tabelul {@code recenzii}).
 * Sentimentul este calculat automat de triggerul {@code trg_set_sentiment_recenzie} la INSERT.
 */
@Data
@AllArgsConstructor
public class Recenzii {
    /** Identificatorul unic al recenziei. */
    private Long id;
    /** Identificatorul clientului care a scris recenzia. */
    private Long idClient;
    /** Identificatorul filmului recenzat. */
    private Long idFilm;
    /** Nota acordata filmului, intre 1 si 10. */
    private Integer nota;
    /** Sentimentul calculat de trigger: {@code "POZITIV"}, {@code "NEGATIV"} sau {@code "NEUTRU"}. */
    private String sentiment;
    /** Textul comentariului; poate fi {@code null}. */
    private String textComentariu;
    /** Data si ora la care a fost postata recenzia. */
    private LocalDateTime dataPostare;
}
