package com.amigosecreto.v1.shared.ratelimit;

import java.time.Clock;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Limite simples por chave (ex.: IP) numa janela fixa, em memória.
 * Suficiente para uma instância só (plano free do Render). Com várias instâncias, troque por Redis.
 */
public class LimitadorTentativas {

    private static final int MAX_CHAVES = 10_000;

    private final int maximo;
    private final Duration janela;
    private final Clock clock;
    private final Map<String, Contador> contadores = new ConcurrentHashMap<>();

    public LimitadorTentativas(int maximo, Duration janela) {
        this(maximo, janela, Clock.systemUTC());
    }

    LimitadorTentativas(int maximo, Duration janela, Clock clock) {
        this.maximo = maximo;
        this.janela = janela;
        this.clock = clock;
    }

    public boolean bloqueado(String chave) {
        Contador c = contadores.get(chave);
        return c != null && !c.expirado(clock.millis(), janela) && c.total >= maximo;
    }

    public void registrar(String chave) {
        long agora = clock.millis();
        if (contadores.size() > MAX_CHAVES) {
            contadores.values().removeIf(c -> c.expirado(agora, janela));
        }
        contadores.compute(chave, (k, c) -> {
            if (c == null || c.expirado(agora, janela)) {
                return new Contador(agora, 1);
            }
            return new Contador(c.inicio, c.total + 1);
        });
    }

    private record Contador(long inicio, int total) {

        boolean expirado(long agora, Duration janela) {
            return agora - inicio >= janela.toMillis();
        }
    }
}
