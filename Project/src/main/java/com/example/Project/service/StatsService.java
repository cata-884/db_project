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

@Service
public class StatsService {

    @Autowired
    private StatsDao statsDao;

    public List<RecomandareResponse> getRecomandari(Long idClient) {
        return statsDao.recomandari(idClient);
    }

    public ProfilCinematograficResponse getProfilCinematografic(Long idClient) {
        ProfilCinematograficResponse profil = statsDao.profilCinematografic(idClient);
        if (profil == null) throw new NotFoundException("Clientul cu id " + idClient + " nu există");
        return profil;
    }

    public List<AnalizaSezonierResponse> getAnalizaSezoniera() {
        return statsDao.analizaSezoniera();
    }

    public List<ClientSimilarResponse> getClientiSimilari(Long idClient, int topN) {
        if (topN < 1 || topN > 50) throw new IllegalArgumentException("topN trebuie să fie între 1 și 50");
        return statsDao.clientiSimilari(idClient, topN);
    }

    public List<PredictieSezoneraResponse> getPredictiiSezoniere(int luna, int topN) {
        if (luna < 1 || luna > 12) throw new IllegalArgumentException("luna trebuie să fie între 1 și 12");
        if (topN < 1 || topN > 50) throw new IllegalArgumentException("topN trebuie să fie între 1 și 50");
        return statsDao.predictiiSezoniere(luna, topN);
    }

    public List<GrupareClientiResponse> getGrupareClienti(double threshold) {
        return statsDao.grupareClienti(threshold);
    }
}
