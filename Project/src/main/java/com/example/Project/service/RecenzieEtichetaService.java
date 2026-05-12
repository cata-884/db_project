package com.example.Project.service;

import com.example.Project.dao.RecenzieEtichetaDao;
import com.example.Project.exception.NotFoundException;
import com.example.Project.model.recenzie.EtichetaRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Serviciu pentru gestionarea asocierilor dintre recenzii si etichete.
 * Coordoneaza validarile si delega operatiile catre {@link RecenzieEtichetaDao}.
 */
@Service
public class RecenzieEtichetaService {

    @Autowired
    private RecenzieEtichetaDao recenzieEtichetaDao;

    /**
     * Asociaza o eticheta unei recenzii.
     * @param idRecenzie Identificatorul recenziei; obligatoriu.
     * @param idEticheta Identificatorul etichetei de asociat; obligatoriu.
     * @throws IllegalArgumentException daca oricare parametru este {@code null}.
     */
    public void addEticheta(Long idRecenzie, Long idEticheta) {
        if (idRecenzie == null) throw new IllegalArgumentException("id_recenzie este obligatoriu");
        if (idEticheta == null) throw new IllegalArgumentException("id_eticheta este obligatoriu");
        recenzieEtichetaDao.addEticheta(idRecenzie, idEticheta);
    }

    /**
     * Returneaza etichetele asociate unei recenzii.
     * @param idRecenzie Identificatorul recenziei.
     * @return Lista etichetelor; lista vida daca nu exista asocieri.
     */
    public List<EtichetaRecord> getEtichete(Long idRecenzie) {
        return recenzieEtichetaDao.findEticheteByRecenzieId(idRecenzie);
    }

    /**
     * Elimina asocierea dintre o recenzie si o eticheta.
     * @param idRecenzie Identificatorul recenziei.
     * @param idEticheta Identificatorul etichetei de eliminat.
     * @throws NotFoundException daca asocierea nu exista.
     */
    public void removeEticheta(Long idRecenzie, Long idEticheta) {
        int rows = recenzieEtichetaDao.removeEticheta(idRecenzie, idEticheta);
        if (rows == 0) throw new NotFoundException("Asocierea recenzie-eticheta nu există");
    }
}
