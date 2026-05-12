package com.example.Project;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;

/**
 * Clasa principala de pornire a aplicatiei Spring Boot.
 * Activeaza si programarea sarcinilor ({@code @EnableScheduling}) pentru curatarea sesiunilor expirate.
 */
@SpringBootApplication
@EnableScheduling
public class ProjectApplication {

	/**
	 * Punctul de intrare al aplicatiei.
	 * @param args Argumentele liniei de comanda.
	 */
	public static void main(String[] args) {
		SpringApplication.run(ProjectApplication.class, args);
	}

	/**
	 * Migreaza parolele in clar (legacy) la hash BCrypt la pornirea aplicatiei.
	 * Identifica clientii cu parola ne-hash-uita (care nu incepe cu {@code $2}) si le re-hash-uieste.
	 * @param jdbcTemplate    Instanta JdbcTemplate pentru interogarea bazei de date.
	 * @param passwordEncoder Encoder-ul BCrypt folosit pentru hash-uire.
	 * @return Runner-ul care executa migrarea o singura data la startup.
	 */
	@Bean
	CommandLineRunner migrateLegacyPasswords(JdbcTemplate jdbcTemplate, PasswordEncoder passwordEncoder) {
		return args -> {
			var legacy = jdbcTemplate.query(
					"SELECT id, parola FROM clienti WHERE parola IS NOT NULL AND parola NOT LIKE '$2%'",
					(rs, rowNum) -> Map.entry(rs.getLong("id"), rs.getString("parola"))
			);
			for (var entry : legacy) {
				jdbcTemplate.update(
						"UPDATE clienti SET parola = ? WHERE id = ?",
						passwordEncoder.encode(entry.getValue()), entry.getKey()
				);
			}
			if (!legacy.isEmpty()) {
				System.out.println("[Auth] Migrated " + legacy.size() + " legacy passwords to BCrypt");
			}
		};
	}
}
