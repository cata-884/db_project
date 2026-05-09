package com.example.Project.controller;

import com.example.Project.dto.response.AnalizaSezonierResponse;
import com.example.Project.dto.response.ClientSimilarResponse;
import com.example.Project.dto.response.GrupareClientiResponse;
import com.example.Project.dto.response.PredictieSezoneraResponse;
import com.example.Project.dto.response.ProfilCinematograficResponse;
import com.example.Project.dto.response.RecomandareResponse;
import com.example.Project.service.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stats")
public class StatsController {
    @Autowired
    private StatsService statsService;

    @GetMapping("/recomandari/{idClient}")
    public List<RecomandareResponse> getRecomandari(@PathVariable Long idClient) {
        return statsService.getRecomandari(idClient);
    }

    @GetMapping("/profil/{idClient}")
    public ProfilCinematograficResponse getProfilCinematografic(@PathVariable Long idClient) {
        return statsService.getProfilCinematografic(idClient);
    }

    @GetMapping("/sezon")
    public List<AnalizaSezonierResponse> getAnalizaSezoniera() {
        return statsService.getAnalizaSezoniera();
    }

    @GetMapping("/similari/{idClient}")
    public List<ClientSimilarResponse> getClientiSimilari(
            @PathVariable Long idClient,
            @RequestParam(defaultValue = "5") int topN) {
        return statsService.getClientiSimilari(idClient, topN);
    }

    @GetMapping("/predictii")
    public List<PredictieSezoneraResponse> getPredictiiSezoniere(
            @RequestParam int luna,
            @RequestParam(defaultValue = "10") int topN) {
        return statsService.getPredictiiSezoniere(luna, topN);
    }

    @GetMapping("/grupare")
    public List<GrupareClientiResponse> getGrupareClienti(
            @RequestParam(defaultValue = "0.3") double threshold) {
        return statsService.getGrupareClienti(threshold);
    }
}
