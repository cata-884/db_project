package com.example.Project.dao;

import com.example.Project.model.film.VersiuneFilm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class VersiuneFilmDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final RowMapper<VersiuneFilm> ROW_MAPPER = (rs, rowNum) -> new VersiuneFilm(
            rs.getLong("id"),
            rs.getLong("id_film"),
            rs.getString("rezolutie"),
            rs.getString("limbi"),
            rs.getString("format")
    );

    public List<VersiuneFilm> findByFilmId(Long idFilm) {
        return jdbcTemplate.query(
                "SELECT id, id_film, rezolutie, limbi, format FROM versiuni_film WHERE id_film = ? ORDER BY id",
                ROW_MAPPER,
                idFilm
        );
    }
}

