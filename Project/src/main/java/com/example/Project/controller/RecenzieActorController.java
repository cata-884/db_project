package com.example.Project.controller;

import com.example.Project.dto.request.CreateRecenzieActorRequest;
import com.example.Project.model.recenzie.RecenziiActori;
import com.example.Project.service.RecenzieActorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recenzii-actori")
public class RecenzieActorController {

    private RecenzieActorService recenzieActorService;

    @PostMapping
    public ResponseEntity<RecenziiActori> create(@RequestBody CreateRecenzieActorRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(recenzieActorService.create(req));
    }

    @GetMapping("/recenzie/{idRecenzie}")
    public List<RecenziiActori> getByRecenzie(@PathVariable Long idRecenzie) {
        return recenzieActorService.getByRecenzie(idRecenzie);
    }

    @DeleteMapping("/{idRecenzie}/{idActor}")
    public ResponseEntity<Void> delete(@PathVariable Long idRecenzie, @PathVariable Long idActor) {
        recenzieActorService.delete(idRecenzie, idActor);
        return ResponseEntity.noContent().build();
    }
}
