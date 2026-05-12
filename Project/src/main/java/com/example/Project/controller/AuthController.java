package com.example.Project.controller;

import com.example.Project.config.CurrentUser;
import com.example.Project.dto.LoginRequest;
import com.example.Project.dto.request.RegisterRequest;
import com.example.Project.dto.response.LoginResponse;
import com.example.Project.service.AuthService;
import com.example.Project.service.SesiuneService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller REST pentru autentificare si gestionarea sesiunilor.
 * Rutele {@code /login} si {@code /register} sunt publice (fara token necesar).
 * Expune resursa {@code /api/auth}.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private SesiuneService sesiuneService;

    /**
     * Autentifica un client existent si returneaza un token de sesiune.
     * @param req Credentialele de autentificare (username si parola).
     * @return Token-ul de sesiune si datele clientului autentificat.
     */
    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest req) {
        return authService.login(req.getUsername(), req.getParola());
    }

    /**
     * Inregistreaza un client nou si returneaza un token de sesiune activ.
     * @param req Datele de inregistrare (username, parola, nume, prenume, email).
     * @return Token-ul de sesiune si datele noului client.
     */
    @PostMapping("/register")
    public LoginResponse register(@RequestBody RegisterRequest req) {
        return authService.register(req.getUsername(), req.getParola(), req.getNume(), req.getPrenume(), req.getEmail());
    }

    /**
     * Invalideaza sesiunea curenta a clientului autentificat (logout).
     * @param req Cererea HTTP din care se extrage token-ul curent.
     * @return HTTP 204 No Content.
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest req) {
        String token = CurrentUser.getToken(req);
        if (token != null) {
            sesiuneService.invalideazaToken(token);
        }
        return ResponseEntity.noContent().build();
    }
}
