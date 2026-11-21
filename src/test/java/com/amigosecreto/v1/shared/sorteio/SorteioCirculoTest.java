package com.amigosecreto.v1.shared.sorteio;

import com.amigosecreto.v1.shared.constant.Commons;
import org.junit.jupiter.api.Test;

import java.security.SecureRandom;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SorteioCirculoTest {

    private final Random random = new SecureRandom();

    @Test
    void sortear_menosDeTresPessoas_lancaExcecao() {
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> SorteioCirculo.sortear(List.of(1L, 2L), random)
        );
        assertEquals(Commons.MSG_SORTEIO_MINIMO, ex.getMessage());
    }

    @Test
    void sortear_ninguemTiraASiMesmo_eTodosDaoERecebemUmaVez() {
        for (int n = 3; n <= 30; n++) {
            List<Long> ids = ids(n);
            for (int rodada = 0; rodada < 50; rodada++) {
                Map<Long, Long> pares = SorteioCirculo.sortear(ids, random);

                assertEquals(new HashSet<>(ids), pares.keySet());
                assertEquals(new HashSet<>(ids), new HashSet<>(pares.values()));
                pares.forEach((quemDa, quemRecebe) -> assertNotEquals(quemDa, quemRecebe));
            }
        }
    }

    @Test
    void sortear_semParFechado_comTresOuMais() {
        for (int n = 3; n <= 30; n++) {
            Map<Long, Long> pares = SorteioCirculo.sortear(ids(n), random);
            pares.forEach((a, b) -> assertNotEquals(a, pares.get(b), "par fechado entre " + a + " e " + b));
        }
    }

    @Test
    void sortear_formaUmCirculoUnico() {
        List<Long> ids = ids(12);
        Map<Long, Long> pares = SorteioCirculo.sortear(ids, random);

        Set<Long> visitados = new HashSet<>();
        Long atual = ids.get(0);
        while (visitados.add(atual)) {
            atual = pares.get(atual);
        }
        assertEquals(ids.size(), visitados.size());
    }

    private List<Long> ids(int n) {
        return LongStream.rangeClosed(1, n).boxed().collect(Collectors.toList());
    }
}
