package com.amigosecreto.v1.core.port.out.sorteio;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface SorteioRepositoryPortOut {

    /** Apaga o sorteio anterior e grava os pares novos (quem dá -> quem recebe). */
    void substituirSorteio(Map<Long, Long> pares);

    void apagarSorteio();

    boolean isMembro(Long participanteId);

    Set<Long> findMembrosIds();

    long countMembros();

    /** Quem a pessoa tirou. Vazio se ela não tem par ou se quem ela tirou foi removido (use {@link #existsAtribuicao}). */
    Optional<Long> findReceiverId(Long giverId);

    boolean existsAtribuicao(Long giverId);
}
