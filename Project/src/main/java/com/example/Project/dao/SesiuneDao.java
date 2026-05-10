package com.example.Project.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

@Repository
public class SesiuneDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final long DURATA_ORE = 24;

    public String creeaza(Long idClient) {
        String token = UUID.randomUUID().toString().replace("-", "");
        Timestamp expira = Timestamp.from(Instant.now().plus(DURATA_ORE, ChronoUnit.HOURS));
        jdbcTemplate.update(
            "INSERT INTO sesiuni (token, id_client, expira_la) VALUES (?, ?, ?)",
            token, idClient, expira
        );
        return token;
    }

    public Optional<Long> gasesteClientPentruToken(String token) {
        if (token == null || token.isBlank()) return Optional.empty();
        return jdbcTemplate.query(
            "SELECT id_client FROM sesiuni WHERE token = ? AND expira_la > SYSTIMESTAMP",
            ps -> ps.setString(1, token),
            rs -> rs.next() ? Optional.of(rs.getLong(1)) : Optional.empty()
        );
    }

    public void sterge(String token) {
        jdbcTemplate.update("DELETE FROM sesiuni WHERE token = ?", token);
    }

    public int curataExpirate() {
        return jdbcTemplate.update("DELETE FROM sesiuni WHERE expira_la < SYSTIMESTAMP");
    }
}
