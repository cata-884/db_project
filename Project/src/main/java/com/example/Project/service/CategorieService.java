package com.example.Project.service;

import com.example.Project.dao.CategorieDao;
import com.example.Project.model.film.Categorie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategorieService {

    @Autowired
    private CategorieDao categorieDao;

    public List<Categorie> getAll() {
        return categorieDao.findAll();
    }

    public Categorie getById(Long id) {
        return categorieDao.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoria cu id " + id + " nu există"));
    }

    public Categorie create(Categorie categorie) {
        if (categorie.getNume() == null || categorie.getNume().isBlank())
            throw new IllegalArgumentException("Numele categoriei este obligatoriu");
        return categorieDao.save(categorie);
    }

    public Categorie update(Long id, Categorie categorie) {
        if (categorie.getNume() == null || categorie.getNume().isBlank())
            throw new IllegalArgumentException("Numele categoriei este obligatoriu");
        int rows = categorieDao.update(id, categorie);
        if (rows == 0) throw new RuntimeException("Categoria cu id " + id + " nu există");
        return categorieDao.findById(id).orElseThrow();
    }

    public void delete(Long id) {
        int rows = categorieDao.deleteById(id);
        if (rows == 0) throw new RuntimeException("Categoria cu id " + id + " nu există");
    }
}
