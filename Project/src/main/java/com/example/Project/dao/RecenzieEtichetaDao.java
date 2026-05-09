package com.example.Project.dao;

import com.example.Project.model.recenzie.Eticheta;
import com.example.Project.model.recenzie.EtichetaRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RecenzieEtichetaDao {

    private final JdbcTemplate jdbcTemplate;

    private static final RowMapper<EtichetaRecord> ETICHETA_MAPPER = (rs, rowNum) -> new EtichetaRecord(
            rs.getLong("id"),
            Eticheta.valueOf(rs.getString("denumire")),
            rs.getString("sentiment")
    );

    public RecenzieEtichetaDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void addEticheta(Long idRecenzie, Long idEticheta) {
        jdbcTemplate.update(
                "INSERT INTO recenzii_etichete (id_recenzie, id_eticheta) VALUES (?, ?)",
                idRecenzie, idEticheta);
    }

    public List<EtichetaRecord> findEticheteByRecenzieId(Long idRecenzie) {
        String sql = "SELECT e.id, e.denumire, e.sentiment " +
                     "FROM etichete e JOIN recenzii_etichete re ON e.id = re.id_eticheta " +
                     "WHERE re.id_recenzie = ? ORDER BY e.sentiment, e.denumire";
        return jdbcTemplate.query(sql, ETICHETA_MAPPER, idRecenzie);
    }

    public int removeEticheta(Long idRecenzie, Long idEticheta) {
        return jdbcTemplate.update(
                "DELETE FROM recenzii_etichete WHERE id_recenzie = ? AND id_eticheta = ?",
                idRecenzie, idEticheta);
    }
}
