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

/**
 * Componenta de acces la date (DAO) pentru entitatea Categorie.
 * Gestioneaza operatiile CRUD asupra tabelului {@code categorii} folosind JdbcTemplate.
 */
@Repository
public class CategorieDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Sablon (template) pentru maparea unui rand din tabelul {@code categorii} catre obiectul {@link Categorie}.
     */
    private static final RowMapper<Categorie> ROW_MAPPER = (rs, rowNum) ->
            new Categorie(rs.getLong("id"), rs.getString("nume"));

    /**
     * Returneaza toate categoriile din baza de date, ordonate dupa ID.
     * @return Lista categoriilor; lista vida daca tabelul este gol.
     */
    public List<Categorie> findAll() {
        return jdbcTemplate.query("SELECT id, nume FROM categorii ORDER BY id", ROW_MAPPER);
    }

    /**
     * Cauta o categorie dupa cheia primara.
     * @param id Identificatorul unic al categoriei.
     * @return Optional cu categoria gasita, sau gol daca nu exista.
     */
    public Optional<Categorie> findById(Long id) {
        List<Categorie> results = jdbcTemplate.query(
                "SELECT id, nume FROM categorii WHERE id = ?", ROW_MAPPER, id);
        return results.stream().findFirst();
    }

    /**
     * Insereaza o categorie noua si ii seteaza ID-ul generat de baza de date.
     * @param categorie Obiectul cu datele categoriei de inserat.
     * @return Obiectul categorie actualizat cu ID-ul generat.
     */
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

    /**
     * Actualizeaza numele unei categorii existente. Foloseste NVL pentru a pastra valoarea existenta daca parametrul e null.
     * @param id        Identificatorul categoriei de actualizat.
     * @param categorie Obiectul cu noul nume.
     * @return Numarul de randuri afectate (1 daca a fost gasita, 0 altfel).
     */
    public int update(Long id, Categorie categorie) {
        return jdbcTemplate.update(
                "UPDATE categorii SET nume = NVL(?, nume) WHERE id = ?",
                categorie.getNume(), id);
    }

    /**
     * Sterge o categorie din baza de date dupa ID.
     * @param id Identificatorul categoriei de sters.
     * @return Numarul de randuri sterse (1 daca a existat, 0 altfel).
     */
    public int deleteById(Long id) {
        return jdbcTemplate.update("DELETE FROM categorii WHERE id = ?", id);
    }
}
