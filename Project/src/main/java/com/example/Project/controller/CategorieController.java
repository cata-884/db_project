package com.example.Project.controller;

import com.example.Project.model.film.Categorie;
import com.example.Project.service.CategorieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST pentru operatiile de citire asupra categoriilor de filme.
 * Expune resursa {@code /api/categorii}.
 */
@RestController
@RequestMapping("/api/categorii")
public class CategorieController {

    @Autowired
    private CategorieService categorieService;

    /**
     * Returneaza toate categoriile disponibile.
     * @return Lista categoriilor, ordonata dupa ID.
     */
    @GetMapping
    public List<Categorie> getAll() {
        return categorieService.getAll();
    }

    /**
     * Returneaza o categorie dupa ID.
     * @param id Identificatorul categoriei.
     * @return Categoria gasita.
     */
    @GetMapping("/{id}")
    public Categorie getById(@PathVariable Long id) {
        return categorieService.getById(id);
    }

}
