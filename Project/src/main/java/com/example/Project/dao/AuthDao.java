package com.example.Project.dao;

import com.example.Project.model.client.Client;
import com.example.Project.model.client.CountryCode;
import com.example.Project.model.client.PhoneNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Componenta de acces la date (DAO) pentru autentificare si inregistrare.
 * Gestioneaza operatiile asupra tabelului {@code clienti} necesare fluxului de autentificare,
 * inclusiv cautarea dupa username si inserarea unui client nou cu KeyHolder.
 */
@Repository
public class AuthDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Sablon (template) pentru maparea unui rand complet din tabelul {@code clienti} catre obiectul {@link Client}.
     * Trateaza numere de telefon nullable prin constructia conditionala a obiectelor {@link PhoneNumber}.
     */
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

    /**
     * Converteste un sir de caractere prefix (ex. {@code "+40"}) la valoarea enum {@link CountryCode} corespunzatoare.
     * Returneaza {@link CountryCode#OTHER} daca prefixul nu este recunoscut.
     * @param prefix Prefixul de tara de convertit.
     * @return Valoarea {@link CountryCode} corespunzatoare.
     */
    private static CountryCode parseCountryCode(String prefix) {
        return Arrays.stream(CountryCode.values())
                .filter(c -> c.getPrefix().equals(prefix))
                .findFirst()
                .orElse(CountryCode.OTHER);
    }

    /**
     * Cauta un client dupa username.
     * @param username Numele de utilizator de cautat.
     * @return Optional cu clientul gasit, sau gol daca username-ul nu exista.
     */
    public Optional<Client> findByUsername(String username) {
        String sql = "SELECT id, nume, prenume, telefon_fix_cod, telefon_fix_nr, adresa, oras, email, " +
                     "telefon_mobil_cod, telefon_mobil_nr, data_nastere, username, parola " +
                     "FROM clienti WHERE username = ?";
        List<Client> results = jdbcTemplate.query(sql, CLIENT_MAPPER, username);
        return results.stream().findFirst();
    }

    /**
     * Cauta un client dupa cheia primara.
     * @param id Identificatorul unic al clientului.
     * @return Optional cu clientul gasit, sau gol daca nu exista niciun client cu ID-ul dat.
     */
    public Optional<Client> findById(Long id) {
        String sql = "SELECT id, nume, prenume, telefon_fix_cod, telefon_fix_nr, adresa, oras, email, " +
                     "telefon_mobil_cod, telefon_mobil_nr, data_nastere, username, parola " +
                     "FROM clienti WHERE id = ?";
        List<Client> results = jdbcTemplate.query(sql, CLIENT_MAPPER, id);
        return results.stream().findFirst();
    }

    /**
     * Insereaza un client nou cu campurile minime obligatorii si returneaza ID-ul generat.
     * @param nume     Numele de familie al clientului.
     * @param prenume  Prenumele clientului.
     * @param email    Adresa de email; poate fi {@code null}.
     * @param username Numele de utilizator unic.
     * @param parola   Parola hash-uita cu BCrypt.
     * @return ID-ul generat de baza de date pentru noul client.
     */
    public Long insertBasicClient(String nume, String prenume, String email, String username, String parola) {
        String sql = "INSERT INTO clienti (nume, prenume, email, username, parola) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, nume);
            ps.setString(2, prenume);
            ps.setString(3, email);
            ps.setString(4, username);
            ps.setString(5, parola);
            return ps;
        }, keyHolder);
        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }
}
