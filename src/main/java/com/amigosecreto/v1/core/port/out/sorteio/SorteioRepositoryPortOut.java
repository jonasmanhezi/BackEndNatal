package com.amigosecreto.v1.core.port.out.sorteio;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface SorteioRepositoryPortOut {

    /** Apaga o sorteio anterior e grava os pares novos (quem dá -> quem recebe, já cifrado). */
    void substituirSorteio(Map<Long, String> paresCifrados);

    void apagarSorteio();

    boolean isMembro(Long participanteId);

    Set<Long> findMembrosIds();

    long countMembros();

    /** O par de quem deu, ainda cifrado. Vazio se a pessoa não tem par. */
    Optional<String> findReceiverCifrado(Long giverId);

    void atualizarReceiverCifrado(Long giverId, String receiverCifrado);
}
