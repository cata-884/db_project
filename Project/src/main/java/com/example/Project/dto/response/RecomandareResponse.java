package com.example.Project.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class RecomandareResponse {
    private Long id;
    private String titlu;
    private String descriere;
    private Double rating;
    private String categorie;
    private LocalDate dataLansare;
}
