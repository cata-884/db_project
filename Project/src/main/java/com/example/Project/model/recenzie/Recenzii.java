package com.example.Project.model.recenzie;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class Recenzii {
    private Long id;
    private Long idClient;
    private Long idFilm;
    private Integer nota;
    private String sentiment;
    private String textComentariu;
    private LocalDateTime dataPostare;
}
