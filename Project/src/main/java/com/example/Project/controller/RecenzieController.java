package com.example.Project.controller;

import com.example.Project.config.CurrentUser;
import com.example.Project.dto.request.CreateRecenzieRequest;
import com.example.Project.dto.request.UpdateRecenzieRequest;
import com.example.Project.dto.response.RecenzieDetailResponse;
import com.example.Project.dto.response.RecenzieResponse;
import com.example.Project.service.OwnershipService;
import com.example.Project.service.RecenzieService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST pentru gestionarea recenziilor de filme.
 * Operatiile de modificare (PUT, DELETE) verifica ca recenzia apartine clientului autentificat.
 * Expune resursa {@code /api/recenzii}.
 */
@RestController
@RequestMapping("/api/recenzii")
public class RecenzieController {

    @Autowired
    private RecenzieService recenzieService;

    @Autowired
    private OwnershipService ownershipService;

    /**
     * Returneaza toate recenziile din sistem.
     * @return Lista tuturor recenziilor.
     */
    @GetMapping
    public List<RecenzieResponse> getAll() {
        return recenzieService.getAll();
    }

    /**
     * Returneaza o recenzie dupa ID.
     * @param id Identificatorul recenziei.
     * @return Recenzia gasita.
     */
    @GetMapping("/{id}")
    public RecenzieResponse getById(@PathVariable Long id) {
        return recenzieService.getById(id);
    }

    /**
     * Creeaza o recenzie completa (cu etichete si comentarii despre actori) pentru clientul autentificat.
     * Operatia este delegata procedurii stocate {@code p_creeaza_recenzie_completa}.
     * @param req  Cererea HTTP cu ID-ul clientului setat de interceptor.
     * @param body Datele recenziei de creat.
     * @return HTTP 201 Created cu recenzia creata in format detaliat.
     */
    @PostMapping
    public ResponseEntity<RecenzieDetailResponse> create(HttpServletRequest req,
                                                          @RequestBody CreateRecenzieRequest body) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(recenzieService.create(CurrentUser.getId(req), body));
    }

    /**
     * Actualizeaza partial o recenzie. Doar proprietarul recenziei poate efectua operatia.
     * @param httpReq Cererea HTTP cu ID-ul clientului autentificat.
     * @param id      Identificatorul recenziei de actualizat.
     * @param req     Noile valori (nota si/sau text comentariu).
     * @return Recenzia actualizata.
     */
    @PutMapping("/{id}")
    public RecenzieResponse update(HttpServletRequest httpReq, @PathVariable Long id, @RequestBody UpdateRecenzieRequest req) {
        ownershipService.verificaRecenzie(id, CurrentUser.getId(httpReq));
        return recenzieService.update(id, req);
    }

    /**
     * Sterge o recenzie. Doar proprietarul recenziei poate efectua operatia.
     * @param req Cererea HTTP cu ID-ul clientului autentificat.
     * @param id  Identificatorul recenziei de sters.
     * @return HTTP 204 No Content.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(HttpServletRequest req, @PathVariable Long id) {
        ownershipService.verificaRecenzie(id, CurrentUser.getId(req));
        recenzieService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Returneaza recenziile detaliate ale unui film, cu etichete si comentarii despre actori.
     * @param idFilm Identificatorul filmului.
     * @return Lista recenziilor in format detaliat, ordonate descrescator dupa data postarii.
     */
    @GetMapping("/film/{idFilm}")
    public List<RecenzieDetailResponse> getByFilm(@PathVariable Long idFilm) {
        return recenzieService.getByFilm(idFilm);
    }

    /**
     * Returneaza toate recenziile scrise de un client specific.
     * @param idClient Identificatorul clientului.
     * @return Lista recenziilor clientului, ordonate descrescator dupa data postarii.
     */
    @GetMapping("/client/{idClient}")
    public List<RecenzieResponse> getByClient(@PathVariable Long idClient) {
        return recenzieService.getByClient(idClient);
    }
}
