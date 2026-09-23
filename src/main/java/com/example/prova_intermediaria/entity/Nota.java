package com.example.prova_intermediaria.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Nota {
    UM(1),
    DOIS(2),
    TRES(3),
    QUATRO(4),
    CINCO(5);

    private final int valor;

    Nota(int valor) {
        this.valor = valor;
    }

    @JsonCreator
    public static Nota fromValor(int valor) {
        for (Nota nota : Nota.values()) {
            if (nota.valor == valor) {
                return nota;
            }
        }

        throw new IllegalArgumentException("Nota deve estar entre 1 e 5");
    }

    @JsonValue
    public int getValor() {
        return valor;
    }
}