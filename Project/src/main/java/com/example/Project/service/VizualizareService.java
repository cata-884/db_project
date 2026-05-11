package com.example.Project.service;

import com.example.Project.dao.VizualizareDao;
import com.example.Project.dto.request.UpdateVizualizareRequest;
import com.example.Project.exception.NotFoundException;
import com.example.Project.dto.request.CreateVizualizareRequest;
import com.example.Project.dto.response.IstoricVizionareResponse;
import com.example.Project.model.client.Vizualizari;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VizualizareService {

    @Autowired
    private VizualizareDao vizualizareDao;

    public List<Vizualizari> getAll() {
        return vizualizareDao.findAll();
    }

    public Vizualizari getById(Long id) {
        return vizualizareDao.findById(id)
                .orElseThrow(() -> new NotFoundException("Vizualizarea cu id " + id + " nu există"));
    }

    public Vizualizari create(Long idClient, CreateVizualizareRequest req) {
        if (req.getIdVersiune() == null) throw new IllegalArgumentException("id_versiune este obligatoriu");
        Long id = vizualizareDao.insert(idClient, req.getIdVersiune());
        return vizualizareDao.findById(id).orElseThrow();
    }

    public Vizualizari updateProgress(Long id, UpdateVizualizareRequest req) {
        if (req.getDurata() != null && req.getDurata() < 0) {
            throw new IllegalArgumentException("durata trebuie sa fie >= 0");
        }
        int rows = vizualizareDao.updateProgress(id, req);
        if (rows == 0) throw new NotFoundException("Vizualizarea cu id " + id + " nu există");
        return vizualizareDao.findById(id).orElseThrow();
    }

    public void delete(Long id) {
        int rows = vizualizareDao.deleteById(id);
        if (rows == 0) throw new NotFoundException("Vizualizarea cu id " + id + " nu există");
    }

    public List<IstoricVizionareResponse> getIstoricByClient(Long idClient) {
        return vizualizareDao.findIstoricByClientId(idClient);
    }
}
