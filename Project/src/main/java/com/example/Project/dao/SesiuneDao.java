package com.example.Project.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

/**
 * Componenta de acces la date (DAO) pentru gestionarea sesiunilor de autentificare.
 * Gestioneaza operatiile CRUD asupra tabelului {@code sesiuni}: creare, validare,
 * stergere individuala si curatare automata a sesiunilor expirate.
 */
@Repository
public class SesiuneDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /** Durata de viata a unui token de sesiune in ore. */
    private static final long DURATA_ORE = 24;

    /**
     * Genereaza un token UUID unic, il insereaza in tabelul {@code sesiuni} cu o data de expirare
     * de {@value DURATA_ORE} ore de la momentul curent si returneaza token-ul generat.
     * @param idClient Identificatorul clientului pentru care se creeaza sesiunea.
     * @return Token-ul Bearer generat (UUID fara cratime, 32 caractere).
     */
    public String creeaza(Long idClient) {
        //e.g: 550e8400-e29b-41d4-a716-446655440000
        String token = UUID.randomUUID().toString().replace("-", "");
        Timestamp expira = Timestamp.from(Instant.now().plus(DURATA_ORE, ChronoUnit.HOURS));
        jdbcTemplate.update(
            "INSERT INTO sesiuni (token, id_client, expira_la) VALUES (?, ?, ?)",
            token, idClient, expira
        );
        return token;
    }

    /**
     * Cauta ID-ul clientului asociat unui token valid si neexpirat.
     * @param token Token-ul Bearer de cautat; returneaza gol daca este {@code null} sau blank.
     * @return Optional cu ID-ul clientului, sau gol daca token-ul este invalid sau expirat.
     */
    public Optional<Long> gasesteClientPentruToken(String token) {
        if (token == null || token.isBlank()) return Optional.empty();
        return jdbcTemplate.query(
            "SELECT id_client FROM sesiuni WHERE token = ? AND expira_la > SYSTIMESTAMP",
            ps -> ps.setString(1, token),
            rs -> rs.next() ? Optional.of(rs.getLong(1)) : Optional.empty()
        );
    }

    /**
     * Sterge sesiunea asociata unui token (logout).
     * @param token Token-ul de sters din tabelul {@code sesiuni}.
     */
    public void sterge(String token) {
        jdbcTemplate.update("DELETE FROM sesiuni WHERE token = ?", token);
    }

    /**
     * Sterge toate sesiunile a caror data de expirare este in trecut.
     * Apelat periodic de {@link com.example.Project.service.SesiuneService#cleanupExpirate()}.
     */
    public void curataExpirate() {
        jdbcTemplate.update("DELETE FROM sesiuni WHERE expira_la < SYSTIMESTAMP");
    }
}
