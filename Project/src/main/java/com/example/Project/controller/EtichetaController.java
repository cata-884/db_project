package com.example.Project.controller;

import com.example.Project.model.recenzie.EtichetaRecord;
import com.example.Project.service.EtichetaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/etichete")
public class EtichetaController {

    private EtichetaService etichetaService;

    @GetMapping
    public List<EtichetaRecord> getAll() {
        return etichetaService.getAll();
    }

    @GetMapping("/{id}")
    public EtichetaRecord getById(@PathVariable Long id) {
        return etichetaService.getById(id);
    }
}
