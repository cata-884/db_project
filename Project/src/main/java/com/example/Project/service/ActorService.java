package com.example.Project.service;

import com.example.Project.dao.ActorDao;
import com.example.Project.exception.NotFoundException;
import com.example.Project.dto.response.ActorDistributieResponse;
import com.example.Project.model.actor.Actor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
/*
    CRUD - CREATE READ UPDATE DELETE
 */
@Service
public class ActorService {

    @Autowired
    private ActorDao actorDao;

    public List<Actor> getAll() {
        return actorDao.findAll();
    }

    public Actor getById(Long id) {
        return actorDao.findById(id)
                .orElseThrow(() -> new NotFoundException("Actorul cu id " + id + " nu există"));
    }

    public Actor create(Actor actor) {
        if (actor.getNume() == null || actor.getNume().isBlank())
            throw new IllegalArgumentException("Numele actorului este obligatoriu");
        if (actor.getPrenume() == null || actor.getPrenume().isBlank())
            throw new IllegalArgumentException("Prenumele actorului este obligatoriu");
        return actorDao.save(actor);
    }

    public Actor update(Long id, Actor actor) {
        int rows = actorDao.update(id, actor);
        if (rows == 0) throw new NotFoundException("Actorul cu id " + id + " nu există");
        return actorDao.findById(id).orElseThrow();
    }

    public void delete(Long id) {
        int rows = actorDao.deleteById(id);
        if (rows == 0) throw new NotFoundException("Actorul cu id " + id + " nu există");
    }

    public List<ActorDistributieResponse> getByFilm(Long idFilm) {
        return actorDao.findByFilmId(idFilm);
    }
}
