package com.example.Project.model.actor;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Distributie {
    private Long idFilm;
    private Long idActor;
    private RolActor role;
}
