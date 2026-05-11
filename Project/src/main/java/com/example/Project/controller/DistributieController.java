package com.example.Project.controller;

import com.example.Project.dto.request.CreateDistributieRequest;
import com.example.Project.dto.response.ActorDistributieResponse;
import com.example.Project.model.actor.Distributie;
import com.example.Project.service.DistributieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/distributie")
public class DistributieController {

    @Autowired
    private DistributieService distributieService;

    @GetMapping("/film/{idFilm}")
    public List<ActorDistributieResponse> getActoriByFilm(@PathVariable Long idFilm) {
        return distributieService.getActoriByFilm(idFilm);
    }
}
