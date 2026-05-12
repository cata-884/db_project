package com.example.Project.dao;

import com.example.Project.dto.response.AnalizaSezonierResponse;
import com.example.Project.dto.response.ClientSimilarResponse;
import com.example.Project.dto.response.GrupareClientiResponse;
import com.example.Project.dto.response.PredictieSezoneraResponse;
import com.example.Project.dto.response.ProfilCinematograficResponse;
import com.example.Project.dto.response.RecomandareResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Componenta de acces la date (DAO) pentru analizele statistice si recomandari.
 * Toate metodele apeleaza proceduri stocate Oracle care returneaza cursoare {@code SYS_REFCURSOR}.
 * Cursorele sunt citite manual din parametrii OUT ai {@link java.sql.CallableStatement}.
 */
@Repository
public class StatsDao {

    /** Codul de tip Oracle pentru SYS_REFCURSOR ({@code oracle.jdbc.OracleTypes.CURSOR = -10}). */
    private static final int CURSOR = -10;


    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Apeleaza procedura {@code p_recomandari_pentru_client} si returneaza filmele recomandate.
     * @param idClient Identificatorul clientului.
     * @return Lista filmelor recomandate bazata pe preferintele clientului.
     */
    public List<RecomandareResponse> recomandari(Long idClient) {
        CallableStatementCreator creator = con -> con.prepareCall("{call p_recomandari_pentru_client(?, ?)}");
        CallableStatementCallback<List<RecomandareResponse>> callback = cs -> {
            cs.setLong(1, idClient);
            //pasam un cursor
            cs.registerOutParameter(2, CURSOR);
            cs.execute();
            List<RecomandareResponse> result = new ArrayList<>();
            //trecem prin cursorul returnat si construim lista de recomandari
            try (ResultSet rs = (ResultSet) cs.getObject(2)) {
                while (rs.next()) {
                    result.add(new RecomandareResponse(
                            rs.getLong("id"),
                            rs.getString("titlu"),
                            rs.getString("descriere"),
                            rs.getDouble("rating"),
                            rs.getString("categorie"),
                            rs.getDate("data_lansare") != null ? rs.getDate("data_lansare").toLocalDate() : null
                    ));
                }
            }
            return result;
        };
        return jdbcTemplate.execute(creator, callback);
    }

    /**
     * Apeleaza procedura {@code p_profil_cinematografic} si returneaza profilul cinematografic.
     * @param idClient Identificatorul clientului.
     * @return Profilul cu categoria preferata, actorul preferat, rating mediu etc.,
     *         sau {@code null} daca clientul nu are activitate.
     */
    public ProfilCinematograficResponse profilCinematografic(Long idClient) {
        CallableStatementCreator creator = con -> con.prepareCall("{call p_profil_cinematografic(?, ?)}");
        CallableStatementCallback<ProfilCinematograficResponse> callback = cs -> {
            cs.setLong(1, idClient);
            cs.registerOutParameter(2, CURSOR);
            cs.execute();
            try (ResultSet rs = (ResultSet) cs.getObject(2)) {
                if (rs.next()) {
                    Object ratingObj = rs.getObject("rating_mediu");
                    Double rating = ratingObj != null ? ((Number) ratingObj).doubleValue() : null;
                    Object totalFilmeObj = rs.getObject("total_filme_vazute");
                    Long totalFilme = totalFilmeObj != null ? ((Number) totalFilmeObj).longValue() : 0L;
                    Object totalRecenziiObj = rs.getObject("total_recenzii");
                    Long totalRecenzii = totalRecenziiObj != null ? ((Number) totalRecenziiObj).longValue() : 0L;
                    return new ProfilCinematograficResponse(
                            rs.getString("categorie_preferata"),
                            rs.getString("actor_preferat"),
                            rating,
                            totalFilme,
                            totalRecenzii,
                            rs.getString("sentiment_dominant")
                    );
                }
            }
            return null;
        };
        return jdbcTemplate.execute(creator, callback);
    }

