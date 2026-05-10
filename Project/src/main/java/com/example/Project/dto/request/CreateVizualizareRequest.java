package com.example.Project.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateVizualizareRequest {
    private Long idClient;
    private Long idVersiune;
    // dataVizualizare nu se trimite — se foloseste CURRENT_DATE la INSERT
}
