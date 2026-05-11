package com.example.Project.controller;

import com.example.Project.config.CurrentUser;
import com.example.Project.model.recenzie.EtichetaRecord;
import com.example.Project.service.OwnershipService;
import com.example.Project.service.RecenzieEtichetaService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recenzii-etichete")
public class RecenzieEtichetaController {

    @Autowired
    private RecenzieEtichetaService recenzieEtichetaService;

    @Autowired
    private OwnershipService ownershipService;

    @PostMapping("/{idRecenzie}/{idEticheta}")
    public ResponseEntity<Void> addEticheta(HttpServletRequest req,
                                            @PathVariable Long idRecenzie,
                                            @PathVariable Long idEticheta) {
        ownershipService.verificaRecenzie(idRecenzie, CurrentUser.getId(req));
        recenzieEtichetaService.addEticheta(idRecenzie, idEticheta);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{idRecenzie}")
    public List<EtichetaRecord> getEtichete(@PathVariable Long idRecenzie) {
        return recenzieEtichetaService.getEtichete(idRecenzie);
    }

    @DeleteMapping("/{idRecenzie}/{idEticheta}")
    public ResponseEntity<Void> removeEticheta(HttpServletRequest req,
                                               @PathVariable Long idRecenzie,
                                               @PathVariable Long idEticheta) {
        ownershipService.verificaRecenzie(idRecenzie, CurrentUser.getId(req));
        recenzieEtichetaService.removeEticheta(idRecenzie, idEticheta);
        return ResponseEntity.noContent().build();
    }
}
