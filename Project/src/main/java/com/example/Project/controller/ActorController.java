package com.example.Project.controller;

import com.example.Project.dto.response.ActorDistributieResponse;
import com.example.Project.model.actor.Actor;
import com.example.Project.service.ActorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/actori")
public class ActorController {

    @Autowired
    private ActorService actorService;

    @GetMapping
    public List<Actor> getAll() {
        return actorService.getAll();
    }

    @GetMapping("/{id}")
    public Actor getById(@PathVariable Long id) {
        return actorService.getById(id);
    }

    @GetMapping("/film/{idFilm}")
    public List<ActorDistributieResponse> getByFilm(@PathVariable Long idFilm) {
        return actorService.getByFilm(idFilm);
    }
}
