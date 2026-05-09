package com.example.Project.controller;

import com.example.Project.dto.LoginRequest;
import com.example.Project.dto.response.LoginResponse;
import com.example.Project.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest req) {
        return authService.login(req.getUsername(), req.getParola());
    }
}
