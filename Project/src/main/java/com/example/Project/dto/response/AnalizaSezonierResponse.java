package com.example.Project.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AnalizaSezonierResponse {
    private Integer luna;
    private String categorie;
    private Long nrVizionari;
    private Long rankCategorie;
}
