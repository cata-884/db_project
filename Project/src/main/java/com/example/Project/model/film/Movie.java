package com.example.Project.model.film;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Movie {
    private Long id;
    private String titlu;
    private String descriere;
    private LocalDate dataLansare;
    private Long idCategorie;
    private Double rating;
}
