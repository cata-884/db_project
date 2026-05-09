package com.example.Project.dao;

import com.example.Project.model.client.Client;
import com.example.Project.model.client.CountryCode;
import com.example.Project.model.client.PhoneNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Repository
public class AuthDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final RowMapper<Client> CLIENT_MAPPER = (rs, rowNum) -> {
        String fixCod = rs.getString("telefon_fix_cod");
        String fixNr  = rs.getString("telefon_fix_nr");
        String mobCod = rs.getString("telefon_mobil_cod");
        String mobNr  = rs.getString("telefon_mobil_nr");

        PhoneNumber homePhone = (fixCod != null && fixNr != null)
                ? new PhoneNumber(parseCountryCode(fixCod), fixNr) : null;
        PhoneNumber cellphone = (mobCod != null && mobNr != null)
                ? new PhoneNumber(parseCountryCode(mobCod), mobNr) : null;

        return new Client(
                rs.getLong("id"),
                rs.getString("nume"),
                rs.getString("prenume"),
                homePhone,
                rs.getString("adresa"),
                rs.getString("oras"),
                rs.getString("email"),
                cellphone,
                rs.getDate("data_nastere") != null ? rs.getDate("data_nastere").toLocalDate() : null,
                rs.getString("username"),
                rs.getString("parola")
        );
    };

    private static CountryCode parseCountryCode(String prefix) {
        return Arrays.stream(CountryCode.values())
                .filter(c -> c.getPrefix().equals(prefix))
                .findFirst()
                .orElse(CountryCode.OTHER);
    }

    public Optional<Client> findByUsernameAndPassword(String username, String parola) {
        String sql = "SELECT id, nume, prenume, telefon_fix_cod, telefon_fix_nr, adresa, oras, email, " +
                     "telefon_mobil_cod, telefon_mobil_nr, data_nastere, username, parola " +
                     "FROM clienti WHERE username = ? AND parola = ?";
        List<Client> results = jdbcTemplate.query(sql, CLIENT_MAPPER, username, parola);
        return results.stream().findFirst();
    }
}
