package com.example.Project.dao;

import com.example.Project.dto.response.AnalizaSezonierResponse;
import com.example.Project.dto.response.ClientSimilarResponse;
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

@Repository
public class StatsDao {

    // Oracle SYS_REFCURSOR type code (oracle.jdbc.OracleTypes.CURSOR = -10)
    private static final int CURSOR = -10;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<RecomandareResponse> recomandari(Long idClient) {
        CallableStatementCreator creator = con -> con.prepareCall("{call p_recomandari_pentru_client(?, ?)}");
        CallableStatementCallback<List<RecomandareResponse>> callback = cs -> {
            cs.setLong(1, idClient);
            cs.registerOutParameter(2, CURSOR);
            cs.execute();
            List<RecomandareResponse> result = new ArrayList<>();
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
}
