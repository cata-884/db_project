package com.example.Project.controller;

import com.example.Project.dto.request.CreateDistributieRequest;
import com.example.Project.dto.response.ActorDistributieResponse;
import com.example.Project.model.actor.Distributie;
import com.example.Project.service.DistributieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/distributie")
public class DistributieController {

    private DistributieService distributieService;

    @PostMapping
    public ResponseEntity<Distributie> create(@RequestBody CreateDistributieRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(distributieService.create(req));
    }

    @GetMapping("/film/{idFilm}")
    public List<ActorDistributieResponse> getActoriByFilm(@PathVariable Long idFilm) {
        return distributieService.getActoriByFilm(idFilm);
    }

    @DeleteMapping("/{idFilm}/{idActor}")
    public ResponseEntity<Void> delete(@PathVariable Long idFilm, @PathVariable Long idActor) {
        distributieService.delete(idFilm, idActor);
        return ResponseEntity.noContent().build();
    }
}
