package com.example.Project.controller;

import com.example.Project.config.CurrentUser;
import com.example.Project.dto.request.UpdateClientRequest;
import com.example.Project.dto.response.ClientResponse;
import com.example.Project.service.ClientiService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

/**
 * Controller REST pentru gestionarea profilului clientului autentificat.
 * Toate rutele necesita autentificare; clientul poate accesa si modifica doar propriul profil.
 * Expune resursa {@code /api/clienti}.
 */
@RestController
@RequestMapping("/api/clienti")
public class ClientiController {

    private final ClientiService clientiService;

    public ClientiController(ClientiService clientiService) {
        this.clientiService = clientiService;
    }

    /**
     * Returneaza profilul clientului autentificat curent.
     * @param req Cererea HTTP cu ID-ul clientului setat de interceptor.
     * @return Datele profilului clientului curent.
     */
    @GetMapping("/me")
    public ClientResponse me(HttpServletRequest req) {
        return clientiService.getById(CurrentUser.getId(req));
    }

    /**
     * Actualizeaza profilul clientului autentificat curent.
     * Campurile {@code null} din body nu vor suprascrie valorile existente.
     * @param req     Cererea HTTP cu ID-ul clientului setat de interceptor.
     * @param request Datele de actualizat.
     * @return Profilul actualizat al clientului.
     */
    @PutMapping("/me")
    public ClientResponse updateMe(HttpServletRequest req,
                                   @RequestBody UpdateClientRequest request) {
        return clientiService.update(CurrentUser.getId(req), request);
    }
}
