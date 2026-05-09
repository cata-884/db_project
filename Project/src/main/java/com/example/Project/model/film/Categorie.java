package com.example.Project.model.film;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Categorie {
    private Long id;
    private String nume;
    /* Actiune, drama, comedie, etc */
}