    /**
     * Apeleaza procedura {@code p_analiza_sezoniera} si returneaza analiza sezoniera a vizionarilor.
     * @return Lista cu numarul de vizionari si rangul fiecarei categorii pe luna.
     */
    public List<AnalizaSezonierResponse> analizaSezoniera() {
        CallableStatementCreator creator = con -> con.prepareCall("{call p_analiza_sezoniera(?)}");
        CallableStatementCallback<List<AnalizaSezonierResponse>> callback = cs -> {
            cs.registerOutParameter(1, CURSOR);
            cs.execute();
            List<AnalizaSezonierResponse> result = new ArrayList<>();
            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                while (rs.next()) {
                    result.add(new AnalizaSezonierResponse(
                            rs.getInt("luna"),
                            rs.getString("categorie"),
                            rs.getLong("nr_vizionari"),
                            rs.getLong("rank_categorie")
                    ));
                }
            }
            return result;
        };
        return jdbcTemplate.execute(creator, callback);
    }

    /**
     * Apeleaza procedura {@code p_clienti_similari} si returneaza clientii cu preferinte similare.
     * @param idClient Identificatorul clientului de referinta.
     * @param topN     Numarul maxim de clienti similari de returnat.
     * @return Lista clientilor similari cu scorul de similaritate.
     */
    public List<ClientSimilarResponse> clientiSimilari(Long idClient, int topN) {
        CallableStatementCreator creator = con -> con.prepareCall("{call p_clienti_similari(?, ?, ?)}");
        CallableStatementCallback<List<ClientSimilarResponse>> callback = cs -> {
            cs.setLong(1, idClient);
            cs.setInt(2, topN);
            cs.registerOutParameter(3, CURSOR);
            cs.execute();
            List<ClientSimilarResponse> result = new ArrayList<>();
            try (ResultSet rs = (ResultSet) cs.getObject(3)) {
                while (rs.next()) {
                    result.add(new ClientSimilarResponse(
                            rs.getLong("id"),
                            rs.getString("nume_complet"),
                            rs.getDouble("scor_similaritate")
                    ));
                }
            }
            return result;
        };
        return jdbcTemplate.execute(creator, callback);
    }

    /**
     * Apeleaza procedura {@code p_predictii_sezoniere} si returneaza predictiile pentru o luna data.
     * @param luna Luna calendaristica (1-12).
     * @param topN Numarul maxim de filme de returnat.
     * @return Lista filmelor cu scorul de predictie calculat.
     */
    public List<PredictieSezoneraResponse> predictiiSezoniere(int luna, int topN) {
        CallableStatementCreator creator = con -> con.prepareCall("{call p_predictii_sezoniere(?, ?, ?)}");
        CallableStatementCallback<List<PredictieSezoneraResponse>> callback = cs -> {
            cs.setInt(1, luna);
            cs.setInt(2, topN);
            cs.registerOutParameter(3, CURSOR);
            cs.execute();
            List<PredictieSezoneraResponse> result = new ArrayList<>();
            try (ResultSet rs = (ResultSet) cs.getObject(3)) {
                while (rs.next()) {
                    result.add(new PredictieSezoneraResponse(
                            rs.getLong("id"),
                            rs.getString("titlu"),
                            rs.getString("categorie"),
                            rs.getDouble("rating"),
                            rs.getDouble("factor_sezonier"),
                            rs.getDouble("scor_predictie")
                    ));
                }
            }
            return result;
        };
        return jdbcTemplate.execute(creator, callback);
    }

    /**
     * Apeleaza procedura {@code p_grupare_clienti} si returneaza gruparea clientilor.
     * @param threshold Pragul de similaritate pentru includere in aceeasi grupa (0-1).
     * @return Lista clientilor cu ID-ul grupei din care fac parte.
     */
    public List<GrupareClientiResponse> grupareClienti(double threshold) {
        CallableStatementCreator creator = con -> con.prepareCall("{call p_grupare_clienti(?, ?)}");
        CallableStatementCallback<List<GrupareClientiResponse>> callback = cs -> {
            cs.setDouble(1, threshold);
            cs.registerOutParameter(2, CURSOR);
            cs.execute();
            List<GrupareClientiResponse> result = new ArrayList<>();
            try (ResultSet rs = (ResultSet) cs.getObject(2)) {
                while (rs.next()) {
                    result.add(new GrupareClientiResponse(
                            rs.getLong("id_grupa"),
                            rs.getLong("id_client"),
                            rs.getString("nume_complet")
                    ));
                }
            }
            return result;
        };
        return jdbcTemplate.execute(creator, callback);
    }
}
