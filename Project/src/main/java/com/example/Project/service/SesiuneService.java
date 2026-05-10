package com.example.Project.service;

import com.example.Project.dao.SesiuneDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SesiuneService {

    @Autowired
    private SesiuneDao sesiuneDao;

    public String creeazaTokenPentru(Long idClient) {
        return sesiuneDao.creeaza(idClient);
    }

    public Optional<Long> validareToken(String token) {
        return sesiuneDao.gasesteClientPentruToken(token);
    }

    public void invalideazaToken(String token) {
        sesiuneDao.sterge(token);
    }
}
