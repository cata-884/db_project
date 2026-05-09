package com.example.Project.dto.request;

import com.example.Project.model.client.StareVizualizare;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateVizualizareRequest {
    private Long idClient;
    private Long idVersiune;
    private Float durata;
    private StareVizualizare stare;
    // dataVizualizare nu se trimite — se foloseste CURRENT_DATE la INSERT
}
