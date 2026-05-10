package com.example.Project.controller;

import com.example.Project.dto.request.UpdateClientRequest;
import com.example.Project.dto.response.ClientResponse;
import com.example.Project.service.ClientiService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/clienti")
public class ClientiController {

    private final ClientiService clientiService;

    public ClientiController(ClientiService clientiService) {
        this.clientiService = clientiService;
    }

    // /me?username=xxx elimina IDOR-ul pe ID numeric
    @GetMapping("/me")
    public ClientResponse me(@RequestParam String username) {
        return clientiService.getMe(username);
    }

    @PutMapping("/me")
    public ClientResponse updateMe(@RequestParam String username,
                                   @RequestBody UpdateClientRequest req) {
        return clientiService.updateMe(username, req);
    }
}
