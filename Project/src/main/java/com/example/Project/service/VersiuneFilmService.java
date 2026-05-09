package com.example.Project.service;

import com.example.Project.dao.VersiuneFilmDao;
import com.example.Project.model.film.VersiuneFilm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VersiuneFilmService {

    @Autowired
    private VersiuneFilmDao versiuneFilmDao;

    public List<VersiuneFilm> getByFilmId(Long idFilm) {
        return versiuneFilmDao.findByFilmId(idFilm);
    }
}

