package com.example.Project.dao;

import com.example.Project.dto.request.CreateDistributieRequest;
import com.example.Project.dto.response.ActorDistributieResponse;
import com.example.Project.model.actor.Distributie;
import com.example.Project.model.actor.RolActor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DistributieDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final RowMapper<Distributie> ROW_MAPPER = (rs, rowNum) -> new Distributie(
            rs.getLong("id_film"),
            rs.getLong("id_actor"),
            rs.getString("rol") != null ? RolActor.valueOf(rs.getString("rol")) : null,
            null
    );

    private static final RowMapper<ActorDistributieResponse> ACTOR_MAPPER = (rs, rowNum) -> new ActorDistributieResponse(
            rs.getLong("id_actor"),
            rs.getString("nume_scena"),
            rs.getString("nume"),
            rs.getString("prenume"),
            rs.getString("rol"),
            null
    );

    public Distributie insert(CreateDistributieRequest req) {
        jdbcTemplate.update(
                "INSERT INTO distributie (id_film, id_actor, rol) VALUES (?, ?, ?)",
                req.getIdFilm(), req.getIdActor(), req.getRole().name());
        return new Distributie(req.getIdFilm(), req.getIdActor(), req.getRole(), req.getComentariu());
    }

    public List<ActorDistributieResponse> findActoriByFilmId(Long idFilm) {
        String sql = "SELECT a.id AS id_actor, a.nume_scena, a.nume, a.prenume, d.rol " +
                     "FROM actori a JOIN distributie d ON a.id = d.id_actor " +
                     "WHERE d.id_film = ?";
        return jdbcTemplate.query(sql, ACTOR_MAPPER, idFilm);
    }

    public int delete(Long idFilm, Long idActor) {
        return jdbcTemplate.update(
                "DELETE FROM distributie WHERE id_film = ? AND id_actor = ?",
                idFilm, idActor);
    }
}
