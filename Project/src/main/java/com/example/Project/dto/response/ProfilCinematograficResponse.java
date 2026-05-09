package com.example.Project.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProfilCinematograficResponse {
    private String categoriePreferata;
    private String actorPreferat;
    private Double ratingMediu;
    private Long totalFilmeVazute;
    private Long totalRecenzii;
    private String sentimentDominant;
}
