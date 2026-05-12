package com.example.Project.exception;

/**
 * Exceptie aruncata cand un client autentificat incearca sa modifice o resursa
 * care nu ii apartine.
 * Gestionata de {@link GlobalExceptionHandler} si tradusa in raspuns HTTP 403 Forbidden.
 */
public class ForbiddenException extends RuntimeException {
    /**
     * Creeaza o noua exceptie cu mesajul specificat.
     * @param message Descrierea operatiei interzise.
     */
    public ForbiddenException(String message) {
        super(message);
    }
}
