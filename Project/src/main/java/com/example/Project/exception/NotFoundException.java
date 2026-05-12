package com.example.Project.exception;

/**
 * Exceptie aruncata cand o resursa cautata nu exista in baza de date.
 * Gestionata de {@link GlobalExceptionHandler} si tradusa in raspuns HTTP 404 Not Found.
 */
public class NotFoundException extends RuntimeException {
    /**
     * Creeaza o noua exceptie cu mesajul specificat.
     * @param message Descrierea resursei care nu a fost gasita.
     */
    public NotFoundException(String message) {
        super(message);
    }
}

