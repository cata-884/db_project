package com.example.Project.dao;

import com.example.Project.dto.request.UpdateRecenzieRequest;
import com.example.Project.dto.response.ComentariuActorDto;
import com.example.Project.dto.response.RecenzieDetailResponse;
import com.example.Project.dto.response.RecenzieResponse;
import com.example.Project.model.recenzie.Recenzii;
import oracle.jdbc.OracleConnection;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Array;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Componenta de acces la date (DAO) pentru entitatea Recenzie.
 * Gestioneaza operatiile CRUD, interogari cu JOIN si apelul procedurii stocate Oracle
 * {@code p_creeaza_recenzie_completa} pentru crearea atomica a recenziei cu etichete si comentarii actori.
 */
@Repository
public class RecenzieDao {

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedJdbcTemplate;

    /**
     * Mapper pentru proiectia simpla a unei recenzii (fara JOIN cu clienti/filme).
     * Coloana {@code nota} este tratata ca nullable.
     */
    private static final RowMapper<RecenzieResponse> RESPONSE_MAPPER = (rs, rowNum) -> {
        Object notaObj = rs.getObject("nota");
        Integer nota = notaObj != null ? ((Number) notaObj).intValue() : null;
        return new RecenzieResponse(
                rs.getLong("id"),
                rs.getLong("id_client"),
                rs.getLong("id_film"),
                nota,
                rs.getString("sentiment"),
                rs.getString("text_comentariu"),
                rs.getTimestamp("data_postare") != null ? rs.getTimestamp("data_postare").toLocalDateTime() : null
        );
    };

    /**
     * Mapper pentru proiectia detaliata a unei recenzii cu JOIN catre {@code clienti} si {@code filme}.
     * Listele {@code etichete} si {@code actoriComentarii} sunt initializate goale si populate ulterior.
     */
    private static final RowMapper<RecenzieDetailResponse> DETAIL_MAPPER = (rs, rowNum) -> {
        Object notaObj = rs.getObject("nota");
        Integer nota = notaObj != null ? ((Number) notaObj).intValue() : null;
        return new RecenzieDetailResponse(
                rs.getLong("id"),
                rs.getLong("id_client"),
                rs.getString("nume_client"),
                rs.getLong("id_film"),
                rs.getString("titlu_film"),
                nota,
                rs.getString("sentiment"),
                rs.getString("text_comentariu"),
                rs.getTimestamp("data_postare") != null ? rs.getTimestamp("data_postare").toLocalDateTime() : null,
                new ArrayList<>(),
                new ArrayList<>()
        );
    };

    /** Fragment SQL reutilizabil pentru selectia detaliata a recenziilor cu JOIN catre clienti si filme. */
    private static final String DETAIL_SELECT =
            "SELECT r.id, r.id_client, c.nume || ' ' || c.prenume AS nume_client, " +
            "r.id_film, f.titlu AS titlu_film, r.nota, r.sentiment, r.text_comentariu, r.data_postare " +
            "FROM recenzii r " +
            "JOIN clienti c ON r.id_client = c.id " +
            "JOIN filme f ON r.id_film = f.id ";

    public RecenzieDao(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedJdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedJdbcTemplate = namedJdbcTemplate;
    }

    /**
     * Returneaza toate recenziile, ordonate dupa ID.
     * @return Lista recenziilor; lista vida daca tabelul este gol.
     */
    public List<RecenzieResponse> findAll() {
        return jdbcTemplate.query(
                "SELECT id, id_client, id_film, nota, sentiment, text_comentariu, data_postare FROM recenzii ORDER BY id",
                RESPONSE_MAPPER);
    }

    /**
     * Cauta o recenzie dupa ID in proiectia simpla.
     * @param id Identificatorul recenziei.
     * @return Optional cu recenzia gasita, sau gol daca nu exista.
     */
    public Optional<RecenzieResponse> findById(Long id) {
        List<RecenzieResponse> results = jdbcTemplate.query(
                "SELECT id, id_client, id_film, nota, sentiment, text_comentariu, data_postare FROM recenzii WHERE id = ?",
                RESPONSE_MAPPER, id);
        return results.stream().findFirst();
    }

