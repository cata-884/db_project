package com.example.Project.service;

import com.example.Project.dao.RecenzieActorDao;
import com.example.Project.model.recenzie.RecenziiActori;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Serviciu pentru consultarea comentariilor despre actori asociate recenziilor.
 * Delega interogarea catre {@link RecenzieActorDao}.
 */
@Service
public class RecenzieActorService {

    @Autowired
    private RecenzieActorDao recenzieActorDao;

    /**
     * Returneaza comentariile despre actori asociate unei recenzii specifice.
     * @param idRecenzie Identificatorul recenziei.
     * @return Lista comentariilor despre actori; lista vida daca nu exista comentarii.
     */
    public List<RecenziiActori> getByRecenzie(Long idRecenzie) {
        return recenzieActorDao.findByRecenzieId(idRecenzie);
    }
}
