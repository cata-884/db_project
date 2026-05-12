package com.example.Project.dao;

import com.example.Project.dto.response.ActorDistributieResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Componenta de acces la date (DAO) pentru entitatea Distributie.
 * Gestioneaza interogarile asupra tabelului de legatura {@code distributie} folosind JdbcTemplate.
 */
@Repository
public class DistributieDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Sablon (template) pentru maparea rezultatelor de tip JOIN dintre {@code actori} si {@code distributie}.
     * Transforma datele brute din ResultSet in instante de tip {@link ActorDistributieResponse}.
     */
    private static final RowMapper<ActorDistributieResponse> ACTOR_MAPPER = (rs, rowNum) -> new ActorDistributieResponse(
            rs.getLong("id_actor"),
            rs.getString("nume_scena"),
            rs.getString("nume"),
            rs.getString("prenume"),
            rs.getString("rol")
    );

    /**
     * Executa o interogare cu JOIN pentru a obtine actorii si rolurile lor dintr-un film specificat.
     * @param idFilm Identificatorul filmului pentru care se cauta distributia.
     * @return O lista de DTO-uri {@link ActorDistributieResponse} cu datele actorilor si rolul jucat;
     *         lista vida daca filmul nu are nicio distributie inregistrata.
     */
    public List<ActorDistributieResponse> findActoriByFilmId(Long idFilm) {
        String sql = "SELECT a.id AS id_actor, a.nume_scena, a.nume, a.prenume, d.rol " +
                     "FROM actori a JOIN distributie d ON a.id = d.id_actor " +
                     "WHERE d.id_film = ?";
        return jdbcTemplate.query(sql, ACTOR_MAPPER, idFilm);
    }
}
