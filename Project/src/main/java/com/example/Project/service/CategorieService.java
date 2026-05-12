package com.example.Project.service;

import com.example.Project.dao.CategorieDao;
import com.example.Project.exception.NotFoundException;
import com.example.Project.model.film.Categorie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Serviciu pentru logica de business a entitatii Categorie.
 * Coordoneaza operatiile CRUD si validarile inainte de a delega catre {@link CategorieDao}.
 */
@Service
public class CategorieService {

    @Autowired
    private CategorieDao categorieDao;

    /**
     * Returneaza toate categoriile disponibile.
     * @return Lista categoriilor, ordonata dupa ID.
     */
    public List<Categorie> getAll() {
        return categorieDao.findAll();
    }

    /**
     * Returneaza o categorie dupa ID.
     * @param id Identificatorul categoriei.
     * @return Categoria gasita.
     * @throws NotFoundException daca nu exista nicio categorie cu ID-ul dat.
     */
    public Categorie getById(Long id) {
        return categorieDao.findById(id)
                .orElseThrow(() -> new NotFoundException("Categoria cu id " + id + " nu există"));
    }

    /**
     * Creeaza o categorie noua.
     * @param categorie Datele categoriei; {@code nume} este obligatoriu.
     * @return Categoria creata cu ID-ul generat de baza de date.
     * @throws IllegalArgumentException daca {@code nume} este nul sau gol.
     */
    public Categorie create(Categorie categorie) {
        if (categorie.getNume() == null || categorie.getNume().isBlank())
            throw new IllegalArgumentException("Numele categoriei este obligatoriu");
        return categorieDao.save(categorie);
    }

    /**
     * Actualizeaza numele unei categorii existente.
     * @param id        Identificatorul categoriei de actualizat.
     * @param categorie Noile date; {@code nume} este obligatoriu.
     * @return Categoria actualizata.
     * @throws IllegalArgumentException daca {@code nume} este nul sau gol.
     * @throws NotFoundException daca nu exista nicio categorie cu ID-ul dat.
     */
    public Categorie update(Long id, Categorie categorie) {
        if (categorie.getNume() == null || categorie.getNume().isBlank())
            throw new IllegalArgumentException("Numele categoriei este obligatoriu");
        int rows = categorieDao.update(id, categorie);
        if (rows == 0) throw new NotFoundException("Categoria cu id " + id + " nu există");
        return categorieDao.findById(id).orElseThrow();
    }

    /**
     * Sterge o categorie dupa ID.
     * @param id Identificatorul categoriei de sters.
     * @throws NotFoundException daca nu exista nicio categorie cu ID-ul dat.
     */
    public void delete(Long id) {
        int rows = categorieDao.deleteById(id);
        if (rows == 0) throw new NotFoundException("Categoria cu id " + id + " nu există");
    }
}
