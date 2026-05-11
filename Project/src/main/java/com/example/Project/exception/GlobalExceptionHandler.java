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

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<Map<String, String>> handleForbidden(ForbiddenException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", ex.getMessage()));
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
            if (code == 20100 || code == 20101) {
                // ORA-20100: nota/text invalid, ORA-20101: array-uri nepotrivite
                return ResponseEntity.badRequest()
                        .body(Map.of("error", sqlEx.getMessage()));
            }
            if (code == 20102 || code == 20103) {
                // ORA-20102: client inexistent, ORA-20103: film inexistent
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", sqlEx.getMessage()));
            }
            if (code == 20104) {
                // ORA-20104: recenzie duplicata pentru acelasi client/film
                return ResponseEntity.status(HttpStatus.CONFLICT)
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
            if (code == 2292) {
                // ORA-02292: child record found — e.g. DELETE film cu recenzii existente
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of("error", "Nu se poate șterge: există înregistrări dependente."));
            }
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", ex.getMessage()));
    }
}
