package com.example.Project.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class Movie {
    Long id;
    String titlu;
    String descriere;
    LocalDate dataLansare;
    Long idCategorie;
    Byte rating; //1-10
}
