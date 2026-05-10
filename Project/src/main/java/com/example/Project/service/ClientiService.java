package com.example.Project.service;

import com.example.Project.dao.AuthDao;
import com.example.Project.dao.ClientiDao;
import com.example.Project.dto.request.UpdateClientRequest;
import com.example.Project.dto.response.ClientResponse;
import com.example.Project.exception.NotFoundException;
import com.example.Project.model.client.Client;
import com.example.Project.model.client.PhoneNumber;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClientiService {

    private final AuthDao authDao;
    private final ClientiDao clientiDao;

    public ClientiService(AuthDao authDao, ClientiDao clientiDao) {
        this.authDao = authDao;
        this.clientiDao = clientiDao;
    }

    public ClientResponse getMe(String username) {
        Client c = authDao.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("Client negasit: " + username));
        return toResponse(c);
    }

    @Transactional
    public ClientResponse updateMe(String username, UpdateClientRequest req) {
        Client c = authDao.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("Client negasit: " + username));
        clientiDao.update(c.getId(), req);
        return getMe(username);
    }

    private ClientResponse toResponse(Client c) {
        PhoneNumber homePhone = c.getHomePhone();
        PhoneNumber cellPhone = c.getCellphone();
        String homeCode = homePhone != null ? homePhone.getCode().getPrefix() : null;
        String homeNr = homePhone != null ? homePhone.getNumber() : null;
        String cellCode = cellPhone != null ? cellPhone.getCode().getPrefix() : null;
        String cellNr = cellPhone != null ? cellPhone.getNumber() : null;

        return new ClientResponse(
                c.getId(),
                c.getUsername(),
                c.getNume(),
                c.getPrenume(),
                c.getEmail(),
                homeCode,
                homeNr,
                cellCode,
                cellNr,
                formatPhone(homePhone),
                formatPhone(cellPhone),
                c.getAddress(),
                c.getCity(),
                c.getDataNastere()
        );
    }

    private String formatPhone(PhoneNumber phone) {
        if (phone == null) return null;
        return phone.getCode().getPrefix() + " " + phone.getNumber();
    }
}
