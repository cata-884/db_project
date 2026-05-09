package com.example.Project.dao;

import com.example.Project.model.film.Categorie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class CategorieDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final RowMapper<Categorie> ROW_MAPPER = (rs, rowNum) ->
            new Categorie(rs.getLong("id"), rs.getString("nume"));

    public List<Categorie> findAll() {
        return jdbcTemplate.query("SELECT id, nume FROM categorii ORDER BY id", ROW_MAPPER);
    }

    public Optional<Categorie> findById(Long id) {
        List<Categorie> results = jdbcTemplate.query(
                "SELECT id, nume FROM categorii WHERE id = ?", ROW_MAPPER, id);
        return results.stream().findFirst();
    }

    public Categorie save(Categorie categorie) {
        String sql = "INSERT INTO categorii (nume) VALUES (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, categorie.getNume());
            return ps;
        }, keyHolder);
        categorie.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return categorie;
    }

    public int update(Long id, Categorie categorie) {
        return jdbcTemplate.update(
                "UPDATE categorii SET nume = NVL(?, nume) WHERE id = ?",
                categorie.getNume(), id);
    }

    public int deleteById(Long id) {
        return jdbcTemplate.update("DELETE FROM categorii WHERE id = ?", id);
    }
}
