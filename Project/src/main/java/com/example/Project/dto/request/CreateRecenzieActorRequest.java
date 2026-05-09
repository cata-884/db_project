package com.example.Project.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateRecenzieActorRequest {
    private Long idRecenzie;
    private Long idActor;
    private String comentariu;
}
