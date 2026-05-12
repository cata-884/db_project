package com.example.Project.service;

import com.example.Project.dao.ActorDao;
import com.example.Project.exception.NotFoundException;
import com.example.Project.dto.response.ActorDistributieResponse;
import com.example.Project.model.actor.Actor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
/**
 * Serviciu pentru logica de business a entitatii Actor.
 * Coordoneaza operatiile CRUD si validarile inainte de a delega catre {@link ActorDao}.
 */
@Service
public class ActorService {

    @Autowired
    private ActorDao actorDao;

    /**
     * Returneaza toti actorii din catalog.
     * @return Lista actorilor, ordonata dupa ID.
     */
    public List<Actor> getAll() {
        return actorDao.findAll();
    }

    /**
     * Returneaza un actor dupa ID.
     * @param id Identificatorul actorului.
     * @return Actorul gasit.
     * @throws NotFoundException daca nu exista niciun actor cu ID-ul dat.
     */
    public Actor getById(Long id) {
        return actorDao.findById(id)
                .orElseThrow(() -> new NotFoundException("Actorul cu id " + id + " nu există"));
    }

    /**
     * Creeaza un actor nou dupa validarea campurilor obligatorii.
     * @param actor Datele actorului de creat; {@code nume} si {@code prenume} sunt obligatorii.
     * @return Actorul creat cu ID-ul generat de baza de date.
     * @throws IllegalArgumentException daca {@code nume} sau {@code prenume} sunt nule sau goale.
     */
    public Actor create(Actor actor) {
        if (actor.getNume() == null || actor.getNume().isBlank())
            throw new IllegalArgumentException("Numele actorului este obligatoriu");
        if (actor.getPrenume() == null || actor.getPrenume().isBlank())
            throw new IllegalArgumentException("Prenumele actorului este obligatoriu");
        return actorDao.save(actor);
    }

    /**
     * Actualizeaza datele unui actor existent.
     * @param id    Identificatorul actorului de actualizat.
     * @param actor Noile valori; campurile {@code null} nu suprascriu valorile existente.
     * @return Actorul actualizat.
     * @throws NotFoundException daca nu exista niciun actor cu ID-ul dat.
     */
    public Actor update(Long id, Actor actor) {
        int rows = actorDao.update(id, actor);
        if (rows == 0) throw new NotFoundException("Actorul cu id " + id + " nu există");
        return actorDao.findById(id).orElseThrow();
    }

    /**
     * Sterge un actor dupa ID.
     * @param id Identificatorul actorului de sters.
     * @throws NotFoundException daca nu exista niciun actor cu ID-ul dat.
     */
    public void delete(Long id) {
        int rows = actorDao.deleteById(id);
        if (rows == 0) throw new NotFoundException("Actorul cu id " + id + " nu există");
    }

    /**
     * Returneaza actorii si rolurile lor dintr-un film specificat.
     * @param idFilm Identificatorul filmului.
     * @return Lista actorilor cu rolurile din filmul dat.
     */
    public List<ActorDistributieResponse> getByFilm(Long idFilm) {
        return actorDao.findByFilmId(idFilm);
    }
}
