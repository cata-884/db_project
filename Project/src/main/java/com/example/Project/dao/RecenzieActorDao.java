package com.example.Project.dao;

import com.example.Project.model.recenzie.RecenziiActori;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Componenta de acces la date (DAO) pentru entitatea RecenzieActor.
 * Gestioneaza operatiile de interogare asupra tabelului {@code recenzii_actori} folosind JdbcTemplate.
 */
@Repository
public class RecenzieActorDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Sablon (template) pentru maparea unui rand din tabelul {@code recenzii_actori} catre obiectul Java {@link RecenziiActori}.
     * Transforma datele brute din ResultSet in instanta de entitate.
     */
    private static final RowMapper<RecenziiActori> ROW_MAPPER = (rs, rowNum) -> new RecenziiActori(
            rs.getLong("id_recenzie"),
            rs.getLong("id_actor"),
            rs.getString("comentariu")
    );

    /**
     * Returneaza toate inregistrarile din {@code recenzii_actori} asociate unei recenzii specifice.
     * @param idRecenzie Identificatorul recenziei dupa care se filtreaza.
     * @return O lista de obiecte {@link RecenziiActori}; lista vida daca nu exista inregistrari pentru recenzia data.
     */
    public List<RecenziiActori> findByRecenzieId(Long idRecenzie) {
        return jdbcTemplate.query(
                "SELECT id_recenzie, id_actor, comentariu FROM recenzii_actori WHERE id_recenzie = ?",
                ROW_MAPPER, idRecenzie);
    }
}
