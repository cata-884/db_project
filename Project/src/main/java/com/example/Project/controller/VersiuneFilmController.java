package com.example.Project.controller;

import com.example.Project.model.film.VersiuneFilm;
import com.example.Project.service.VersiuneFilmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/versiuni-film")
public class VersiuneFilmController {

    @Autowired
    private VersiuneFilmService versiuneFilmService;

    @GetMapping("/film/{idFilm}")
    public List<VersiuneFilm> getByFilmId(@PathVariable Long idFilm) {
        return versiuneFilmService.getByFilmId(idFilm);
    }
}

