package com.example.Project.model;

public enum Rezolutie {
    TINY("144p"),
    LOW("240p"),
    SD( "360p"),
    DVD( "480p"),
    HD( "720p"),
    FULL_HD( "1080p"),
    QHD( "1440p"),
    UHD( "4K"),
    UHD_8K( "8K");

    private final String label;

    Rezolutie(String label) {
        this.label = label;
    }

    public String getLabel() { return label; }

    @Override
    public String toString() {
        return "Rezolutie de" + label;
    }
}
