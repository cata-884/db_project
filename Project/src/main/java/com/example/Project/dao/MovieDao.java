package com.example.Project.dao;

import com.example.Project.model.film.Movie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

@Repository
public class MovieDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // NOTA: la orice ORDER BY pe rating sau alte coloane nullable, foloseste NULLS LAST
    //       in Oracle DESC pune NULL-urile primele, ceea ce nu e ce vrem
    private static final RowMapper<Movie> ROW_MAPPER = (rs, rowNum) -> new Movie(
            rs.getLong("id"),
            rs.getString("titlu"),
            rs.getString("descriere"),
            rs.getDate("data_lansare") != null ? rs.getDate("data_lansare").toLocalDate() : null,
            rs.getLong("id_categorie"),
            rs.getDouble("rating")
    );

    public List<Movie> findAll() {
        return jdbcTemplate.query(
                "SELECT id, titlu, descriere, data_lansare, id_categorie, rating FROM filme ORDER BY id",
                ROW_MAPPER);
    }

    public Optional<Movie> findById(Long id) {
        List<Movie> results = jdbcTemplate.query(
                "SELECT id, titlu, descriere, data_lansare, id_categorie, rating FROM filme WHERE id = ?",
                ROW_MAPPER, id);
        return results.stream().findFirst();
    }

    public List<Movie> findByCategorieId(Long idCategorie) {
        return jdbcTemplate.query(
                "SELECT id, titlu, descriere, data_lansare, id_categorie, rating FROM filme WHERE id_categorie = ? ORDER BY rating DESC NULLS LAST",
                ROW_MAPPER, idCategorie);
    }

    public Movie save(Movie movie) {
        String sql = "INSERT INTO filme (titlu, descriere, data_lansare, id_categorie, rating) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, movie.getTitlu());
            ps.setString(2, movie.getDescriere());
            ps.setDate(3, movie.getDataLansare() != null ? Date.valueOf(movie.getDataLansare()) : null);
            ps.setObject(4, movie.getIdCategorie());
            ps.setObject(5, movie.getRating());
            return ps;
        }, keyHolder);
        movie.setId(keyHolder.getKey().longValue());
        return movie;
    }

    public int update(Long id, Movie movie) {
        return jdbcTemplate.update(
                "UPDATE filme SET titlu = NVL(?, titlu), descriere = NVL(?, descriere), " +
                "data_lansare = NVL(?, data_lansare), id_categorie = NVL(?, id_categorie), " +
                "rating = NVL(?, rating) WHERE id = ?",
                movie.getTitlu(),
                movie.getDescriere(),
                movie.getDataLansare() != null ? Date.valueOf(movie.getDataLansare()) : null,
                movie.getIdCategorie(),
                movie.getRating(),
                id);
    }

    public int deleteById(Long id) {
        return jdbcTemplate.update("DELETE FROM filme WHERE id = ?", id);
    }
}
