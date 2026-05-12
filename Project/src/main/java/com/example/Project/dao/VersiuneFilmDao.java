package com.example.Project.dao;

import com.example.Project.model.film.VersiuneFilm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Componenta de acces la date (DAO) pentru entitatea VersiuneFilm.
 * Gestioneaza interogarile asupra tabelului {@code versiuni_film} folosind JdbcTemplate.
 */
@Repository
public class VersiuneFilmDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Sablon (template) pentru maparea unui rand din tabelul {@code versiuni_film}
     * catre obiectul {@link VersiuneFilm}.
     */
    private static final RowMapper<VersiuneFilm> ROW_MAPPER = (rs, rowNum) -> new VersiuneFilm(
            rs.getLong("id"),
            rs.getLong("id_film"),
            rs.getString("rezolutie"),
            rs.getString("limbi"),
            rs.getString("format")
    );

    /**
     * Returneaza toate versiunile unui film, ordonate dupa ID.
     * @param idFilm Identificatorul filmului parinte.
     * @return Lista versiunilor filmului; lista vida daca filmul nu are versiuni.
     */
    public List<VersiuneFilm> findByFilmId(Long idFilm) {
        return jdbcTemplate.query(
                "SELECT id, id_film, rezolutie, limbi, format FROM versiuni_film WHERE id_film = ? ORDER BY id",
                ROW_MAPPER,
                idFilm
        );
    }
}

