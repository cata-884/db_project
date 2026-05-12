package com.example.Project.dao;

import com.example.Project.model.recenzie.Eticheta;
import com.example.Project.model.recenzie.EtichetaRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Componenta de acces la date (DAO) pentru tabelul de legatura {@code recenzii_etichete}.
 * Gestioneaza asocierea si disocierea etichetelor de recenzii, precum si interogarile cu JOIN.
 */
@Repository
public class RecenzieEtichetaDao {

    private final JdbcTemplate jdbcTemplate;

    /**
     * Sablon (template) pentru maparea rezultatelor JOIN dintre {@code etichete} si {@code recenzii_etichete}
     * catre obiectul {@link EtichetaRecord}.
     */
    private static final RowMapper<EtichetaRecord> ETICHETA_MAPPER = (rs, rowNum) -> new EtichetaRecord(
            rs.getLong("id"),
            Eticheta.valueOf(rs.getString("denumire")),
            rs.getString("sentiment")
    );

    public RecenzieEtichetaDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Insereaza o asociere intre o recenzie si o eticheta in tabelul {@code recenzii_etichete}.
     * @param idRecenzie Identificatorul recenziei.
     * @param idEticheta Identificatorul etichetei de asociat.
     */
    public void addEticheta(Long idRecenzie, Long idEticheta) {
        jdbcTemplate.update(
                "INSERT INTO recenzii_etichete (id_recenzie, id_eticheta) VALUES (?, ?)",
                idRecenzie, idEticheta);
    }

    /**
     * Returneaza etichetele asociate unei recenzii, ordonate dupa sentiment si denumire.
     * @param idRecenzie Identificatorul recenziei.
     * @return Lista etichetelor; lista vida daca nu exista asocieri.
     */
    public List<EtichetaRecord> findEticheteByRecenzieId(Long idRecenzie) {
        String sql = "SELECT e.id, e.denumire, e.sentiment " +
                     "FROM etichete e JOIN recenzii_etichete re ON e.id = re.id_eticheta " +
                     "WHERE re.id_recenzie = ? ORDER BY e.sentiment, e.denumire";
        return jdbcTemplate.query(sql, ETICHETA_MAPPER, idRecenzie);
    }

    /**
     * Elimina asocierea dintre o recenzie si o eticheta din tabelul {@code recenzii_etichete}.
     * @param idRecenzie Identificatorul recenziei.
     * @param idEticheta Identificatorul etichetei de eliminat.
     * @return Numarul de randuri sterse (1 daca asocierea exista, 0 altfel).
     */
    public int removeEticheta(Long idRecenzie, Long idEticheta) {
        return jdbcTemplate.update(
                "DELETE FROM recenzii_etichete WHERE id_recenzie = ? AND id_eticheta = ?",
                idRecenzie, idEticheta);
    }
}
