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

@Service
public class RecenzieService {

    @Autowired
    private RecenzieDao recenzieDao;

    public List<RecenzieResponse> getAll() {
        return recenzieDao.findAll();
    }

    public RecenzieResponse getById(Long id) {
        return recenzieDao.findById(id)
                .orElseThrow(() -> new NotFoundException("Recenzia cu id " + id + " nu există"));
    }

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

    public RecenzieResponse update(Long id, UpdateRecenzieRequest req) {
        if (req.getNota() != null && (req.getNota() < 1 || req.getNota() > 10))
            throw new IllegalArgumentException("Nota trebuie să fie între 1 și 10");
        int rows = recenzieDao.update(id, req);
        if (rows == 0) throw new NotFoundException("Recenzia cu id " + id + " nu există");
        return recenzieDao.findById(id).orElseThrow();
    }

    public void delete(Long id) {
        int rows = recenzieDao.deleteById(id);
        if (rows == 0) throw new NotFoundException("Recenzia cu id " + id + " nu există");
    }

    public List<RecenzieDetailResponse> getByFilm(Long idFilm) {
        return recenzieDao.findByFilmId(idFilm);
    }

    public List<RecenzieResponse> getByClient(Long idClient) {
        return recenzieDao.findByClientId(idClient);
    }
}
