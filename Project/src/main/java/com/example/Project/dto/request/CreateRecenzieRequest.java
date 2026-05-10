package com.example.Project.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class CreateRecenzieRequest {
    private Long idClient;
    private Long idFilm;
    private Integer nota;
    private String textComentariu;
    private List<Long> etichetaIds;
    private List<Long> actoriIds;
    private List<String> actoriComentarii;
}
