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

@RestController
@RequestMapping("/api/vizualizari")
public class VizualizareController {

    @Autowired
    private VizualizareService vizualizareService;

    @Autowired
    private OwnershipService ownershipService;

    @GetMapping
    public List<Vizualizari> getAll() {
        return vizualizareService.getAll();
    }

    @GetMapping("/{id}")
    public Vizualizari getById(HttpServletRequest req, @PathVariable Long id) {
        ownershipService.verificaVizualizare(id, CurrentUser.getId(req));
        return vizualizareService.getById(id);
    }

    @PostMapping
    public ResponseEntity<Vizualizari> create(HttpServletRequest req, @RequestBody CreateVizualizareRequest body) {
        Long idClient = CurrentUser.getId(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(vizualizareService.create(idClient, body));
    }

    @PutMapping("/{id}")
    public Vizualizari update(HttpServletRequest req,
                              @PathVariable Long id,
                              @RequestBody UpdateVizualizareRequest body) {
        ownershipService.verificaVizualizare(id, CurrentUser.getId(req));
        return vizualizareService.updateProgress(id, body);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(HttpServletRequest req, @PathVariable Long id) {
        ownershipService.verificaVizualizare(id, CurrentUser.getId(req));
        vizualizareService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public List<IstoricVizionareResponse> getIstoricForMe(HttpServletRequest req) {
        return vizualizareService.getIstoricByClient(CurrentUser.getId(req));
    }
}
