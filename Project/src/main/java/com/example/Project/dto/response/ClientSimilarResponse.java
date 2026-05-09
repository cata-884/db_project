package com.example.Project.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ClientSimilarResponse {
    private Long id;
    private String numeComplet;
    private Double scorSimilaritate;
}