    /**
     * Cauta o recenzie dupa ID in proiectia detaliata (cu JOIN catre clienti si filme).
     * @param id Identificatorul recenziei.
     * @return Optional cu recenzia detaliata gasita, sau gol daca nu exista.
     */
    public Optional<RecenzieDetailResponse> findDetailById(Long id) {
        List<RecenzieDetailResponse> results = jdbcTemplate.query(
                DETAIL_SELECT + "WHERE r.id = ?",
                DETAIL_MAPPER, id);
        return results.stream().findFirst();
    }

    /**
     * Insereaza o recenzie simpla (fara etichete sau comentarii actori) si returneaza ID-ul generat.
     * {@code sentiment} este lasat {@code NULL} la INSERT — triggerul {@code trg_set_sentiment_recenzie}
     * il calculeaza automat. {@code data_postare} are {@code DEFAULT CURRENT_TIMESTAMP} in baza de date.
     * @param recenzie Obiectul cu datele recenziei de inserat.
     * @return ID-ul generat de baza de date pentru noua recenzie.
     */
    public Long insert(Recenzii recenzie) {
        String sql = "INSERT INTO recenzii (id_client, id_film, nota, sentiment, text_comentariu) VALUES (?, ?, ?, NULL, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, new String[]{"id"});
            ps.setLong(1, recenzie.getIdClient());
            ps.setLong(2, recenzie.getIdFilm());
            ps.setInt(3, recenzie.getNota());
            ps.setString(4, recenzie.getTextComentariu());
            return ps;
        }, keyHolder);
        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    /**
     * Actualizeaza partial nota si/sau textul unei recenzii. Foloseste NVL pentru a pastra valorile existente.
     * @param id  Identificatorul recenziei de actualizat.
     * @param req Noile valori; campurile {@code null} nu suprascriu datele existente.
     * @return Numarul de randuri afectate (1 daca recenzia exista, 0 altfel).
     */
    public int update(Long id, UpdateRecenzieRequest req) {
        return jdbcTemplate.update(
                "UPDATE recenzii SET nota = NVL(?, nota), text_comentariu = NVL(?, text_comentariu) WHERE id = ?",
                req.getNota(), req.getTextComentariu(), id);
    }

    /**
     * Sterge o recenzie din baza de date dupa ID.
     * @param id Identificatorul recenziei de sters.
     * @return Numarul de randuri sterse (1 daca a existat, 0 altfel).
     */
    public int deleteById(Long id) {
        return jdbcTemplate.update("DELETE FROM recenzii WHERE id = ?", id);
    }

    /**
     * Returneaza recenziile detaliate ale unui film, imbogatite cu etichete si comentarii despre actori.
     * Executa trei interogari: una pentru recenzii, una pentru etichete si una pentru comentarii actori,
     * toate filtrate dupa aceeasi lista de ID-uri de recenzii.
     * @param idFilm Identificatorul filmului.
     * @return Lista recenziilor detaliate, ordonata descrescator dupa data postarii.
     */
    public List<RecenzieDetailResponse> findByFilmId(Long idFilm) {
        List<RecenzieDetailResponse> reviews = jdbcTemplate.query(
                DETAIL_SELECT + "WHERE r.id_film = ? ORDER BY r.data_postare DESC",
                DETAIL_MAPPER, idFilm);

        if (reviews.isEmpty()) return reviews;

        List<Long> ids = reviews.stream()
                .map(RecenzieDetailResponse::getId)
                .collect(Collectors.toList());

        MapSqlParameterSource params = new MapSqlParameterSource("ids", ids);

        String sql = "SELECT re.id_recenzie, e.denumire " +
                     "FROM recenzii_etichete re JOIN etichete e ON re.id_eticheta = e.id " +
                     "WHERE re.id_recenzie IN (:ids)";

        Map<Long, List<String>> eticheteMap = new HashMap<>();
        namedJdbcTemplate.query(sql, params, rs -> {
            Long idRec = rs.getLong("id_recenzie");
            String denumire = rs.getString("denumire");
            eticheteMap.computeIfAbsent(idRec, k -> new ArrayList<>()).add(denumire);
        });

        reviews.forEach(r -> r.setEtichete(eticheteMap.getOrDefault(r.getId(), Collections.emptyList())));

        String actoriSql = "SELECT ra.id_recenzie, a.nume_scena, ra.comentariu " +
                           "FROM recenzii_actori ra JOIN actori a ON ra.id_actor = a.id " +
                           "WHERE ra.id_recenzie IN (:ids)";

        Map<Long, List<ComentariuActorDto>> actoriMap = new HashMap<>();
        namedJdbcTemplate.query(actoriSql, params, rs -> {
            Long idRec = rs.getLong("id_recenzie");
            ComentariuActorDto dto = new ComentariuActorDto(
                    rs.getString("nume_scena"),
                    rs.getString("comentariu"));
            actoriMap.computeIfAbsent(idRec, k -> new ArrayList<>()).add(dto);
        });

        reviews.forEach(r -> r.setActoriComentarii(actoriMap.getOrDefault(r.getId(), Collections.emptyList())));

        return reviews;
    }

    /**
     * Returneaza toate recenziile scrise de un client, ordonate descrescator dupa data postarii.
     * @param idClient Identificatorul clientului.
     * @return Lista recenziilor clientului; lista vida daca nu a scris nicio recenzie.
     */
    public List<RecenzieResponse> findByClientId(Long idClient) {
        return jdbcTemplate.query(
                "SELECT id, id_client, id_film, nota, sentiment, text_comentariu, data_postare " +
                "FROM recenzii WHERE id_client = ? ORDER BY data_postare DESC",
                RESPONSE_MAPPER, idClient);
    }

    /**
     * Apeleaza procedura stocata Oracle {@code p_creeaza_recenzie_completa} pentru crearea atomica
     * a unei recenzii cu etichete si comentarii despre actori.
     * Foloseste tipurile Oracle {@code SYS.ODCINUMBERLIST} si {@code SYS.ODCIVARCHAR2LIST}
     * pentru transmiterea array-urilor.
     * @param idClient        ID-ul clientului care creeaza recenzia.
     * @param idFilm          ID-ul filmului recenzat.
     * @param nota            Nota acordata (1-10).
     * @param textComentariu  Textul comentariului; poate fi {@code null}.
     * @param etichetaIds     Lista ID-urilor etichetelor de asociat; poate fi {@code null} sau goala.
     * @param actoriIds       Lista ID-urilor actorilor comentati; poate fi {@code null} sau goala.
     * @param actoriComentarii Lista comentariilor pentru actori; trebuie sa aiba aceeasi lungime cu {@code actoriIds}.
     * @return ID-ul recenziei create, returnat ca parametru OUT de procedura.
     */
    public Long creeazaRecenzieCompleta(
            Long idClient, Long idFilm, Integer nota, String textComentariu,
            List<Long> etichetaIds, List<Long> actoriIds, List<String> actoriComentarii) {

        return jdbcTemplate.execute((ConnectionCallback<Long>) conn -> {
            OracleConnection oc = conn.unwrap(OracleConnection.class);

            Long[] eArr = etichetaIds == null ? new Long[0] : etichetaIds.toArray(new Long[0]);
            Long[] aIdArr = actoriIds == null ? new Long[0] : actoriIds.toArray(new Long[0]);
            String[] aComArr = actoriComentarii == null ? new String[0] : actoriComentarii.toArray(new String[0]);

            Array etichetaArray = oc.createOracleArray("SYS.ODCINUMBERLIST", eArr);
            Array actoriIdArray = oc.createOracleArray("SYS.ODCINUMBERLIST", aIdArr);
            Array actoriComArray = oc.createOracleArray("SYS.ODCIVARCHAR2LIST", aComArr);

            try (CallableStatement cs = conn.prepareCall(
                    "{call p_creeaza_recenzie_completa(?,?,?,?,?,?,?,?)}")) {
                cs.setLong(1, idClient);
                cs.setLong(2, idFilm);
                cs.setInt(3, nota);
                cs.setString(4, textComentariu);
                cs.setArray(5, etichetaArray);
                cs.setArray(6, actoriIdArray);
                cs.setArray(7, actoriComArray);
                cs.registerOutParameter(8, Types.NUMERIC);
                cs.execute();
                return cs.getLong(8);
            }
        });
    }
}
