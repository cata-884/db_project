package com.example.Project.controller;

import com.example.Project.model.film.Categorie;
import com.example.Project.service.CategorieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categorii")
public class CategorieController {

    @Autowired
    private CategorieService categorieService;

    @GetMapping
    public List<Categorie> getAll() {
        return categorieService.getAll();
    }

    @GetMapping("/{id}")
    public Categorie getById(@PathVariable Long id) {
        return categorieService.getById(id);
    }

    @PostMapping
    public ResponseEntity<Categorie> create(@RequestBody Categorie categorie) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categorieService.create(categorie));
    }

    @PutMapping("/{id}")
    public Categorie update(@PathVariable Long id, @RequestBody Categorie categorie) {
        return categorieService.update(id, categorie);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categorieService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
