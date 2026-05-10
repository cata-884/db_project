package com.example.Project.service;

import com.example.Project.dao.AuthDao;
import com.example.Project.dto.response.LoginResponse;
import com.example.Project.model.client.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private AuthDao authDao;

    public LoginResponse login(String username, String parola) {
        if (username == null || username.isBlank())
            throw new IllegalArgumentException("Username-ul este obligatoriu");
        if (parola == null || parola.isBlank())
            throw new IllegalArgumentException("Parola este obligatorie");

        Client client = authDao.findByUsernameAndPassword(username, parola)
                .orElseThrow(() -> new IllegalArgumentException("Username sau parola incorecte"));

        return new LoginResponse(client.getId(), client.getUsername(),
                client.getNume(), client.getPrenume(), client.getEmail());
    }

    public LoginResponse register(String username, String parola, String nume, String prenume, String email) {
        if (username == null || username.isBlank())
            throw new IllegalArgumentException("Username-ul este obligatoriu");
        if (parola == null || parola.isBlank())
            throw new IllegalArgumentException("Parola este obligatorie");
        if (nume == null || nume.isBlank())
            throw new IllegalArgumentException("Numele este obligatoriu");
        if (prenume == null || prenume.isBlank())
            throw new IllegalArgumentException("Prenumele este obligatoriu");

        if (authDao.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username deja folosit");
        }

        Long id = authDao.insertBasicClient(nume, prenume, (email == null || email.isBlank()) ? null : email,
                username, parola);

        return new LoginResponse(id, username, nume, prenume, email);
    }
}
