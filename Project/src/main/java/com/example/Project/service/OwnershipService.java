package com.example.Project.service;

import com.example.Project.exception.ForbiddenException;
import com.example.Project.exception.NotFoundException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class OwnershipService {

    private final JdbcTemplate jdbcTemplate;

    public OwnershipService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void verificaRecenzie(Long idRecenzie, Long idClientCurent) {
        Long owner = jdbcTemplate.query(
                "SELECT id_client FROM recenzii WHERE id = ?",
                ps -> ps.setLong(1, idRecenzie),
                rs -> rs.next() ? rs.getLong(1) : null
        );
        if (owner == null) {
            throw new NotFoundException("Recenzia cu id " + idRecenzie + " nu exista");
        }
        if (!owner.equals(idClientCurent)) {
            throw new ForbiddenException("Nu ai dreptul sa modifici aceasta recenzie");
        }
    }

    public void verificaVizualizare(Long idVizualizare, Long idClientCurent) {
        Long owner = jdbcTemplate.query(
                "SELECT id_client FROM vizualizari WHERE id = ?",
                ps -> ps.setLong(1, idVizualizare),
                rs -> rs.next() ? rs.getLong(1) : null
        );
        if (owner == null) {
            throw new NotFoundException("Vizualizarea cu id " + idVizualizare + " nu exista");
        }
        if (!owner.equals(idClientCurent)) {
            throw new ForbiddenException("Nu ai dreptul sa modifici aceasta vizualizare");
        }
    }
}
