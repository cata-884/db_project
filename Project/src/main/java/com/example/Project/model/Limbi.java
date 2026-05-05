package com.example.Project.model;

public enum Limbi{
    ROMANIAN("romana"),
    ENGLISH("engleza"),
    GERMAN("germana"),
    RUSSIAN("rusa"),
    SPANISH("spaniola"),
    OTHER("neidentificata");

    final String nume;

    Limbi(String nume) {
        this.nume = nume;
    }

    @Override
    public String toString() {
        return "Limba" + nume;
    }
}