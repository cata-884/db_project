package com.example.Project.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateRecenzieRequest {
    private Long idClient;
    private Long idFilm;
    private Integer nota;
    private String textComentariu;
    // sentiment si dataPostare sunt generate automat (trigger + DEFAULT)
}
