package com.example.Project.dao;

import com.example.Project.dto.request.CreateRecenzieActorRequest;
import com.example.Project.model.recenzie.RecenziiActori;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RecenzieActorDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final RowMapper<RecenziiActori> ROW_MAPPER = (rs, rowNum) -> new RecenziiActori(
            rs.getLong("id_recenzie"),
            rs.getLong("id_actor"),
            rs.getString("comentariu")
    );

    // INSERT poate declansa trg_validare_actor_recenzie (ORA-20001 daca actorul nu e in distributia filmului)
    public RecenziiActori insert(CreateRecenzieActorRequest req) {
        jdbcTemplate.update(
                "INSERT INTO recenzii_actori (id_recenzie, id_actor, comentariu) VALUES (?, ?, ?)",
                req.getIdRecenzie(), req.getIdActor(), req.getComentariu());
        return new RecenziiActori(req.getIdRecenzie(), req.getIdActor(), req.getComentariu());
    }

    public List<RecenziiActori> findByRecenzieId(Long idRecenzie) {
        return jdbcTemplate.query(
                "SELECT id_recenzie, id_actor, comentariu FROM recenzii_actori WHERE id_recenzie = ?",
                ROW_MAPPER, idRecenzie);
    }

    public int delete(Long idRecenzie, Long idActor) {
        return jdbcTemplate.update(
                "DELETE FROM recenzii_actori WHERE id_recenzie = ? AND id_actor = ?",
                idRecenzie, idActor);
    }
}
