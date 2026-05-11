package com.example.Project.controller;

import com.example.Project.config.CurrentUser;
import com.example.Project.dto.request.UpdateClientRequest;
import com.example.Project.dto.response.ClientResponse;
import com.example.Project.service.ClientiService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/clienti")
public class ClientiController {

    private final ClientiService clientiService;

    public ClientiController(ClientiService clientiService) {
        this.clientiService = clientiService;
    }

    @GetMapping("/me")
    public ClientResponse me(HttpServletRequest req) {
        return clientiService.getById(CurrentUser.getId(req));
    }

    @PutMapping("/me")
    public ClientResponse updateMe(HttpServletRequest req,
                                   @RequestBody UpdateClientRequest request) {
        return clientiService.update(CurrentUser.getId(req), request);
    }
}
