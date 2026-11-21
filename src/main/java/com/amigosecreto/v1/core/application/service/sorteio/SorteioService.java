package com.amigosecreto.v1.core.application.service.sorteio;

import com.amigosecreto.v1.core.domain.entity.configuracao.Configuracao;
import com.amigosecreto.v1.core.domain.entity.participante.Participante;
import com.amigosecreto.v1.core.port.in.SorteioPortIn;
import com.amigosecreto.v1.core.port.out.configuracao.ConfiguracaoRepositoryPortOut;
import com.amigosecreto.v1.core.port.out.participante.ParticipanteRepositoryPortOut;
import com.amigosecreto.v1.core.port.out.sorteio.SorteioRepositoryPortOut;
import com.amigosecreto.v1.shared.sorteio.SorteioCirculo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class SorteioService implements SorteioPortIn {

    private static final SecureRandom RANDOM = new SecureRandom();

    private final ConfiguracaoRepositoryPortOut configuracaoRepositoryPortOut;
    private final ParticipanteRepositoryPortOut participanteRepositoryPortOut;
    private final SorteioRepositoryPortOut sorteioRepositoryPortOut;

    @Override
    @Transactional
    public Configuracao sortear() {
        Configuracao configuracao = configuracaoRepositoryPortOut.buscarComBloqueio();

        List<Long> ids = participanteRepositoryPortOut.findAllIds();
        Map<Long, Long> pares = SorteioCirculo.sortear(ids, RANDOM);

        sorteioRepositoryPortOut.substituirSorteio(pares);

        configuracao.setStatus(Configuracao.StatusSorteio.DRAWN);
        configuracao.setSorteadoEm(Instant.now());
        log.info("Sorteio feito com {} pessoas", ids.size());
        return configuracaoRepositoryPortOut.salvar(configuracao);
    }

    @Override
    @Transactional
    public Configuracao reabrir() {
        Configuracao configuracao = configuracaoRepositoryPortOut.buscarComBloqueio();

        sorteioRepositoryPortOut.apagarSorteio();

        configuracao.setStatus(Configuracao.StatusSorteio.OPEN);
        configuracao.setSorteadoEm(null);
        log.info("Inscrições reabertas");
        return configuracaoRepositoryPortOut.salvar(configuracao);
    }

    @Override
    @Transactional(readOnly = true)
    public MeuAmigoSecreto buscarMeuAmigoSecreto(Long participanteId) {
        if (!configuracaoRepositoryPortOut.buscar().isSorteado()) {
            return new MeuAmigoSecreto(null, MeuAmigoSecreto.NAO_SORTEADO);
        }

        if (!sorteioRepositoryPortOut.isMembro(participanteId)
                || !sorteioRepositoryPortOut.existsAtribuicao(participanteId)) {
            return new MeuAmigoSecreto(null, MeuAmigoSecreto.ENTROU_DEPOIS);
        }

        Optional<Participante> amigo = sorteioRepositoryPortOut.findReceiverId(participanteId)
                .flatMap(participanteRepositoryPortOut::findById);

        return amigo
                .map(p -> new MeuAmigoSecreto(p, null))
                .orElseGet(() -> new MeuAmigoSecreto(null, MeuAmigoSecreto.AMIGO_SAIU));
    }

    @Override
    @Transactional(readOnly = true)
    public Set<Long> idsNoSorteio() {
        return sorteioRepositoryPortOut.findMembrosIds();
    }
}
