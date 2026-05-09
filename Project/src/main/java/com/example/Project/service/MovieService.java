package com.example.Project.service;

import com.example.Project.dao.MovieDao;
import com.example.Project.exception.NotFoundException;
import com.example.Project.model.film.Movie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovieService {

    @Autowired
    private MovieDao movieDao;

    public List<Movie> getAll() {
        return movieDao.findAll();
    }

    public Movie getById(Long id) {
        return movieDao.findById(id)
                .orElseThrow(() -> new NotFoundException("Filmul cu id " + id + " nu există"));
    }

    public List<Movie> getByCategorie(Long idCategorie) {
        return movieDao.findByCategorieId(idCategorie);
    }

    public Movie create(Movie movie) {
        if (movie.getTitlu() == null || movie.getTitlu().isBlank())
            throw new IllegalArgumentException("Titlul filmului este obligatoriu");
        return movieDao.save(movie);
    }

    public Movie update(Long id, Movie movie) {
        if (movie.getTitlu() == null || movie.getTitlu().isBlank())
            throw new IllegalArgumentException("Titlul filmului este obligatoriu");
        int rows = movieDao.update(id, movie);
        if (rows == 0) throw new NotFoundException("Filmul cu id " + id + " nu există");
        return movieDao.findById(id).orElseThrow();
    }

    public void delete(Long id) {
        int rows = movieDao.deleteById(id);
        if (rows == 0) throw new NotFoundException("Filmul cu id " + id + " nu există");
    }
}
