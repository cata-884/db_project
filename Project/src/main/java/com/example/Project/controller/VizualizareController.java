package com.example.Project.controller;

import com.example.Project.config.CurrentUser;
import com.example.Project.dto.request.CreateVizualizareRequest;
import com.example.Project.dto.response.IstoricVizionareResponse;
import com.example.Project.model.client.Vizualizari;
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

    @GetMapping
    public List<Vizualizari> getAll() {
        return vizualizareService.getAll();
    }

    @GetMapping("/{id}")
    public Vizualizari getById(@PathVariable Long id) {
        return vizualizareService.getById(id);
    }

    @PostMapping
    public ResponseEntity<Vizualizari> create(HttpServletRequest req, @RequestBody CreateVizualizareRequest body) {
        body.setIdClient(CurrentUser.getId(req));
        return ResponseEntity.status(HttpStatus.CREATED).body(vizualizareService.create(body));
    }

    @PutMapping("/{id}")
    public Vizualizari update(@PathVariable Long id, @RequestBody Vizualizari viz) {
        return vizualizareService.update(id, viz);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        vizualizareService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public List<IstoricVizionareResponse> getIstoricForMe(HttpServletRequest req) {
        return vizualizareService.getIstoricByClient(CurrentUser.getId(req));
    }
}
