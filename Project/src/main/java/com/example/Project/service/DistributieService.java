package com.example.Project.service;

import com.example.Project.dao.DistributieDao;
import com.example.Project.dto.response.ActorDistributieResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Serviciu pentru consultarea distributiei filmelor.
 * Delega interogarea catre {@link DistributieDao}.
 */
@Service
public class DistributieService {

    @Autowired
    private DistributieDao distributieDao;

    /**
     * Returneaza actorii si rolurile lor dintr-un film specificat.
     * @param idFilm Identificatorul filmului.
     * @return Lista actorilor cu rolurile din filmul dat.
     */
    public List<ActorDistributieResponse> getActoriByFilm(Long idFilm) {
        return distributieDao.findActoriByFilmId(idFilm);
    }
}
