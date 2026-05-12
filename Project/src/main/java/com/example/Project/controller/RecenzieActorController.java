package com.example.Project.controller;

import com.example.Project.model.recenzie.RecenziiActori;
import com.example.Project.service.RecenzieActorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST pentru consultarea comentariilor despre actori asociate unei recenzii.
 * Expune resursa {@code /api/recenzii-actori}.
 */
@RestController
@RequestMapping("/api/recenzii-actori")
public class RecenzieActorController {

    @Autowired
    private RecenzieActorService recenzieActorService;

    /**
     * Returneaza comentariile despre actori asociate unei recenzii specifice.
     * @param idRecenzie Identificatorul recenziei.
     * @return Lista comentariilor despre actori din recenzia data.
     */
    @GetMapping("/recenzie/{idRecenzie}")
    public List<RecenziiActori> getByRecenzie(@PathVariable Long idRecenzie) {
        return recenzieActorService.getByRecenzie(idRecenzie);
    }
}
