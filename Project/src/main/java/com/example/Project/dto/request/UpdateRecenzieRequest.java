package com.example.Project.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UpdateRecenzieRequest {
    private Integer nota;
    private String textComentariu;
}
