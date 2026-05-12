package com.example.Project.dao;

import com.example.Project.model.recenzie.Eticheta;
import com.example.Project.model.recenzie.EtichetaRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Componenta de acces la date (DAO) pentru entitatea Eticheta.
 * Gestioneaza operatiile de citire din tabelul {@code etichete} folosind JdbcTemplate.
 */
@Repository
public class EtichetaDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Sablon (template) pentru maparea unui rand din tabelul {@code etichete} catre obiectul {@link EtichetaRecord}.
     * Coloana {@code denumire} este convertita la valoarea enum {@link Eticheta} corespunzatoare.
     */
    private static final RowMapper<EtichetaRecord> ROW_MAPPER = (rs, rowNum) -> new EtichetaRecord(
            rs.getLong("id"),
            Eticheta.valueOf(rs.getString("denumire")),
            rs.getString("sentiment")
    );

    /**
     * Returneaza toate etichetele din baza de date, ordonate dupa sentiment si denumire.
     * @return Lista etichetelor; lista vida daca tabelul este gol.
     */
    public List<EtichetaRecord> findAll() {
        return jdbcTemplate.query(
                "SELECT id, denumire, sentiment FROM etichete ORDER BY sentiment, denumire",
                ROW_MAPPER);
    }

    /**
     * Cauta o eticheta dupa cheia primara.
     * @param id Identificatorul unic al etichetei.
     * @return Optional cu eticheta gasita, sau gol daca nu exista.
     */
    public Optional<EtichetaRecord> findById(Long id) {
        List<EtichetaRecord> results = jdbcTemplate.query(
                "SELECT id, denumire, sentiment FROM etichete WHERE id = ?",
                ROW_MAPPER, id);
        return results.stream().findFirst();
    }
}
