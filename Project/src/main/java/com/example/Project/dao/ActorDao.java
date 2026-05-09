package com.example.Project.dao;

import com.example.Project.dto.response.ActorDistributieResponse;
import com.example.Project.model.actor.Actor;
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

@Repository
public class ActorDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final RowMapper<Actor> ROW_MAPPER = (rs, rowNum) -> new Actor(
            rs.getLong("id"),
            rs.getString("nume_scena"),
            rs.getString("nume"),
            rs.getString("prenume"),
            rs.getDate("data_nastere") != null ? rs.getDate("data_nastere").toLocalDate() : null
    );

    private static final RowMapper<ActorDistributieResponse> DISTRIBUTIE_MAPPER = (rs, rowNum) -> new ActorDistributieResponse(
            rs.getLong("id_actor"),
            rs.getString("nume_scena"),
            rs.getString("nume"),
            rs.getString("prenume"),
            rs.getString("rol")
    );

    public List<Actor> findAll() {
        return jdbcTemplate.query(
                "SELECT id, nume_scena, nume, prenume, data_nastere FROM actori ORDER BY id",
                ROW_MAPPER);
    }

    public Optional<Actor> findById(Long id) {
        List<Actor> results = jdbcTemplate.query(
                "SELECT id, nume_scena, nume, prenume, data_nastere FROM actori WHERE id = ?",
                ROW_MAPPER, id);
        return results.stream().findFirst();
    }

    public Actor save(Actor actor) {
        String sql = "INSERT INTO actori (nume_scena, nume, prenume, data_nastere) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, actor.getNumeScena());
            ps.setString(2, actor.getNume());
            ps.setString(3, actor.getPrenume());
            ps.setDate(4, actor.getDataNastere() != null ? Date.valueOf(actor.getDataNastere()) : null);
            return ps;
        }, keyHolder);
        actor.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return actor;
    }

    public int update(Long id, Actor actor) {
        return jdbcTemplate.update(
                "UPDATE actori SET nume_scena = NVL(?, nume_scena), nume = NVL(?, nume), " +
                "prenume = NVL(?, prenume), data_nastere = NVL(?, data_nastere) WHERE id = ?",
                actor.getNumeScena(),
                actor.getNume(),
                actor.getPrenume(),
                actor.getDataNastere() != null ? Date.valueOf(actor.getDataNastere()) : null,
                id);
    }

    public int deleteById(Long id) {
        return jdbcTemplate.update("DELETE FROM actori WHERE id = ?", id);
    }

    public List<ActorDistributieResponse> findByFilmId(Long idFilm) {
        String sql = "SELECT a.id AS id_actor, a.nume_scena, a.nume, a.prenume, d.rol " +
                     "FROM actori a JOIN distributie d ON a.id = d.id_actor " +
                     "WHERE d.id_film = ?";
        return jdbcTemplate.query(sql, DISTRIBUTIE_MAPPER, idFilm);
    }
}
