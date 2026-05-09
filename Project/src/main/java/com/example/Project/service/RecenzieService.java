package com.example.Project.service;

import com.example.Project.dao.RecenzieDao;
import com.example.Project.dto.request.CreateRecenzieRequest;
import com.example.Project.dto.request.UpdateRecenzieRequest;
import com.example.Project.dto.response.RecenzieDetailResponse;
import com.example.Project.dto.response.RecenzieResponse;
import com.example.Project.model.recenzie.Recenzii;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
                .orElseThrow(() -> new RuntimeException("Recenzia cu id " + id + " nu există"));
    }

    public RecenzieDetailResponse create(CreateRecenzieRequest req) {
        if (req.getIdClient() == null) throw new IllegalArgumentException("id_client este obligatoriu");
        if (req.getIdFilm() == null) throw new IllegalArgumentException("id_film este obligatoriu");
        if (req.getNota() == null || req.getNota() < 1 || req.getNota() > 10)
            throw new IllegalArgumentException("Nota trebuie să fie între 1 și 10");
        if (req.getTextComentariu() != null && req.getTextComentariu().length() > 4000)
            throw new IllegalArgumentException("textComentariu depășește 4000 caractere");

        Recenzii recenzie = new Recenzii(null, req.getIdClient(), req.getIdFilm(),
                req.getNota(), null, req.getTextComentariu(), null);

        Long id = recenzieDao.insert(recenzie);
        return recenzieDao.findDetailById(id)
                .orElseThrow(() -> new RuntimeException("Eroare la recuperarea recenziei create"));
    }

    public RecenzieResponse update(Long id, UpdateRecenzieRequest req) {
        if (req.getNota() != null && (req.getNota() < 1 || req.getNota() > 10))
            throw new IllegalArgumentException("Nota trebuie să fie între 1 și 10");
        int rows = recenzieDao.update(id, req);
        if (rows == 0) throw new RuntimeException("Recenzia cu id " + id + " nu există");
        return recenzieDao.findById(id).orElseThrow();
    }

    public void delete(Long id) {
        int rows = recenzieDao.deleteById(id);
        if (rows == 0) throw new RuntimeException("Recenzia cu id " + id + " nu există");
    }

    public List<RecenzieDetailResponse> getByFilm(Long idFilm) {
        return recenzieDao.findByFilmId(idFilm);
    }

    public List<RecenzieResponse> getByClient(Long idClient) {
        return recenzieDao.findByClientId(idClient);
    }
}
