package com.example.Project.controller;

import com.example.Project.config.CurrentUser;
import com.example.Project.dto.request.CreateRecenzieActorRequest;
import com.example.Project.model.recenzie.RecenziiActori;
import com.example.Project.service.OwnershipService;
import com.example.Project.service.RecenzieActorService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recenzii-actori")
public class RecenzieActorController {

    @Autowired
    private RecenzieActorService recenzieActorService;

    @Autowired
    private OwnershipService ownershipService;

    @PostMapping
    public ResponseEntity<RecenziiActori> create(HttpServletRequest httpReq, @RequestBody CreateRecenzieActorRequest req) {
        ownershipService.verificaRecenzie(req.getIdRecenzie(), CurrentUser.getId(httpReq));
        return ResponseEntity.status(HttpStatus.CREATED).body(recenzieActorService.create(req));
    }

    @GetMapping("/recenzie/{idRecenzie}")
    public List<RecenziiActori> getByRecenzie(@PathVariable Long idRecenzie) {
        return recenzieActorService.getByRecenzie(idRecenzie);
    }

    @DeleteMapping("/{idRecenzie}/{idActor}")
    public ResponseEntity<Void> delete(HttpServletRequest req, @PathVariable Long idRecenzie, @PathVariable Long idActor) {
        ownershipService.verificaRecenzie(idRecenzie, CurrentUser.getId(req));
        recenzieActorService.delete(idRecenzie, idActor);
        return ResponseEntity.noContent().build();
    }
}
