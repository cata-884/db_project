package com.example.Project.dao;

import com.example.Project.dto.request.UpdateVizualizareRequest;
import com.example.Project.dto.response.IstoricVizionareResponse;
import com.example.Project.model.client.StareVizualizare;
import com.example.Project.model.client.Vizualizari;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

/**
 * Componenta de acces la date (DAO) pentru entitatea Vizualizari.
 * Gestioneaza operatiile CRUD si interogarile cu JOIN asupra tabelului {@code vizualizari}
 * folosind JdbcTemplate.
 */
@Repository
public class VizualizareDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Sablon (template) pentru maparea unui rand din tabelul {@code vizualizari} catre obiectul {@link Vizualizari}.
     * Coloana {@code stare} este convertita la enum {@link StareVizualizare}; coloana {@code data_vizualizare}
     * si {@code durata} sunt tratate ca nullable.
     */
    private static final RowMapper<Vizualizari> ROW_MAPPER = (rs, rowNum) ->
            new Vizualizari(
                    rs.getLong("id"),
                    rs.getLong("id_client"),
                    rs.getLong("id_versiune"),
                    rs.getDate("data_vizualizare") != null ? rs.getDate("data_vizualizare").toLocalDate() : null,
                    rs.getObject("durata", Integer.class),
                    rs.getString("stare") != null ? StareVizualizare.valueOf(rs.getString("stare")) : null
            );

    /**
     * Sablon (template) specializat pentru maparea rezultatelor interogarii cu JOIN
     * {@code vizualizari - versiuni_film - filme} catre {@link IstoricVizionareResponse}.
     */
    private static final RowMapper<IstoricVizionareResponse> ISTORIC_MAPPER = (rs, rowNum) ->
            new IstoricVizionareResponse(
                    rs.getLong("id_vizualizare"),
                    rs.getLong("id_film"),
                    rs.getString("titlu_film"),
                    rs.getString("denumire_versiune"),
                    rs.getDate("data_vizualizare") != null ? rs.getDate("data_vizualizare").toLocalDate() : null,
                    rs.getObject("durata", Integer.class),
                    rs.getString("stare")
            );

    /**
     * Returneaza toate vizualizarile din baza de date, ordonate dupa ID.
     * @return Lista vizualizarilor; lista vida daca tabelul este gol.
     */
    public List<Vizualizari> findAll() {
        return jdbcTemplate.query(
                "SELECT id, id_client, id_versiune, data_vizualizare, durata, stare FROM vizualizari ORDER BY id",
                ROW_MAPPER);
    }

    /**
     * Cauta o vizualizare dupa cheia primara.
     * @param id Identificatorul unic al vizualizarii.
     * @return Optional cu vizualizarea gasita, sau gol daca nu exista.
     */
    public Optional<Vizualizari> findById(Long id) {
        List<Vizualizari> results = jdbcTemplate.query(
                "SELECT id, id_client, id_versiune, data_vizualizare, durata, stare FROM vizualizari WHERE id = ?",
                ROW_MAPPER, id);
        return results.stream().findFirst();
    }

    /**
     * Insereaza o noua vizualizare cu data curenta si returneaza ID-ul generat.
     * @param idClient   Identificatorul clientului.
     * @param idVersiune Identificatorul versiunii de film vizionate.
     * @return ID-ul generat de baza de date pentru noua vizualizare.
     */
    public Long insert(Long idClient, Long idVersiune) {
        String sql = "INSERT INTO vizualizari (id_client, id_versiune, data_vizualizare) VALUES (?, ?, CURRENT_DATE)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, new String[]{"id"});
            ps.setLong(1, idClient);
            ps.setLong(2, idVersiune);
            return ps;
        }, keyHolder);
        return keyHolder.getKey().longValue();
    }

    /**
     * Actualizeaza durata si/sau starea unei vizualizari. Foloseste NVL pentru a pastra valorile existente.
     * @param id  Identificatorul vizualizarii de actualizat.
     * @param req Noile valori; campurile {@code null} nu suprascriu datele existente.
     * @return Numarul de randuri afectate (1 daca vizualizarea exista, 0 altfel).
     */
    public int updateProgress(Long id, UpdateVizualizareRequest req) {
        return jdbcTemplate.update(
                "UPDATE vizualizari SET durata = NVL(?, durata), stare = NVL(?, stare) WHERE id = ?",
                req.getDurata(),
                req.getStare() != null ? req.getStare().name() : null,
                id);
    }

    /**
     * Sterge o vizualizare din baza de date dupa ID.
     * @param id Identificatorul vizualizarii de sters.
     * @return Numarul de randuri sterse (1 daca a existat, 0 altfel).
     */
    public int deleteById(Long id) {
        return jdbcTemplate.update("DELETE FROM vizualizari WHERE id = ?", id);
    }

    /**
     * Returneaza istoricul de vizionare al unui client cu detalii despre film si versiune.
     * Executa un JOIN intre {@code vizualizari}, {@code versiuni_film} si {@code filme}.
     * @param idClient Identificatorul clientului.
     * @return Lista vizionarilor cu detalii, ordonata descrescator dupa data vizionarii.
     */
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
