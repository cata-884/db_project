package com.example.Project.dao;

import com.example.Project.dto.request.UpdateClientRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Date;

/**
 * Componenta de acces la date (DAO) pentru actualizarea datelor de contact ale clientilor.
 * Gestioneaza exclusiv operatia de UPDATE pe tabelul {@code clienti} folosind JdbcTemplate.
 */
@Repository
public class ClientiDao {

    private final JdbcTemplate jdbcTemplate;

    public ClientiDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Actualizeaza datele de contact ale unui client (telefon, adresa, oras, data nasterii).
     * Campurile {@code null} din request vor suprascrie explicit valorile existente.
     * @param id  Identificatorul clientului de actualizat.
     * @param req Obiectul cu noile date de contact.
     * @return Numarul de randuri afectate (1 daca clientul a fost gasit, 0 altfel).
     */
    public int update(Long id, UpdateClientRequest req) {
        return jdbcTemplate.update(
                "UPDATE clienti SET " +
                "telefon_fix_cod = ?, telefon_fix_nr = ?, " +
                "telefon_mobil_cod = ?, telefon_mobil_nr = ?, " +
                "adresa = ?, oras = ?, data_nastere = ? " +
                "WHERE id = ?",
                req.getTelefonFixCod(),
                req.getTelefonFixNr(),
                req.getTelefonMobilCod(),
                req.getTelefonMobilNr(),
                req.getAdresa(),
                req.getOras(),
                req.getDataNastere() != null ? Date.valueOf(req.getDataNastere()) : null,
                id);
    }
}

