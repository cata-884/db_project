package com.example.Project.dao;

import com.example.Project.dto.request.UpdateRecenzieRequest;
import com.example.Project.dto.response.RecenzieDetailResponse;
import com.example.Project.dto.response.RecenzieResponse;
import com.example.Project.model.recenzie.Recenzii;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class RecenzieDao {

    private final JdbcTemplate jdbcTemplate;

    private static final RowMapper<RecenzieResponse> RESPONSE_MAPPER = (rs, rowNum) -> {
        Object notaObj = rs.getObject("nota");
        Integer nota = notaObj != null ? ((Number) notaObj).intValue() : null;
        return new RecenzieResponse(
                rs.getLong("id"),
                rs.getLong("id_client"),
                rs.getLong("id_film"),
                nota,
                rs.getString("sentiment"),
                rs.getString("text_comentariu"),
                rs.getTimestamp("data_postare") != null ? rs.getTimestamp("data_postare").toLocalDateTime() : null
        );
    };

    private static final RowMapper<RecenzieDetailResponse> DETAIL_MAPPER = (rs, rowNum) -> {
        Object notaObj = rs.getObject("nota");
        Integer nota = notaObj != null ? ((Number) notaObj).intValue() : null;
        return new RecenzieDetailResponse(
                rs.getLong("id"),
                rs.getLong("id_client"),
                rs.getString("nume_client"),
                rs.getLong("id_film"),
                rs.getString("titlu_film"),
                nota,
                rs.getString("sentiment"),
                rs.getString("text_comentariu"),
                rs.getTimestamp("data_postare") != null ? rs.getTimestamp("data_postare").toLocalDateTime() : null
        );
    };

    private static final String DETAIL_SELECT =
            "SELECT r.id, r.id_client, c.nume || ' ' || c.prenume AS nume_client, " +
            "r.id_film, f.titlu AS titlu_film, r.nota, r.sentiment, r.text_comentariu, r.data_postare " +
            "FROM recenzii r " +
            "JOIN clienti c ON r.id_client = c.id " +
            "JOIN filme f ON r.id_film = f.id ";

    public RecenzieDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<RecenzieResponse> findAll() {
        return jdbcTemplate.query(
                "SELECT id, id_client, id_film, nota, sentiment, text_comentariu, data_postare FROM recenzii ORDER BY id",
                RESPONSE_MAPPER);
    }

    public Optional<RecenzieResponse> findById(Long id) {
        List<RecenzieResponse> results = jdbcTemplate.query(
                "SELECT id, id_client, id_film, nota, sentiment, text_comentariu, data_postare FROM recenzii WHERE id = ?",
                RESPONSE_MAPPER, id);
        return results.stream().findFirst();
    }

    public Optional<RecenzieDetailResponse> findDetailById(Long id) {
        List<RecenzieDetailResponse> results = jdbcTemplate.query(
                DETAIL_SELECT + "WHERE r.id = ?",
                DETAIL_MAPPER, id);
        return results.stream().findFirst();
    }

    // sentiment = NULL la INSERT — triggerul trg_set_sentiment_recenzie il completeaza
    // data_postare nu se trimite — are DEFAULT CURRENT_TIMESTAMP in BD
    public Long insert(Recenzii recenzie) {
        String sql = "INSERT INTO recenzii (id_client, id_film, nota, sentiment, text_comentariu) VALUES (?, ?, ?, NULL, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, new String[]{"id"});
            ps.setLong(1, recenzie.getIdClient());
            ps.setLong(2, recenzie.getIdFilm());
            ps.setInt(3, recenzie.getNota());
            ps.setString(4, recenzie.getTextComentariu());
            return ps;
        }, keyHolder);
        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    public int update(Long id, UpdateRecenzieRequest req) {
        return jdbcTemplate.update(
                "UPDATE recenzii SET nota = ?, text_comentariu = ? WHERE id = ?",
                req.getNota(), req.getTextComentariu(), id);
    }

    public int deleteById(Long id) {
        return jdbcTemplate.update("DELETE FROM recenzii WHERE id = ?", id);
    }

    public List<RecenzieDetailResponse> findByFilmId(Long idFilm) {
        return jdbcTemplate.query(
                DETAIL_SELECT + "WHERE r.id_film = ? ORDER BY r.data_postare DESC",
                DETAIL_MAPPER, idFilm);
    }

    public List<RecenzieResponse> findByClientId(Long idClient) {
        return jdbcTemplate.query(
                "SELECT id, id_client, id_film, nota, sentiment, text_comentariu, data_postare " +
                "FROM recenzii WHERE id_client = ? ORDER BY data_postare DESC",
                RESPONSE_MAPPER, idClient);
    }
}
