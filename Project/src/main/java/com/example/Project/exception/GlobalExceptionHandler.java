package com.example.Project.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntime(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(org.springframework.dao.DataAccessException.class)
    public ResponseEntity<Map<String, String>> handleDataAccess(org.springframework.dao.DataAccessException ex) {
        Throwable cause = ex.getCause();
        if (cause instanceof SQLException sqlEx) {
            int code = sqlEx.getErrorCode();
            if (code == 1) {
                // ORA-00001: unique constraint violated
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of("error", "Înregistrare duplicată: " + sqlEx.getMessage()));
            }
            if (code == 20001) {
                // ORA-20001: trigger trg_validare_actor_recenzie
                return ResponseEntity.badRequest()
                        .body(Map.of("error", sqlEx.getMessage()));
            }
            if (code == 2290) {
                // ORA-02290: check constraint violated
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Valoare invalidă: " + sqlEx.getMessage()));
            }
            if (code == 2291) {
                // ORA-02291: integrity constraint (FK) violated
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Referință inexistentă: " + sqlEx.getMessage()));
            }
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", ex.getMessage()));
    }
}
