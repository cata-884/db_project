package com.example.Project.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class RecenzieDetailResponse {
    private Long id;
    private Long idClient;
    private String numeClient;
    private Long idFilm;
    private String titluFilm;
    private Integer nota;
    private String sentiment;
    private String textComentariu;
    private LocalDateTime dataPostare;
}
