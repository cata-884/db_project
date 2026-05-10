package com.example.Project.dao;

import com.example.Project.dto.request.UpdateClientRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Date;

@Repository
public class ClientiDao {

    private final JdbcTemplate jdbcTemplate;

    public ClientiDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

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

