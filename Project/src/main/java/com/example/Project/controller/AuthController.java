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

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private SesiuneService sesiuneService;

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest req) {
        return authService.login(req.getUsername(), req.getParola());
    }

    @PostMapping("/register")
    public LoginResponse register(@RequestBody RegisterRequest req) {
        return authService.register(req.getUsername(), req.getParola(), req.getNume(), req.getPrenume(), req.getEmail());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest req) {
        String token = CurrentUser.getToken(req);
        if (token != null) {
            sesiuneService.invalideazaToken(token);
        }
        return ResponseEntity.noContent().build();
    }
}
