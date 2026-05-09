package com.example.Project.service;

import com.example.Project.dao.VizualizareDao;
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

    public Vizualizari create(CreateVizualizareRequest req) {
        if (req.getIdClient() == null) throw new IllegalArgumentException("id_client este obligatoriu");
        if (req.getIdVersiune() == null) throw new IllegalArgumentException("id_versiune este obligatoriu");
        Long id = vizualizareDao.insert(req);
        return vizualizareDao.findById(id).orElseThrow();
    }

    public Vizualizari update(Long id, Vizualizari viz) {
        int rows = vizualizareDao.update(id, viz);
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
