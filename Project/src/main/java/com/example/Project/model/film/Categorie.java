package com.example.Project.model.film;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Categorie {
    private Long id;
    private String nume;
    /* Actiune, drama, comedie, etc */
}
