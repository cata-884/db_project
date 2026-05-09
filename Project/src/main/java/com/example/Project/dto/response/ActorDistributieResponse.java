package com.example.Project.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ActorDistributieResponse {
    private Long idActor;
    private String numeScena;
    private String nume;
    private String prenume;
    private String rol;
}
