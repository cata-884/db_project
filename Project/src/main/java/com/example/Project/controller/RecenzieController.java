package com.example.Project.controller;

import com.example.Project.dto.request.CreateRecenzieRequest;
import com.example.Project.dto.request.UpdateRecenzieRequest;
import com.example.Project.dto.response.RecenzieDetailResponse;
import com.example.Project.dto.response.RecenzieResponse;
import com.example.Project.service.RecenzieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recenzii")
public class RecenzieController {

    @Autowired
    private RecenzieService recenzieService;

    @GetMapping
    public List<RecenzieResponse> getAll() {
        return recenzieService.getAll();
    }

    @GetMapping("/{id}")
    public RecenzieResponse getById(@PathVariable Long id) {
        return recenzieService.getById(id);
    }

    @PostMapping
    public ResponseEntity<RecenzieDetailResponse> create(@RequestBody CreateRecenzieRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(recenzieService.create(req));
    }

    @PutMapping("/{id}")
    public RecenzieResponse update(@PathVariable Long id, @RequestBody UpdateRecenzieRequest req) {
        return recenzieService.update(id, req);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        recenzieService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/film/{idFilm}")
    public List<RecenzieDetailResponse> getByFilm(@PathVariable Long idFilm) {
        return recenzieService.getByFilm(idFilm);
    }

    @GetMapping("/client/{idClient}")
    public List<RecenzieResponse> getByClient(@PathVariable Long idClient) {
        return recenzieService.getByClient(idClient);
    }
}
