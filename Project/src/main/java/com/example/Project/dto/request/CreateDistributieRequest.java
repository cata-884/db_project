package com.example.Project.dto.request;

import com.example.Project.model.actor.RolActor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateDistributieRequest {
    private Long idFilm;
    private Long idActor;
    private RolActor role;
    private String comentariu;
}
