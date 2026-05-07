package com.example.Project.model.recenzie;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EtichetaRecord {
    private Long id;
    private Eticheta denumire;
    private String sentiment;  // "POZITIV" / "NEGATIV" / "NEUTRU"
}
