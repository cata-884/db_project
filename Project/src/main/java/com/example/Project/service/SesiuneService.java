package com.example.Project.service;

import com.example.Project.dao.SesiuneDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Serviciu pentru gestionarea sesiunilor de autentificare.
 * Creeaza, valideaza si invalideaza token-uri de sesiune stocate in tabelul {@code sesiuni}.
 * Curata automat sesiunile expirate o data pe ora prin {@code @Scheduled}.
 */
@Service
public class SesiuneService {

    @Autowired
    private SesiuneDao sesiuneDao;

    /**
     * Creeaza o sesiune noua pentru clientul dat si returneaza token-ul generat.
     * @param idClient Identificatorul clientului pentru care se creeaza sesiunea.
     * @return Token-ul Bearer generat (UUID fara cratime).
     */
    public String creeazaTokenPentru(Long idClient) {
        return sesiuneDao.creeaza(idClient);
    }

    /**
     * Valideaza un token de sesiune si returneaza ID-ul clientului asociat daca token-ul este valid si neexpirat.
     * @param token Token-ul Bearer de validat.
     * @return Optional cu ID-ul clientului, sau {@link java.util.Optional#empty()} daca token-ul este invalid sau expirat.
     */
    public Optional<Long> validareToken(String token) {
        return sesiuneDao.gasesteClientPentruToken(token);
    }

    /**
     * Invalideaza (sterge) un token de sesiune la logout.
     * @param token Token-ul de sters din tabelul sesiuni.
     */
    public void invalideazaToken(String token) {
        sesiuneDao.sterge(token);
    }

    /**
     * Sterge sesiunile expirate din baza de date. Rulat automat la inceputul fiecarei ore.
     */
    @Scheduled(cron = "0 0 * * * *")
    public void cleanupExpirate() {
        sesiuneDao.curataExpirate();
    }
}
