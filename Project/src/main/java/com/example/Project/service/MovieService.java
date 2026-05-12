package com.example.Project.service;

import com.example.Project.dao.MovieDao;
import com.example.Project.exception.NotFoundException;
import com.example.Project.model.film.Movie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Serviciu pentru logica de business a entitatii Movie (film).
 * Coordoneaza operatiile CRUD si validarile inainte de a delega catre {@link MovieDao}.
 */
@Service
public class MovieService {

    @Autowired
    private MovieDao movieDao;

    /**
     * Returneaza toate filmele din catalog.
     * @return Lista filmelor, ordonata dupa ID.
     */
    public List<Movie> getAll() {
        return movieDao.findAll();
    }

    /**
     * Returneaza un film dupa ID.
     * @param id Identificatorul filmului.
     * @return Filmul gasit.
     * @throws NotFoundException daca nu exista niciun film cu ID-ul dat.
     */
    public Movie getById(Long id) {
        return movieDao.findById(id)
                .orElseThrow(() -> new NotFoundException("Filmul cu id " + id + " nu există"));
    }

    /**
     * Returneaza filmele dintr-o categorie, ordonate descrescator dupa rating.
     * @param idCategorie Identificatorul categoriei.
     * @return Lista filmelor din categoria data.
     */
    public List<Movie> getByCategorie(Long idCategorie) {
        return movieDao.findByCategorieId(idCategorie);
    }

    /**
     * Creeaza un film nou dupa validarea campurilor obligatorii.
     * @param movie Datele filmului; {@code titlu} este obligatoriu.
     * @return Filmul creat cu ID-ul generat de baza de date.
     * @throws IllegalArgumentException daca {@code titlu} este nul sau gol.
     */
    public Movie create(Movie movie) {
        if (movie.getTitlu() == null || movie.getTitlu().isBlank())
            throw new IllegalArgumentException("Titlul filmului este obligatoriu");
        return movieDao.save(movie);
    }

    /**
     * Actualizeaza datele unui film existent.
     * @param id    Identificatorul filmului de actualizat.
     * @param movie Noile date; {@code titlu} este obligatoriu.
     * @return Filmul actualizat.
     * @throws IllegalArgumentException daca {@code titlu} este nul sau gol.
     * @throws NotFoundException daca nu exista niciun film cu ID-ul dat.
     */
    public Movie update(Long id, Movie movie) {
        if (movie.getTitlu() == null || movie.getTitlu().isBlank())
            throw new IllegalArgumentException("Titlul filmului este obligatoriu");
        int rows = movieDao.update(id, movie);
        if (rows == 0) throw new NotFoundException("Filmul cu id " + id + " nu există");
        return movieDao.findById(id).orElseThrow();
    }

    /**
     * Sterge un film dupa ID.
     * @param id Identificatorul filmului de sters.
     * @throws NotFoundException daca nu exista niciun film cu ID-ul dat.
     */
    public void delete(Long id) {
        int rows = movieDao.deleteById(id);
        if (rows == 0) throw new NotFoundException("Filmul cu id " + id + " nu există");
    }
}
