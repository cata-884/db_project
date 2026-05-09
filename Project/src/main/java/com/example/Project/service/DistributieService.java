package com.example.Project.service;

import com.example.Project.dao.DistributieDao;
import com.example.Project.exception.NotFoundException;
import com.example.Project.dto.request.CreateDistributieRequest;
import com.example.Project.dto.response.ActorDistributieResponse;
import com.example.Project.model.actor.Distributie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DistributieService {

    @Autowired
    private DistributieDao distributieDao;

    public Distributie create(CreateDistributieRequest req) {
        if (req.getIdFilm() == null) throw new IllegalArgumentException("id_film este obligatoriu");
        if (req.getIdActor() == null) throw new IllegalArgumentException("id_actor este obligatoriu");
        if (req.getRole() == null) throw new IllegalArgumentException("rolul este obligatoriu");
        return distributieDao.insert(req);
    }

    public List<ActorDistributieResponse> getActoriByFilm(Long idFilm) {
        return distributieDao.findActoriByFilmId(idFilm);
    }

    public void delete(Long idFilm, Long idActor) {
        int rows = distributieDao.delete(idFilm, idActor);
        if (rows == 0) throw new NotFoundException("Distribuția nu există");
    }
}
