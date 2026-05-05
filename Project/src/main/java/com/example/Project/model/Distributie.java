package com.example.Project.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor

public class Distributie {
    private Long idFilm;
    private Long idActor;
    RolActor role;
    String comentariu; //comentariu asupra rolului jucat de actor in film
}
