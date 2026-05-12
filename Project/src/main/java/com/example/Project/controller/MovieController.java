package com.example.Project.controller;

import com.example.Project.model.film.Movie;
import com.example.Project.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST pentru consultarea filmelor din catalog.
 * Expune resursa {@code /api/filme}.
 */
@RestController
@RequestMapping("/api/filme")
public class MovieController {

    @Autowired
    private MovieService movieService;

    /**
     * Returneaza toate filmele din catalog.
     * @return Lista filmelor, ordonata dupa ID.
     */
    @GetMapping
    public List<Movie> getAll() {
        return movieService.getAll();
    }

    /**
     * Returneaza un film dupa ID.
     * @param id Identificatorul filmului.
     * @return Filmul gasit.
     */
    @GetMapping("/{id}")
    public Movie getById(@PathVariable Long id) {
        return movieService.getById(id);
    }

    /**
     * Returneaza filmele dintr-o categorie specifica, ordonate descrescator dupa rating.
     * @param idCategorie Identificatorul categoriei.
     * @return Lista filmelor din categoria data.
     */
    @GetMapping("/categorie/{idCategorie}")
    public List<Movie> getByCategorie(@PathVariable Long idCategorie) {
        return movieService.getByCategorie(idCategorie);
    }

}
