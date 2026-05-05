package com.example.Project.model.actor;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RolActor {
    PROTAGONIST("Principal"),
    ANTAGONIST("Antagonist"),
    SECUNDAR("Secundar"),
    EPISODIC("Episodic"),
    DUBLURA("Dublură");

    private final String eticheta;
}
