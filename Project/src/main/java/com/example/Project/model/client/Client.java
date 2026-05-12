package com.example.Project.model.client;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

/**
 * Entitate ce reprezinta un client inregistrat in sistem (tabelul {@code clienti}).
 * Contine datele personale, de contact si credentialele de autentificare.
 */
@Data
@AllArgsConstructor
public class Client {
    /** Identificatorul unic al clientului. */
    private Long id;
    /** Numele de familie al clientului. */
    private String nume;
    /** Prenumele clientului. */
    private String prenume;
    /** Numarul de telefon fix; poate fi {@code null}. */
    private PhoneNumber telefonFix;
    /** Adresa de domiciliu; poate fi {@code null}. */
    private String adresa;
    /** Orasul de rezidenta; poate fi {@code null}. */
    private String oras;
    /** Adresa de email; poate fi {@code null}. */
    private String email;
    /** Numarul de telefon mobil; poate fi {@code null}. */
    private PhoneNumber telefonMobil;
    /** Data nasterii; poate fi {@code null}. */
    private LocalDate dataNastere;
    /** Numele de utilizator unic folosit la autentificare. */
    private String username;
    /** Parola hash-uita cu BCrypt. */
    private String parola;
}
