package com.example.Project.config;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Clasa utilitara ce ofera acces convenabil la datele clientului autentificat
 * stocate ca atribute pe {@link HttpServletRequest} de catre {@link AuthInterceptor}.
 *
 * <p>Structura unui request autentificat:</p>
 * <pre>
 * {
 *   "headers": { "authorization": "Bearer a7f93b2c8e4d..." },
 *   "attributes": {
 *     "tokenCurent": "a7f93b2c8e4d...",
 *     "idClientCurent": 45
 *   }
 * }
 * </pre>
 */
public class CurrentUser {

    /**
     * Returneaza ID-ul clientului autentificat din atributele request-ului.
     *
     * @param req Cererea HTTP curenta, cu atributele setate de {@link AuthInterceptor}.
     * @return ID-ul clientului curent.
     * @throws RuntimeException daca interceptorul nu a setat atributul (cerere neautentificata).
     */
    public static Long getId(HttpServletRequest req) {
        Object id = req.getAttribute("idClientCurent");
        if (id == null) {
            throw new RuntimeException("Utilizator neautentificat — interceptorul nu a setat idClientCurent");
        }
        return (Long) id;
    }

    /**
     * Returneaza token-ul de sesiune curent din atributele request-ului.
     * Folosit in principal la logout pentru a invalida sesiunea.
     *
     * @param req Cererea HTTP curenta.
     * @return Token-ul Bearer al sesiunii curente, sau {@code null} daca nu este setat.
     */
    public static String getToken(HttpServletRequest req) {
        return (String) req.getAttribute("tokenCurent");
    }
}
