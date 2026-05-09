package com.example.Project.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GrupareClientiResponse {
    private Long idGrupa;
    private Long idClient;
    private String numeComplet;
}
