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

/**
 * Controller REST pentru gestionarea etichetelor asociate unei recenzii.
 * Operatiile de modificare verifica ca recenzia apartine clientului autentificat.
 * Expune resursa {@code /api/recenzii-etichete}.
 */
@RestController
@RequestMapping("/api/recenzii-etichete")
public class RecenzieEtichetaController {

    @Autowired
    private RecenzieEtichetaService recenzieEtichetaService;

    @Autowired
    private OwnershipService ownershipService;

    /**
     * Asociaza o eticheta unei recenzii. Doar proprietarul recenziei poate efectua operatia.
     * @param req        Cererea HTTP cu ID-ul clientului autentificat.
     * @param idRecenzie Identificatorul recenziei.
     * @param idEticheta Identificatorul etichetei de asociat.
     * @return HTTP 201 Created.
     */
    @PostMapping("/{idRecenzie}/{idEticheta}")
    public ResponseEntity<Void> addEticheta(HttpServletRequest req,
                                            @PathVariable Long idRecenzie,
                                            @PathVariable Long idEticheta) {
        ownershipService.verificaRecenzie(idRecenzie, CurrentUser.getId(req));
        recenzieEtichetaService.addEticheta(idRecenzie, idEticheta);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * Returneaza etichetele asociate unei recenzii.
     * @param idRecenzie Identificatorul recenziei.
     * @return Lista etichetelor asociate.
     */
    @GetMapping("/{idRecenzie}")
    public List<EtichetaRecord> getEtichete(@PathVariable Long idRecenzie) {
        return recenzieEtichetaService.getEtichete(idRecenzie);
    }

    /**
     * Elimina asocierea dintre o recenzie si o eticheta. Doar proprietarul recenziei poate efectua operatia.
     * @param req        Cererea HTTP cu ID-ul clientului autentificat.
     * @param idRecenzie Identificatorul recenziei.
     * @param idEticheta Identificatorul etichetei de eliminat.
     * @return HTTP 204 No Content.
     */
    @DeleteMapping("/{idRecenzie}/{idEticheta}")
    public ResponseEntity<Void> removeEticheta(HttpServletRequest req,
                                               @PathVariable Long idRecenzie,
                                               @PathVariable Long idEticheta) {
        ownershipService.verificaRecenzie(idRecenzie, CurrentUser.getId(req));
        recenzieEtichetaService.removeEticheta(idRecenzie, idEticheta);
        return ResponseEntity.noContent().build();
    }
}
