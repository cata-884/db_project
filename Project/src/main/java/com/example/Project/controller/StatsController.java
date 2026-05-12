package com.example.Project.controller;

import com.example.Project.config.CurrentUser;
import com.example.Project.dto.response.AnalizaSezonierResponse;
import com.example.Project.dto.response.ClientSimilarResponse;
import com.example.Project.dto.response.GrupareClientiResponse;
import com.example.Project.dto.response.PredictieSezoneraResponse;
import com.example.Project.dto.response.ProfilCinematograficResponse;
import com.example.Project.dto.response.RecomandareResponse;
import com.example.Project.service.StatsService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST pentru analizele statistice si recomandarile personalizate.
 * Toate rutele sunt delegate procedurilor stocate Oracle prin {@link StatsService}.
 * Expune resursa {@code /api/stats}.
 */
@RestController
@RequestMapping("/api/stats")
public class StatsController {

    @Autowired
    private StatsService statsService;

    /**
     * Returneaza filmele recomandate pentru clientul autentificat.
     * @param req Cererea HTTP cu ID-ul clientului.
     * @return Lista filmelor recomandate bazata pe preferintele clientului.
     */
    @GetMapping("/recomandari")
    public List<RecomandareResponse> getRecomandari(HttpServletRequest req) {
        return statsService.getRecomandari(CurrentUser.getId(req));
    }

    /**
     * Returneaza profilul cinematografic al clientului autentificat.
     * @param req Cererea HTTP cu ID-ul clientului.
     * @return Profilul cu categoria preferata, actorul preferat, rating mediu etc.
     */
    @GetMapping("/profil")
    public ProfilCinematograficResponse getProfilCinematografic(HttpServletRequest req) {
        return statsService.getProfilCinematografic(CurrentUser.getId(req));
    }

    /**
     * Returneaza analiza sezoniera a vizionarilor pentru toate categoriile si lunile.
     * @return Lista cu numarul de vizionari si rangul fiecarei categorii pe fiecare luna.
     */
    @GetMapping("/sezon")
    public List<AnalizaSezonierResponse> getAnalizaSezoniera() {
        return statsService.getAnalizaSezoniera();
    }

    /**
     * Returneaza clientii cu preferinte similare cu ale clientului autentificat.
     * @param req  Cererea HTTP cu ID-ul clientului.
     * @param topN Numarul maxim de clienti similari de returnat (implicit 5, maxim 50).
     * @return Lista clientilor similari cu scorul de similaritate.
     */
    @GetMapping("/similari")
    public List<ClientSimilarResponse> getClientiSimilari(
            HttpServletRequest req,
            @RequestParam(defaultValue = "5") int topN) {
        return statsService.getClientiSimilari(CurrentUser.getId(req), topN);
    }

    /**
     * Returneaza predictiile sezoniere de vizionare pentru o luna specificata.
     * @param luna Luna calendaristica (1-12).
     * @param topN Numarul maxim de filme de returnat (implicit 10, maxim 50).
     * @return Lista filmelor cu scorul de predictie pentru luna data.
     */
    @GetMapping("/predictii")
    public List<PredictieSezoneraResponse> getPredictiiSezoniere(
            @RequestParam int luna,
            @RequestParam(defaultValue = "10") int topN) {
        return statsService.getPredictiiSezoniere(luna, topN);
    }

    /**
     * Returneaza gruparea clientilor dupa similaritatea preferintelor.
     * @param threshold Pragul de similaritate pentru grupare (implicit 0.3, intre 0 si 1).
     * @return Lista clientilor cu ID-ul grupei din care fac parte.
     */
    @GetMapping("/grupare")
    public List<GrupareClientiResponse> getGrupareClienti(
            @RequestParam(defaultValue = "0.3") double threshold) {
        return statsService.getGrupareClienti(threshold);
    }
}
