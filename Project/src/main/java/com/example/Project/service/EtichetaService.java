package com.example.Project.service;

import com.example.Project.dao.EtichetaDao;
import com.example.Project.exception.NotFoundException;
import com.example.Project.model.recenzie.EtichetaRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EtichetaService {

    @Autowired
    private EtichetaDao etichetaDao;

    public List<EtichetaRecord> getAll() {
        return etichetaDao.findAll();
    }

    public EtichetaRecord getById(Long id) {
        return etichetaDao.findById(id)
                .orElseThrow(() -> new NotFoundException("Eticheta cu id " + id + " nu există"));
    }
}
