package com.scout.scoutmanagement.domain;

import java.util.Optional;

public enum NivelCurso {
    NIVEL_1,
    NIVEL_2,
    NIVEL_3;

    public Optional<NivelCurso> nivelRequeridoAnterior() {
        return switch (this) {
            case NIVEL_1 -> Optional.empty();
            case NIVEL_2 -> Optional.of(NIVEL_1);
            case NIVEL_3 -> Optional.of(NIVEL_2);
        };
    }

    public Optional<NivelCurso> siguienteNivel() {
        return switch (this) {
            case NIVEL_1 -> Optional.of(NIVEL_2);
            case NIVEL_2 -> Optional.of(NIVEL_3);
            case NIVEL_3 -> Optional.empty();
        };
    }
}


