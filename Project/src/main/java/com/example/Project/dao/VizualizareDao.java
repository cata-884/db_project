package com.example.Project.dao;

import com.example.Project.dto.request.CreateVizualizareRequest;
import com.example.Project.dto.response.IstoricVizionareResponse;
import com.example.Project.model.client.Vizualizari;
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
public class VizualizareDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final RowMapper<Vizualizari> ROW_MAPPER = (rs, rowNum) ->
            new Vizualizari(
                    rs.getLong("id"),
                    rs.getLong("id_client"),
                    rs.getLong("id_versiune"),
                    rs.getDate("data_vizualizare") != null ? rs.getDate("data_vizualizare").toLocalDate() : null
            );

    private static final RowMapper<IstoricVizionareResponse> ISTORIC_MAPPER = (rs, rowNum) ->
            new IstoricVizionareResponse(
                    rs.getLong("id_vizualizare"),
                    rs.getLong("id_film"),
                    rs.getString("titlu_film"),
                    rs.getString("denumire_versiune"),
                    rs.getDate("data_vizualizare") != null ? rs.getDate("data_vizualizare").toLocalDate() : null
            );

    public List<Vizualizari> findAll() {
        return jdbcTemplate.query(
                "SELECT id, id_client, id_versiune, data_vizualizare FROM vizualizari ORDER BY id",
                ROW_MAPPER);
    }

    public Optional<Vizualizari> findById(Long id) {
        List<Vizualizari> results = jdbcTemplate.query(
                "SELECT id, id_client, id_versiune, data_vizualizare FROM vizualizari WHERE id = ?",
                ROW_MAPPER, id);
        return results.stream().findFirst();
    }

    public Long insert(CreateVizualizareRequest req) {
        String sql = "INSERT INTO vizualizari (id_client, id_versiune, data_vizualizare) VALUES (?, ?, CURRENT_DATE)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, new String[]{"id"});
            ps.setLong(1, req.getIdClient());
            ps.setLong(2, req.getIdVersiune());
            return ps;
        }, keyHolder);
        return keyHolder.getKey().longValue();
    }

    public int update(Long id, Vizualizari viz) {
        return jdbcTemplate.update(
                "UPDATE vizualizari SET data_vizualizare = NVL(?, data_vizualizare) WHERE id = ?",
                viz.getDataVizualizare() != null ? Date.valueOf(viz.getDataVizualizare()) : null,
                id);
    }

    public int deleteById(Long id) {
        return jdbcTemplate.update("DELETE FROM vizualizari WHERE id = ?", id);
    }

    public List<IstoricVizionareResponse> findIstoricByClientId(Long idClient) {
        String sql = "SELECT v.id AS id_vizualizare, f.id AS id_film, f.titlu AS titlu_film, " +
                     "vf.rezolutie || ' - ' || vf.format AS denumire_versiune, " +
                     "v.data_vizualizare " +
                     "FROM vizualizari v " +
                     "JOIN versiuni_film vf ON v.id_versiune = vf.id " +
                     "JOIN filme f ON vf.id_film = f.id " +
                     "WHERE v.id_client = ? ORDER BY v.data_vizualizare DESC";
        return jdbcTemplate.query(sql, ISTORIC_MAPPER, idClient);
    }
}
