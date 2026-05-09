package com.example.Project.service;

import com.example.Project.dao.RecenzieEtichetaDao;
import com.example.Project.exception.NotFoundException;
import com.example.Project.model.recenzie.EtichetaRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecenzieEtichetaService {

    @Autowired
    private RecenzieEtichetaDao recenzieEtichetaDao;

    public void addEticheta(Long idRecenzie, Long idEticheta) {
        if (idRecenzie == null) throw new IllegalArgumentException("id_recenzie este obligatoriu");
        if (idEticheta == null) throw new IllegalArgumentException("id_eticheta este obligatoriu");
        recenzieEtichetaDao.addEticheta(idRecenzie, idEticheta);
    }

    public List<EtichetaRecord> getEtichete(Long idRecenzie) {
        return recenzieEtichetaDao.findEticheteByRecenzieId(idRecenzie);
    }

    public void removeEticheta(Long idRecenzie, Long idEticheta) {
        int rows = recenzieEtichetaDao.removeEticheta(idRecenzie, idEticheta);
        if (rows == 0) throw new NotFoundException("Asocierea recenzie-eticheta nu există");
    }
}
