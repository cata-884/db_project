package com.example.Project.model.client;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Reprezinta un numar de telefon format din codul de tara si numarul local.
 * Folosit pentru stocarea telefoanelor fix si mobil ale clientilor.
 */
@Getter
@AllArgsConstructor
public class PhoneNumber {
    /** Codul de tara (prefix international). */
    private CountryCode code;
    /** Numarul local al telefonului, fara prefix. */
    private String number;
}
