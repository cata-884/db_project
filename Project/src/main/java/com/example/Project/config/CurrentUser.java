package com.example.Project.config;

import jakarta.servlet.http.HttpServletRequest;

public class CurrentUser {

    public static Long getId(HttpServletRequest req) {
        Object id = req.getAttribute("idClientCurent");
        if (id == null) {
            throw new RuntimeException("Utilizator neautentificat — interceptorul nu a setat idClientCurent");
        }
        return (Long) id;
    }

    public static String getToken(HttpServletRequest req) {
        return (String) req.getAttribute("tokenCurent");
    }
}
