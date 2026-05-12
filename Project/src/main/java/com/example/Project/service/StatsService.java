package com.example.Project.service;

import com.example.Project.dao.StatsDao;
import com.example.Project.dto.response.AnalizaSezonierResponse;
import com.example.Project.dto.response.ClientSimilarResponse;
import com.example.Project.dto.response.GrupareClientiResponse;
import com.example.Project.dto.response.PredictieSezoneraResponse;
import com.example.Project.dto.response.ProfilCinematograficResponse;
import com.example.Project.dto.response.RecomandareResponse;
import com.example.Project.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Serviciu pentru analizele statistice si recomandarile personalizate.
 * Toate operatiile delega catre proceduri stocate Oracle prin {@link StatsDao}.
 */
@Service
public class StatsService {

    @Autowired
    private StatsDao statsDao;

    /**
     * Returneaza filmele recomandate pentru un client pe baza preferintelor sale.
     * @param idClient Identificatorul clientului.
     * @return Lista filmelor recomandate.
     */
    public List<RecomandareResponse> getRecomandari(Long idClient) {
        return statsDao.recomandari(idClient);
    }

    /**
     * Returneaza profilul cinematografic al unui client.
     * @param idClient Identificatorul clientului.
     * @return Profilul cu categoria preferata, actorul preferat, rating mediu etc.
     * @throws NotFoundException daca clientul nu exista sau nu are activitate inregistrata.
     */
    public ProfilCinematograficResponse getProfilCinematografic(Long idClient) {
        ProfilCinematograficResponse profil = statsDao.profilCinematografic(idClient);
        if (profil == null) throw new NotFoundException("Clientul cu id " + idClient + " nu există");
        return profil;
    }

    /**
     * Returneaza analiza sezoniera a vizionarilor pentru toate categoriile si lunile.
     * @return Lista cu numarul de vizionari si rangul fiecarei categorii pe luna.
     */
    public List<AnalizaSezonierResponse> getAnalizaSezoniera() {
        return statsDao.analizaSezoniera();
    }

    /**
     * Returneaza clientii cu preferinte similare cu un client dat.
     * @param idClient Identificatorul clientului de referinta.
     * @param topN     Numarul maxim de clienti similari de returnat; trebuie sa fie intre 1 si 50.
     * @return Lista clientilor similari cu scorul de similaritate.
     * @throws IllegalArgumentException daca {@code topN} este in afara intervalului [1, 50].
     */
    public List<ClientSimilarResponse> getClientiSimilari(Long idClient, int topN) {
        if (topN < 1 || topN > 50) throw new IllegalArgumentException("topN trebuie să fie între 1 și 50");
        return statsDao.clientiSimilari(idClient, topN);
    }

    /**
     * Returneaza predictiile sezoniere de vizionare pentru o luna specificata.
     * @param luna Luna calendaristica (1-12).
     * @param topN Numarul maxim de filme de returnat; trebuie sa fie intre 1 si 50.
     * @return Lista filmelor cu scorul de predictie pentru luna data.
     * @throws IllegalArgumentException daca {@code luna} nu e intre 1-12 sau {@code topN} depaseste limitele.
     */
    public List<PredictieSezoneraResponse> getPredictiiSezoniere(int luna, int topN) {
        if (luna < 1 || luna > 12) throw new IllegalArgumentException("luna trebuie să fie între 1 și 12");
        if (topN < 1 || topN > 50) throw new IllegalArgumentException("topN trebuie să fie între 1 și 50");
        return statsDao.predictiiSezoniere(luna, topN);
    }

    /**
     * Returneaza gruparea clientilor dupa similaritatea preferintelor.
     * @param threshold Pragul de similaritate pentru includere in aceeasi grupa (0-1).
     * @return Lista clientilor cu ID-ul grupei din care fac parte.
     */
    public List<GrupareClientiResponse> getGrupareClienti(double threshold) {
        return statsDao.grupareClienti(threshold);
    }
}
