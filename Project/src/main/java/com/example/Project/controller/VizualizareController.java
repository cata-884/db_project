package com.example.Project.controller;

import com.example.Project.config.CurrentUser;
import com.example.Project.dto.request.CreateVizualizareRequest;
import com.example.Project.dto.request.UpdateVizualizareRequest;
import com.example.Project.dto.response.IstoricVizionareResponse;
import com.example.Project.model.client.Vizualizari;
import com.example.Project.service.OwnershipService;
import com.example.Project.service.VizualizareService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST pentru gestionarea vizualizarilor de filme.
 * Operatiile de citire/modificare/stergere a unei vizualizari individuale
 * verifica ca aceasta apartine clientului autentificat.
 * Expune resursa {@code /api/vizualizari}.
 */
@RestController
@RequestMapping("/api/vizualizari")
public class VizualizareController {

    @Autowired
    private VizualizareService vizualizareService;

    @Autowired
    private OwnershipService ownershipService;

    /**
     * Returneaza toate vizualizarile din sistem (uz administrativ).
     * @return Lista tuturor vizualizarilor.
     */
    @GetMapping
    public List<Vizualizari> getAll() {
        return vizualizareService.getAll();
    }

    /**
     * Returneaza o vizualizare dupa ID. Clientul poate accesa doar propriile vizualizari.
     * @param req Cererea HTTP cu ID-ul clientului autentificat.
     * @param id  Identificatorul vizualizarii.
     * @return Vizualizarea gasita.
     */
    @GetMapping("/{id}")
    public Vizualizari getById(HttpServletRequest req, @PathVariable Long id) {
        ownershipService.verificaVizualizare(id, CurrentUser.getId(req));
        return vizualizareService.getById(id);
    }

    /**
     * Inregistreaza o noua vizualizare pentru clientul autentificat.
     * @param req  Cererea HTTP cu ID-ul clientului.
     * @param body Datele vizualizarii (ID-ul versiunii de film).
     * @return HTTP 201 Created cu vizualizarea creata.
     */
    @PostMapping
    public ResponseEntity<Vizualizari> create(HttpServletRequest req, @RequestBody CreateVizualizareRequest body) {
        Long idClient = CurrentUser.getId(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(vizualizareService.create(idClient, body));
    }

    /**
     * Actualizeaza progresul (durata si starea) unei vizualizari. Clientul poate modifica doar propriile vizualizari.
     * @param req  Cererea HTTP cu ID-ul clientului autentificat.
     * @param id   Identificatorul vizualizarii de actualizat.
     * @param body Noile valori pentru durata si/sau stare.
     * @return Vizualizarea actualizata.
     */
    @PutMapping("/{id}")
    public Vizualizari update(HttpServletRequest req,
                              @PathVariable Long id,
                              @RequestBody UpdateVizualizareRequest body) {
        ownershipService.verificaVizualizare(id, CurrentUser.getId(req));
        return vizualizareService.updateProgress(id, body);
    }

    /**
     * Sterge o vizualizare. Clientul poate sterge doar propriile vizualizari.
     * @param req Cererea HTTP cu ID-ul clientului autentificat.
     * @param id  Identificatorul vizualizarii de sters.
     * @return HTTP 204 No Content.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(HttpServletRequest req, @PathVariable Long id) {
        ownershipService.verificaVizualizare(id, CurrentUser.getId(req));
        vizualizareService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Returneaza istoricul de vizionare al clientului autentificat.
     * @param req Cererea HTTP cu ID-ul clientului.
     * @return Lista vizionarilor cu detalii despre film si versiune, ordonata descrescator dupa data.
     */
    @GetMapping("/me")
    public List<IstoricVizionareResponse> getIstoricForMe(HttpServletRequest req) {
        return vizualizareService.getIstoricByClient(CurrentUser.getId(req));
    }
}
