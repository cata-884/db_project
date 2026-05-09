package com.example.Project.dao;

import com.example.Project.dto.request.CreateVizualizareRequest;
import com.example.Project.dto.response.IstoricVizionareResponse;
import com.example.Project.model.client.StareVizualizare;
import com.example.Project.model.client.Vizualizari;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Types;
import java.util.List;
import java.util.Optional;

@Repository
public class VizualizareDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final RowMapper<Vizualizari> ROW_MAPPER = (rs, rowNum) -> {
        Object durataObj = rs.getObject("durata");
        Float durata = durataObj != null ? ((Number) durataObj).floatValue() : null;
        String stareStr = rs.getString("stare");
        return new Vizualizari(
                rs.getLong("id"),
                rs.getLong("id_client"),
                rs.getLong("id_versiune"),
                rs.getDate("data_vizualizare") != null ? rs.getDate("data_vizualizare").toLocalDate() : null,
                durata,
                stareStr != null ? StareVizualizare.valueOf(stareStr) : null
        );
    };

    private static final RowMapper<IstoricVizionareResponse> ISTORIC_MAPPER = (rs, rowNum) -> {
        Object durataObj = rs.getObject("durata");
        Float durata = durataObj != null ? ((Number) durataObj).floatValue() : null;
        return new IstoricVizionareResponse(
                rs.getLong("id_vizualizare"),
                rs.getLong("id_film"),
                rs.getString("titlu_film"),
                rs.getString("denumire_versiune"),
                rs.getDate("data_vizualizare") != null ? rs.getDate("data_vizualizare").toLocalDate() : null,
                durata,
                rs.getString("stare")
        );
    };

    public List<Vizualizari> findAll() {
        return jdbcTemplate.query(
                "SELECT id, id_client, id_versiune, data_vizualizare, durata, stare FROM vizualizari ORDER BY id",
                ROW_MAPPER);
    }

    public Optional<Vizualizari> findById(Long id) {
        List<Vizualizari> results = jdbcTemplate.query(
                "SELECT id, id_client, id_versiune, data_vizualizare, durata, stare FROM vizualizari WHERE id = ?",
                ROW_MAPPER, id);
        return results.stream().findFirst();
    }

    // dataVizualizare = CURRENT_DATE — nu se trimite din request
    public Long insert(CreateVizualizareRequest req) {
        String sql = "INSERT INTO vizualizari (id_client, id_versiune, data_vizualizare, durata, stare) " +
                     "VALUES (?, ?, CURRENT_DATE, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, new String[]{"id"});
            ps.setLong(1, req.getIdClient());
            ps.setLong(2, req.getIdVersiune());
            if (req.getDurata() != null) ps.setFloat(3, req.getDurata());
            else ps.setNull(3, Types.NUMERIC);
            ps.setString(4, req.getStare() != null ? req.getStare().name() : null);
            return ps;
        }, keyHolder);
        return keyHolder.getKey().longValue();
    }

    public int update(Long id, Vizualizari viz) {
        return jdbcTemplate.update(
                "UPDATE vizualizari SET data_vizualizare = ?, durata = ?, stare = ? WHERE id = ?",
                viz.getDataVizualizare() != null ? Date.valueOf(viz.getDataVizualizare()) : null,
                viz.getDurata(),
                viz.getStare() != null ? viz.getStare().name() : null,
                id);
    }

    public int deleteById(Long id) {
        return jdbcTemplate.update("DELETE FROM vizualizari WHERE id = ?", id);
    }

    public List<IstoricVizionareResponse> findIstoricByClientId(Long idClient) {
        String sql = "SELECT v.id AS id_vizualizare, f.id AS id_film, f.titlu AS titlu_film, " +
                     "vf.rezolutie || ' - ' || vf.format AS denumire_versiune, " +
                     "v.data_vizualizare, v.durata, v.stare " +
                     "FROM vizualizari v " +
                     "JOIN versiuni_film vf ON v.id_versiune = vf.id " +
                     "JOIN filme f ON vf.id_film = f.id " +
                     "WHERE v.id_client = ? ORDER BY v.data_vizualizare DESC";
        return jdbcTemplate.query(sql, ISTORIC_MAPPER, idClient);
    }
}
