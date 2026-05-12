package com.example.Project.model.actor;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Tipurile de rol pe care un actor le poate juca intr-un film.
 * Fiecare valoare are o eticheta afisabila in interfata.
 */
@Getter
@AllArgsConstructor
public enum RolActor {
    PROTAGONIST("Principal"),
    ANTAGONIST("Antagonist"),
    SECUNDAR("Secundar"),
    EPISODIC("Episodic"),
    DUBLURA("Dublură");

    /** Eticheta afisabila in interfata pentru acest tip de rol. */
    private final String eticheta;
}
