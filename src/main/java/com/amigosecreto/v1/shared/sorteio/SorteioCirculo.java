package com.amigosecreto.v1.shared.sorteio;

import com.amigosecreto.v1.shared.constant.Commons;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Sorteio em círculo único: embaralha (Fisher–Yates) e cada pessoa tira a próxima.
 * Ninguém tira a si mesmo e, com 3 ou mais pessoas, ninguém forma par fechado (A tira B e B tira A).
 * Mesmo algoritmo de js/draw.js no frontend.
 */
public final class SorteioCirculo {

    public static final int MINIMO_PARTICIPANTES = 3;

    private SorteioCirculo() {
    }

    /** @return quem dá -> quem recebe */
    public static <T> Map<T, T> sortear(List<T> ids, Random random) {
        if (ids == null || ids.size() < MINIMO_PARTICIPANTES) {
            throw new IllegalStateException(Commons.MSG_SORTEIO_MINIMO);
        }

        List<T> embaralhados = new ArrayList<>(ids);
        Collections.shuffle(embaralhados, random);

        Map<T, T> pares = new LinkedHashMap<>();
        for (int i = 0; i < embaralhados.size(); i++) {
            pares.put(embaralhados.get(i), embaralhados.get((i + 1) % embaralhados.size()));
        }
        return pares;
    }
}
