package com.example.Project.dto.request;

import com.example.Project.model.client.StareVizualizare;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UpdateVizualizareRequest {
    private Integer durata;
    private StareVizualizare stare;
}
