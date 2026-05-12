package com.example.Project.model.client;

/**
 * Starile posibile ale unei vizualizari de film.
 * Stocata ca sir de caractere in coloana {@code stare} din tabelul {@code vizualizari}.
 */
public enum StareVizualizare {
    /** Vizionarea este in desfasurare. */
    IN_PROGRESS,
    /** Vizionarea a fost finalizata. */
    COMPLETED,
    /** Vizionarea a fost intrerupta temporar. */
    PAUSED,
    /** Vizionarea a fost abandonata. */
    ABANDONED
}
