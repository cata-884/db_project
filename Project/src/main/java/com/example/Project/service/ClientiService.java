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

/**
 * Serviciu pentru citirea si actualizarea profilului clientilor.
 * Citeste datele complete ale clientului prin {@link AuthDao} si
 * persista modificarile prin {@link ClientiDao}.
 */
@Service
public class ClientiService {

    private final AuthDao authDao;
    private final ClientiDao clientiDao;

    public ClientiService(AuthDao authDao, ClientiDao clientiDao) {
        this.authDao = authDao;
        this.clientiDao = clientiDao;
    }

    /**
     * Returneaza profilul unui client dupa ID, proiectat in {@link ClientResponse}.
     * @param id Identificatorul clientului.
     * @return DTO cu datele profilului clientului.
     * @throws NotFoundException daca nu exista niciun client cu ID-ul dat.
     */
    public ClientResponse getById(Long id) {
        Client c = authDao.findById(id)
                .orElseThrow(() -> new NotFoundException("Client negasit cu id: " + id));
        return toResponse(c);
    }

    /**
     * Actualizeaza datele de contact ale unui client si returneaza profilul actualizat.
     * Operatia este tranzactionala: daca update-ul esueaza, nicio modificare nu este persistata.
     * @param id  Identificatorul clientului de actualizat.
     * @param req Noile date de contact.
     * @return DTO cu profilul actualizat al clientului.
     * @throws NotFoundException daca nu exista niciun client cu ID-ul dat.
     */
    @Transactional
    public ClientResponse update(Long id, UpdateClientRequest req) {
        authDao.findById(id).orElseThrow(() -> new NotFoundException("Client negasit cu id: " + id));
        clientiDao.update(id, req);
        return getById(id);
    }

    private ClientResponse toResponse(Client c) {
        PhoneNumber homePhone = c.getTelefonFix();
        PhoneNumber cellPhone = c.getTelefonMobil();
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
                c.getAdresa(),
                c.getOras(),
                c.getDataNastere()
        );
    }

    private String formatPhone(PhoneNumber phone) {
        if (phone == null) return null;
        return phone.getCode().getPrefix() + " " + phone.getNumber();
    }
}
