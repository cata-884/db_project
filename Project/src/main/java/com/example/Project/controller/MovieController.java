package com.example.Project.controller;

import com.example.Project.model.film.Movie;
import com.example.Project.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/filme")
public class MovieController {

    @Autowired
    private MovieService movieService;

    @GetMapping
    public List<Movie> getAll() {
        return movieService.getAll();
    }

    @GetMapping("/{id}")
    public Movie getById(@PathVariable Long id) {
        return movieService.getById(id);
    }

    @GetMapping("/categorie/{idCategorie}")
    public List<Movie> getByCategorie(@PathVariable Long idCategorie) {
        return movieService.getByCategorie(idCategorie);
    }

}
