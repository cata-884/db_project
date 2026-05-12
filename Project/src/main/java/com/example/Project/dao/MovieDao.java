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
import java.util.Objects;
import java.util.Optional;

/**
 * Componenta de acces la date (DAO) pentru entitatea Movie (film).
 * Gestioneaza operatiile CRUD si interogarile specifice asupra tabelului {@code filme} folosind JdbcTemplate.
 */
@Repository
public class MovieDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Sablon (template) pentru maparea unui rand din tabelul {@code filme} catre obiectul {@link Movie}.
     * Coloana {@code rating} este tratata ca nullable si convertita explicit la {@link Double}.
     * La ORDER BY pe coloane nullable se foloseste {@code NULLS LAST} — in Oracle {@code DESC}
     * plaseaza NULL-urile primele implicit.
     */
    private static final RowMapper<Movie> ROW_MAPPER = (rs, rowNum) -> {
        Object ratingObj = rs.getObject("rating");
        Double rating = ratingObj != null ? ((Number) ratingObj).doubleValue() : null;
        return new Movie(
                rs.getLong("id"),
                rs.getString("titlu"),
                rs.getString("descriere"),
                rs.getDate("data_lansare") != null ? rs.getDate("data_lansare").toLocalDate() : null,
                rs.getLong("id_categorie"),
                rating
        );
    };

    /**
     * Returneaza toate filmele din catalog, ordonate dupa ID.
     * @return Lista filmelor; lista vida daca tabelul este gol.
     */
    public List<Movie> findAll() {
        return jdbcTemplate.query(
                "SELECT id, titlu, descriere, data_lansare, id_categorie, rating FROM filme ORDER BY id",
                ROW_MAPPER);
    }

    /**
     * Cauta un film dupa cheia primara.
     * @param id Identificatorul unic al filmului.
     * @return Optional cu filmul gasit, sau gol daca nu exista.
     */
    public Optional<Movie> findById(Long id) {
        List<Movie> results = jdbcTemplate.query(
                "SELECT id, titlu, descriere, data_lansare, id_categorie, rating FROM filme WHERE id = ?",
                ROW_MAPPER, id);
        return results.stream().findFirst();
    }

    /**
     * Returneaza filmele dintr-o categorie, ordonate descrescator dupa rating (NULL-urile la final).
     * @param idCategorie Identificatorul categoriei.
     * @return Lista filmelor din categoria data.
     */
    public List<Movie> findByCategorieId(Long idCategorie) {
        return jdbcTemplate.query(
                "SELECT id, titlu, descriere, data_lansare, id_categorie, rating FROM filme WHERE id_categorie = ? ORDER BY rating DESC NULLS LAST",
                ROW_MAPPER, idCategorie);
    }

    /**
     * Insereaza un film nou si ii seteaza ID-ul generat de baza de date.
     * @param movie Obiectul cu datele filmului de inserat.
     * @return Obiectul film actualizat cu ID-ul generat.
     */
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
        movie.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return movie;
    }

    /**
     * Actualizeaza selectiv campurile unui film existent. Foloseste NVL pentru a pastra valorile existente.
     * @param id    Identificatorul filmului de actualizat.
     * @param movie Obiectul cu noile valori; campurile {@code null} nu suprascriu datele existente.
     * @return Numarul de randuri afectate (1 daca filmul a fost gasit, 0 altfel).
     */
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

    /**
     * Sterge un film din baza de date dupa ID.
     * @param id Identificatorul filmului de sters.
     * @return Numarul de randuri sterse (1 daca a existat, 0 altfel).
     */
    public int deleteById(Long id) {
        return jdbcTemplate.update("DELETE FROM filme WHERE id = ?", id);
    }
}
