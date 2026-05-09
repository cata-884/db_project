package com.example.Project.dao;

import com.example.Project.model.recenzie.Eticheta;
import com.example.Project.model.recenzie.EtichetaRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class EtichetaDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final RowMapper<EtichetaRecord> ROW_MAPPER = (rs, rowNum) -> new EtichetaRecord(
            rs.getLong("id"),
            Eticheta.valueOf(rs.getString("denumire")),
            rs.getString("sentiment")
    );

    public List<EtichetaRecord> findAll() {
        return jdbcTemplate.query(
                "SELECT id, denumire, sentiment FROM etichete ORDER BY sentiment, denumire",
                ROW_MAPPER);
    }

    public Optional<EtichetaRecord> findById(Long id) {
        List<EtichetaRecord> results = jdbcTemplate.query(
                "SELECT id, denumire, sentiment FROM etichete WHERE id = ?",
                ROW_MAPPER, id);
        return results.stream().findFirst();
    }
}
