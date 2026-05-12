package com.example.Project.service;

import com.example.Project.dao.EtichetaDao;
import com.example.Project.exception.NotFoundException;
import com.example.Project.model.recenzie.EtichetaRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Serviciu pentru consultarea etichetelor disponibile in sistem.
 * Delega interogarea catre {@link EtichetaDao}.
 */
@Service
public class EtichetaService {

    @Autowired
    private EtichetaDao etichetaDao;

    /**
     * Returneaza toate etichetele disponibile, ordonate dupa sentiment si denumire.
     * @return Lista completa de etichete.
     */
    public List<EtichetaRecord> getAll() {
        return etichetaDao.findAll();
    }

    /**
     * Returneaza o eticheta dupa ID.
     * @param id Identificatorul etichetei.
     * @return Eticheta gasita.
     * @throws NotFoundException daca nu exista nicio eticheta cu ID-ul dat.
     */
    public EtichetaRecord getById(Long id) {
        return etichetaDao.findById(id)
                .orElseThrow(() -> new NotFoundException("Eticheta cu id " + id + " nu există"));
    }
}
