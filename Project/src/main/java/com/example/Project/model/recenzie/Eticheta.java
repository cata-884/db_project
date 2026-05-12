package com.example.Project.model.recenzie;

/**
 * Valorile posibile ale etichetelor ce pot fi asociate unei recenzii.
 * Etichetele sunt grupate pe categorii: sentiment general, reactie emotionala,
 * recomandare, aspecte specifice si context de vizionare.
 * Valorile sunt stocate ca sir de caractere in coloana {@code denumire} din tabelul {@code etichete}.
 */
public enum Eticheta {
    // Sentiment general
    MI_A_PLACUT,
    NU_MI_A_PLACUT,

    // Reacție emoțională
    EMOTIONANT,
    AMUZANT,
    INSPAIMANTATOR,
    RELAXANT,
    PLICTISITOR,
    SURPRINZATOR,

    // Recomandare
    AS_RECOMANDA,
    NU_AS_RECOMANDA,
    AS_MAI_VIZIONA,

    // Aspecte specifice
    SCENARIU_BUN,
    SCENARIU_SLAB,
    ACTOR_PRINCIPAL_APRECIAT,
    REGIE_EXCELENTA,
    COLOANA_SONORA_BUNA,
    EFECTE_VIZUALE_IMPRESIONANTE,

    // Context vizionare
    BUN_PENTRU_FAMILIE,
    BUN_PENTRU_COPII,
    PENTRU_ADULTI
}
