package com.example.Project.service;

import com.example.Project.exception.ForbiddenException;
import com.example.Project.exception.NotFoundException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

/**
 * Serviciu de verificare a proprietatii resurselor.
 * Se asigura ca un client autentificat poate modifica sau sterge
 * doar resursele care ii apartin, aruncand exceptii adecvate in caz contrar.
 */
@Service
public class OwnershipService {

    private final JdbcTemplate jdbcTemplate;

    public OwnershipService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Verifica ca recenzia apartine clientului autentificat curent.
     * @param idRecenzie      Identificatorul recenziei de verificat.
     * @param idClientCurent  ID-ul clientului autentificat, extras din sesiune.
     * @throws NotFoundException  daca recenzia nu exista.
     * @throws ForbiddenException daca recenzia apartine altui client.
     */
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

    /**
     * Verifica ca vizualizarea apartine clientului autentificat curent.
     * @param idVizualizare  Identificatorul vizualizarii de verificat.
     * @param idClientCurent ID-ul clientului autentificat, extras din sesiune.
     * @throws NotFoundException  daca vizualizarea nu exista.
     * @throws ForbiddenException daca vizualizarea apartine altui client.
     */
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
