package com.example.Project.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PredictieSezoneraResponse {
    private Long id;
    private String titlu;
    private String categorie;
    private Double rating;
    private Double factorSezonier;
    private Double scorPredictie;
}
