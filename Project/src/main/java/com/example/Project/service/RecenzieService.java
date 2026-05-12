package com.example.Project.service;

import com.example.Project.dao.RecenzieDao;
import com.example.Project.exception.NotFoundException;
import com.example.Project.dto.request.CreateRecenzieRequest;
import com.example.Project.dto.request.UpdateRecenzieRequest;
import com.example.Project.dto.response.RecenzieDetailResponse;
import com.example.Project.dto.response.RecenzieResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Serviciu pentru logica de business a entitatii Recenzie.
 * Coordoneaza validarile si delega operatiile catre {@link RecenzieDao}.
 * Crearea recenziei complete (cu etichete si comentarii actori) este delegata
 * procedurii stocate Oracle prin {@link RecenzieDao#creeazaRecenzieCompleta}.
 */
@Service
public class RecenzieService {

    @Autowired
    private RecenzieDao recenzieDao;

    /**
     * Returneaza toate recenziile din sistem.
     * @return Lista recenziilor, ordonata dupa ID.
     */
    public List<RecenzieResponse> getAll() {
        return recenzieDao.findAll();
    }

    /**
     * Returneaza o recenzie dupa ID.
     * @param id Identificatorul recenziei.
     * @return Recenzia gasita.
     * @throws NotFoundException daca nu exista nicio recenzie cu ID-ul dat.
     */
    public RecenzieResponse getById(Long id) {
        return recenzieDao.findById(id)
                .orElseThrow(() -> new NotFoundException("Recenzia cu id " + id + " nu există"));
    }

    /**
     * Creeaza o recenzie completa cu etichete si comentarii despre actori.
     * Delega intreaga operatie procedurii stocate {@code p_creeaza_recenzie_completa}.
     * @param idClient ID-ul clientului autentificat.
     * @param req      Datele recenziei (film, nota, text, etichete, actori).
     * @return Recenzia creata in format detaliat.
     * @throws IllegalArgumentException daca {@code idFilm} lipseste sau nota este invalida.
     */
    @Transactional
    public RecenzieDetailResponse create(Long idClient, CreateRecenzieRequest req) {
        if (req.getIdFilm() == null) throw new IllegalArgumentException("id_film este obligatoriu");
        if (req.getNota() == null || req.getNota() < 1 || req.getNota() > 10)
            throw new IllegalArgumentException("Nota trebuie sa fie intre 1 si 10");
        if (req.getTextComentariu() != null && req.getTextComentariu().length() > 4000)
            throw new IllegalArgumentException("textComentariu depaseste 4000 caractere");

        Long id = recenzieDao.creeazaRecenzieCompleta(
                idClient, req.getIdFilm(), req.getNota(), req.getTextComentariu(),
                req.getEtichetaIds(), req.getActoriIds(), req.getActoriComentarii());

        return recenzieDao.findDetailById(id)
                .orElseThrow(() -> new RuntimeException("Eroare la recuperarea recenziei create"));
    }

    /**
     * Actualizeaza partial o recenzie existenta.
     * @param id  Identificatorul recenziei de actualizat.
     * @param req Noile valori; campurile {@code null} nu suprascriu datele existente.
     * @return Recenzia actualizata.
     * @throws IllegalArgumentException daca nota este in afara intervalului [1, 10].
     * @throws NotFoundException daca nu exista nicio recenzie cu ID-ul dat.
     */
    public RecenzieResponse update(Long id, UpdateRecenzieRequest req) {
        if (req.getNota() != null && (req.getNota() < 1 || req.getNota() > 10))
            throw new IllegalArgumentException("Nota trebuie să fie între 1 și 10");
        int rows = recenzieDao.update(id, req);
        //ar trebui sa fie !=1 dar lasam asa
        if (rows == 0) throw new NotFoundException("Recenzia cu id " + id + " nu există");
        return recenzieDao.findById(id).orElseThrow();
    }

    /**
     * Sterge o recenzie dupa ID.
     * @param id Identificatorul recenziei de sters.
     * @throws NotFoundException daca nu exista nicio recenzie cu ID-ul dat.
     */
    public void delete(Long id) {
        int rows = recenzieDao.deleteById(id);
        if (rows == 0) throw new NotFoundException("Recenzia cu id " + id + " nu există");
    }

    /**
     * Returneaza recenziile detaliate ale unui film, cu etichete si comentarii despre actori.
     * @param idFilm Identificatorul filmului.
     * @return Lista recenziilor in format detaliat, ordonata descrescator dupa data postarii.
     */
    public List<RecenzieDetailResponse> getByFilm(Long idFilm) {
        return recenzieDao.findByFilmId(idFilm);
    }

    /**
     * Returneaza toate recenziile scrise de un client specific.
     * @param idClient Identificatorul clientului.
     * @return Lista recenziilor clientului, ordonata descrescator dupa data postarii.
     */
    public List<RecenzieResponse> getByClient(Long idClient) {
        return recenzieDao.findByClientId(idClient);
    }
}
