package com.example.Project.service;

import com.example.Project.dao.RecenzieActorDao;
import com.example.Project.dto.request.CreateRecenzieActorRequest;
import com.example.Project.model.recenzie.RecenziiActori;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecenzieActorService {

    @Autowired
    private RecenzieActorDao recenzieActorDao;

    public RecenziiActori create(CreateRecenzieActorRequest req) {
        if (req.getIdRecenzie() == null) throw new IllegalArgumentException("id_recenzie este obligatoriu");
        if (req.getIdActor() == null) throw new IllegalArgumentException("id_actor este obligatoriu");
        return recenzieActorDao.insert(req);
    }

    public List<RecenziiActori> getByRecenzie(Long idRecenzie) {
        return recenzieActorDao.findByRecenzieId(idRecenzie);
    }

    public void delete(Long idRecenzie, Long idActor) {
        int rows = recenzieActorDao.delete(idRecenzie, idActor);
        if (rows == 0) throw new RuntimeException("Recenzia actorului nu există");
    }
}
