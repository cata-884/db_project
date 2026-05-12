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

/**
 * Componenta de acces la date (DAO) pentru entitatea Actor.
 * Gestioneaza operatiile CRUD si interogarile specifice folosind JdbcTemplate.
 */
@Repository
public class ActorDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Sablon (template) pentru maparea unui rand din tabelul 'actori' catre obiectul Java Actor.
     * Transforma datele brute din ResultSet in instanta de entitate.
     */
    private static final RowMapper<Actor> ROW_MAPPER = (rs, rowNum) -> new Actor(
            rs.getLong("id"),
            rs.getString("nume_scena"),
            rs.getString("nume"),
            rs.getString("prenume"),
            rs.getDate("data_nastere") != null ? rs.getDate("data_nastere").toLocalDate() : null
    );

    /**
     * Sablon (template) specializat pentru maparea rezultatelor de tip JOIN.
     */
    private static final RowMapper<ActorDistributieResponse> DISTRIBUTIE_MAPPER = (rs, rowNum) -> new ActorDistributieResponse(
            rs.getLong("id_actor"),
            rs.getString("nume_scena"),
            rs.getString("nume"),
            rs.getString("prenume"),
            rs.getString("rol")
    );

    /**
     * Executa un query SQL pentru a extrage toti actorii.
     * @return O lista de obiecte Actor sortate dupa ID.
     */
    public List<Actor> findAll() {
        return jdbcTemplate.query(
                "SELECT id, nume_scena, nume, prenume, data_nastere FROM actori ORDER BY id",
                ROW_MAPPER);
    }

    /**
     * Cauta un actor dupa cheia primara.
     * @param id Identificatorul unic al actorului.
     * @return Un Optional ce contine Actorul daca a fost gasit, sau gol daca nu.
     */
    public Optional<Actor> findById(Long id) {
        List<Actor> results = jdbcTemplate.query(
                "SELECT id, nume_scena, nume, prenume, data_nastere FROM actori WHERE id = ?",
                ROW_MAPPER, id);
        return results.stream().findFirst();
    }

    /**
     * Insereaza un actor nou in baza de date.
     * Utilizeaza KeyHolder pentru a recupera ID-ul generat automat de sistem.
     * @param actor Obiectul actor cu datele de inserat.
     * @return Obiectul actor actualizat cu ID-ul din baza de date.
     */
    public Actor save(Actor actor) {
        String sql = "INSERT INTO actori (nume_scena, nume, prenume, data_nastere) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            /*  Dupa terminarea statementului, extrage valoare id si
                este pusa intr-o zona creata de prepared statement, generated keys
                si e preluata de keyholder
            */
            PreparedStatement ps = con.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, actor.getNumeScena());
            ps.setString(2, actor.getNume());
            ps.setString(3, actor.getPrenume());
            ps.setDate(4, actor.getDataNastere() != null ? Date.valueOf(actor.getDataNastere()) : null);
            return ps;
        }, keyHolder);
        //seteaza id-ul pentru obiectul nou
        actor.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return actor;
    }

    /**
     * Actualizeaza datele unui actor existent.
     * Foloseste NVL pentru a pastra valorile vechi in cazul in care parametrii noi sunt null.
     * @param id Identificatorul actorului de actualizat.
     * @param actor Obiectul ce contine noile valori.
     * @return Numarul de randuri influentate in baza de date.
     */
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

    /**
     * Sterge un actor din baza de date dupa ID.
     * @param id Identificatorul actorului.
     * @return Numarul de randuri sterse.
     */
    public int deleteById(Long id) {
        return jdbcTemplate.update("DELETE FROM actori WHERE id = ?", id);
    }

    /**
     * Executa o interogare complexa cu JOIN pentru a gasi actorii dintr-un film specific.
     * @param idFilm Identificatorul filmului.
     * @return O lista de DTO-uri care includ si rolul jucat de fiecare actor.
     */
    public List<ActorDistributieResponse> findByFilmId(Long idFilm) {
        String sql = "SELECT a.id AS id_actor, a.nume_scena, a.nume, a.prenume, d.rol " +
                "FROM actori a JOIN distributie d ON a.id = d.id_actor " +
                "WHERE d.id_film = ?";
        return jdbcTemplate.query(sql, DISTRIBUTIE_MAPPER, idFilm);
    }
}