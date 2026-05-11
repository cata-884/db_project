package com.example.Project;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@EnableScheduling
public class ProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProjectApplication.class, args);
	}

	@Bean
	CommandLineRunner migrateLegacyPasswords(JdbcTemplate jdbcTemplate, PasswordEncoder passwordEncoder) {
		return args -> jdbcTemplate.query(
				"SELECT id, parola FROM clienti WHERE parola IS NOT NULL AND parola NOT LIKE '$2%'",
				(org.springframework.jdbc.core.RowCallbackHandler) rs -> jdbcTemplate.update(
						"UPDATE clienti SET parola = ? WHERE id = ?",
						passwordEncoder.encode(rs.getString("parola")),
						rs.getLong("id")
				)
		);
	}
}
