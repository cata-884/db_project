package com.example.Project.controller;

import com.example.Project.dto.response.ActorDistributieResponse;
import com.example.Project.model.actor.Actor;
import com.example.Project.service.ActorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST pentru operatiile de citire asupra actorilor.
 * Expune resursa {@code /api/actori}.
 */
@RestController
@RequestMapping("/api/actori")
public class ActorController {

    @Autowired
    private ActorService actorService;

    /**
     * Returneaza toti actorii din catalog.
     * @return Lista tuturor actorilor, ordonata dupa ID.
     */
    @GetMapping
    public List<Actor> getAll() {
        return actorService.getAll();
    }

    /**
     * Returneaza un actor dupa ID.
     * @param id Identificatorul actorului.
     * @return Actorul gasit.
     */
    @GetMapping("/{id}")
    public Actor getById(@PathVariable Long id) {
        return actorService.getById(id);
    }

    /**
     * Returneaza actorii dintr-un film, impreuna cu rolul jucat de fiecare.
     * @param idFilm Identificatorul filmului.
     * @return Lista actorilor cu rolurile lor in filmul specificat.
     */
    @GetMapping("/film/{idFilm}")
    public List<ActorDistributieResponse> getByFilm(@PathVariable Long idFilm) {
        return actorService.getByFilm(idFilm);
    }
}
