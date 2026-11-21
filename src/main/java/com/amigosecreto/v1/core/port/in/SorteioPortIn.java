package com.amigosecreto.v1.core.port.in;

import com.amigosecreto.v1.core.domain.entity.configuracao.Configuracao;
import com.amigosecreto.v1.core.domain.entity.participante.Participante;

import java.util.Set;

public interface SorteioPortIn {

    Configuracao sortear();

    Configuracao reabrir();

    MeuAmigoSecreto buscarMeuAmigoSecreto(Long participanteId);

    Set<Long> idsNoSorteio();

    /** {@code amigoSecreto} preenchido ou {@code motivo} explicando por que não. */
    record MeuAmigoSecreto(Participante amigoSecreto, String motivo) {

        public static final String NAO_SORTEADO = "not_drawn";
        public static final String ENTROU_DEPOIS = "joined_after_draw";
        public static final String AMIGO_SAIU = "receiver_left";
    }
}
