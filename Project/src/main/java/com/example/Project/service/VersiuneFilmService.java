package com.example.Project.service;

import com.example.Project.dao.VersiuneFilmDao;
import com.example.Project.model.film.VersiuneFilm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Serviciu pentru consultarea versiunilor disponibile ale filmelor.
 * Delega interogarea catre {@link VersiuneFilmDao}.
 */
@Service
public class VersiuneFilmService {

    @Autowired
    private VersiuneFilmDao versiuneFilmDao;

    /**
     * Returneaza toate versiunile unui film (rezolutie, format, limbi).
     * @param idFilm Identificatorul filmului.
     * @return Lista versiunilor filmului, ordonata dupa ID.
     */
    public List<VersiuneFilm> getByFilmId(Long idFilm) {
        return versiuneFilmDao.findByFilmId(idFilm);
    }
}

