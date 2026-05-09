package com.example.Project.controller;

import com.example.Project.model.recenzie.EtichetaRecord;
import com.example.Project.service.RecenzieEtichetaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recenzii")
public class RecenzieEtichetaController {

    private RecenzieEtichetaService recenzieEtichetaService;

    @PostMapping("/{idRecenzie}/etichete/{idEticheta}")
    public ResponseEntity<Void> addEticheta(@PathVariable Long idRecenzie, @PathVariable Long idEticheta) {
        recenzieEtichetaService.addEticheta(idRecenzie, idEticheta);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{idRecenzie}/etichete")
    public List<EtichetaRecord> getEtichete(@PathVariable Long idRecenzie) {
        return recenzieEtichetaService.getEtichete(idRecenzie);
    }

    @DeleteMapping("/{idRecenzie}/etichete/{idEticheta}")
    public ResponseEntity<Void> removeEticheta(@PathVariable Long idRecenzie, @PathVariable Long idEticheta) {
        recenzieEtichetaService.removeEticheta(idRecenzie, idEticheta);
        return ResponseEntity.noContent().build();
    }
}
