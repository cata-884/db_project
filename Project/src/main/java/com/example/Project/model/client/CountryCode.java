package com.example.Project.model.client;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Codurile de tara suportate pentru numerele de telefon.
 * Fiecare valoare contine prefixul international si numele tarii.
 */
@Getter
@AllArgsConstructor
public enum CountryCode {
    ROMANIA("+40", "Romania"),
    USA("+1", "SUA"),
    GERMANY("+49", "Germania"),
    UK("+44", "Marea Britanie"),
    OTHER("00", "Necunoscut");

    /** Prefixul international al tarii (ex. {@code "+40"}). */
    private final String prefix;
    /** Numele tarii in limba romana. */
    private final String country;
}
