package com.example.Project.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CountryCode {
    ROMANIA("+40", "Romania"),
    USA("+1", "SUA"),
    GERMANY("+49", "Germania"),
    UK("+44", "Marea Britanie"),
    OTHER("00", "Necunoscut");

    private final String prefix;
    private final String country;
}