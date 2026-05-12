package com.example.Project.controller;

import com.example.Project.dto.response.ActorDistributieResponse;
import com.example.Project.service.DistributieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST pentru consultarea distributiei unui film.
 * Expune resursa {@code /api/distributie}.
 */
@RestController
@RequestMapping("/api/distributie")
public class DistributieController {

    @Autowired
    private DistributieService distributieService;

    /**
     * Returneaza actorii si rolurile lor dintr-un film specificat.
     * @param idFilm Identificatorul filmului.
     * @return Lista actorilor cu rolul jucat in filmul dat.
     */
    @GetMapping("/film/{idFilm}")
    public List<ActorDistributieResponse> getActoriByFilm(@PathVariable Long idFilm) {
        return distributieService.getActoriByFilm(idFilm);
    }
}
