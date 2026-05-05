package com.example.Project.model.recenzie;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RecenziiActori {
    private Long idRecenzie;
    private Long idActor;
    private String comentariu;
}
