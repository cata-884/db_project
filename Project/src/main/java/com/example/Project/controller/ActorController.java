package com.example.Project.controller;

import com.example.Project.dto.response.ActorDistributieResponse;
import com.example.Project.model.actor.Actor;
import com.example.Project.service.ActorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @PostMapping
    public ResponseEntity<Actor> create(@RequestBody Actor actor) {
        return ResponseEntity.status(HttpStatus.CREATED).body(actorService.create(actor));
    }

    @PutMapping("/{id}")
    public Actor update(@PathVariable Long id, @RequestBody Actor actor) {
        return actorService.update(id, actor);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        actorService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/film/{idFilm}")
    public List<ActorDistributieResponse> getByFilm(@PathVariable Long idFilm) {
        return actorService.getByFilm(idFilm);
    }
}
