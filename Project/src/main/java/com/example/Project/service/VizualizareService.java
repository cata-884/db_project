package com.example.Project.service;

import com.example.Project.dao.VizualizareDao;
import com.example.Project.dto.request.UpdateVizualizareRequest;
import com.example.Project.exception.NotFoundException;
import com.example.Project.dto.request.CreateVizualizareRequest;
import com.example.Project.dto.response.IstoricVizionareResponse;
import com.example.Project.model.client.Vizualizari;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Serviciu pentru logica de business a entitatii Vizualizari.
 * Coordoneaza validarile si delega operatiile catre {@link VizualizareDao}.
 */
@Service
public class VizualizareService {

    @Autowired
    private VizualizareDao vizualizareDao;

    /**
     * Returneaza toate vizualizarile din sistem.
     * @return Lista tuturor vizualizarilor.
     */
    public List<Vizualizari> getAll() {
        return vizualizareDao.findAll();
    }

    /**
     * Returneaza o vizualizare dupa ID.
     * @param id Identificatorul vizualizarii.
     * @return Vizualizarea gasita.
     * @throws NotFoundException daca nu exista nicio vizualizare cu ID-ul dat.
     */
    public Vizualizari getById(Long id) {
        return vizualizareDao.findById(id)
                .orElseThrow(() -> new NotFoundException("Vizualizarea cu id " + id + " nu există"));
    }

    /**
     * Inregistreaza o noua vizualizare pentru un client.
     * @param idClient ID-ul clientului autentificat.
     * @param req      Datele vizualizarii; {@code idVersiune} este obligatoriu.
     * @return Vizualizarea creata cu ID-ul generat.
     * @throws IllegalArgumentException daca {@code idVersiune} este {@code null}.
     */
    public Vizualizari create(Long idClient, CreateVizualizareRequest req) {
        if (req.getIdVersiune() == null) throw new IllegalArgumentException("id_versiune este obligatoriu");
        Long id = vizualizareDao.insert(idClient, req.getIdVersiune());
        return vizualizareDao.findById(id).orElseThrow();
    }

    /**
     * Actualizeaza progresul (durata si/sau starea) unei vizualizari existente.
     * @param id  Identificatorul vizualizarii de actualizat.
     * @param req Noile valori; campurile {@code null} nu suprascriu valorile existente.
     * @return Vizualizarea actualizata.
     * @throws IllegalArgumentException daca {@code durata} este negativa.
     * @throws NotFoundException daca nu exista nicio vizualizare cu ID-ul dat.
     */
    public Vizualizari updateProgress(Long id, UpdateVizualizareRequest req) {
        if (req.getDurata() != null && req.getDurata() < 0) {
            throw new IllegalArgumentException("durata trebuie sa fie >= 0");
        }
        int rows = vizualizareDao.updateProgress(id, req);
        if (rows == 0) throw new NotFoundException("Vizualizarea cu id " + id + " nu există");
        return vizualizareDao.findById(id).orElseThrow();
    }

    /**
     * Sterge o vizualizare dupa ID.
     * @param id Identificatorul vizualizarii de sters.
     * @throws NotFoundException daca nu exista nicio vizualizare cu ID-ul dat.
     */
    public void delete(Long id) {
        int rows = vizualizareDao.deleteById(id);
        if (rows == 0) throw new NotFoundException("Vizualizarea cu id " + id + " nu există");
    }

    /**
     * Returneaza istoricul de vizionare al unui client.
     * @param idClient Identificatorul clientului.
     * @return Lista vizionarilor cu detalii despre film si versiune, ordonata descrescator dupa data.
     */
    public List<IstoricVizionareResponse> getIstoricByClient(Long idClient) {
        return vizualizareDao.findIstoricByClientId(idClient);
    }
}
