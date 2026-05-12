package com.example.Project.controller;

import com.example.Project.model.recenzie.EtichetaRecord;
import com.example.Project.service.EtichetaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST pentru consultarea etichetelor disponibile.
 * Expune resursa {@code /api/etichete}.
 */
@RestController
@RequestMapping("/api/etichete")
public class EtichetaController {

    @Autowired
    private EtichetaService etichetaService;

    /**
     * Returneaza toate etichetele disponibile, ordonate dupa sentiment si denumire.
     * @return Lista completa de etichete.
     */
    @GetMapping
    public List<EtichetaRecord> getAll() {
        return etichetaService.getAll();
    }

    /**
     * Returneaza o eticheta dupa ID.
     * @param id Identificatorul etichetei.
     * @return Eticheta gasita.
     */
    @GetMapping("/{id}")
    public EtichetaRecord getById(@PathVariable Long id) {
        return etichetaService.getById(id);
    }
}
