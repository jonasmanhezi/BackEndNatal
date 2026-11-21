package com.amigosecreto.v1.core.application.service.sorteio;

import com.amigosecreto.v1.core.domain.entity.configuracao.Configuracao;
import com.amigosecreto.v1.core.domain.entity.participante.Participante;
import com.amigosecreto.v1.core.port.in.SorteioPortIn;
import com.amigosecreto.v1.core.port.out.configuracao.ConfiguracaoRepositoryPortOut;
import com.amigosecreto.v1.core.port.out.participante.ParticipanteRepositoryPortOut;
import com.amigosecreto.v1.core.port.out.sorteio.SorteioRepositoryPortOut;
import com.amigosecreto.v1.shared.constant.Commons;
import com.amigosecreto.v1.shared.cripto.ParSecreto;
import com.amigosecreto.v1.shared.participante.NomeUtil;
import com.amigosecreto.v1.shared.sorteio.SorteioCirculo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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

        List<Participante> participantes = participanteRepositoryPortOut.findAll();

        List<String> semLink = participantes.stream()
                .filter(p -> p.getToken() == null)
                .map(Participante::getNome)
                .collect(Collectors.toList());
        if (!semLink.isEmpty()) {
            throw new IllegalStateException(String.format(Commons.MSG_SORTEIO_SEM_LINK, String.join(", ", semLink)));
        }

        // Cadastros duplicados de antes da trava de nomes: a mesma pessoa contaria duas vezes.
        List<String> repetidos = participantes.stream()
                .collect(Collectors.groupingBy(p -> NomeUtil.normalizar(p.getNome())))
                .values().stream()
                .filter(grupo -> grupo.size() > 1)
                .map(grupo -> grupo.get(0).getNome())
                .collect(Collectors.toList());
        if (!repetidos.isEmpty()) {
            throw new IllegalStateException(String.format(Commons.MSG_SORTEIO_NOMES_REPETIDOS, String.join(", ", repetidos)));
        }

        Map<Long, String> tokens = participantes.stream()
                .collect(Collectors.toMap(Participante::getId, Participante::getToken));
        Map<Long, Long> pares = SorteioCirculo.sortear(List.copyOf(tokens.keySet()), RANDOM);

        Map<Long, String> paresCifrados = new HashMap<>();
        pares.forEach((giverId, receiverId) ->
                paresCifrados.put(giverId, ParSecreto.cifrar(tokens.get(giverId), giverId, receiverId)));

        sorteioRepositoryPortOut.substituirSorteio(paresCifrados);

        configuracao.setStatus(Configuracao.StatusSorteio.DRAWN);
        configuracao.setSorteadoEm(Instant.now());
        log.info("Sorteio feito com {} pessoas", pares.size());
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
    public MeuAmigoSecreto buscarMeuAmigoSecreto(Long participanteId, String token) {
        if (!configuracaoRepositoryPortOut.buscar().isSorteado()) {
            return new MeuAmigoSecreto(null, MeuAmigoSecreto.NAO_SORTEADO);
        }

        Optional<String> receiverCifrado = sorteioRepositoryPortOut.findReceiverCifrado(participanteId);
        if (!sorteioRepositoryPortOut.isMembro(participanteId) || receiverCifrado.isEmpty()) {
            return new MeuAmigoSecreto(null, MeuAmigoSecreto.ENTROU_DEPOIS);
        }

        // Se quem foi tirado saiu (ou o par não abre), a corrente quebrou: o organizador refaz o sorteio.
        Optional<Participante> amigo = ParSecreto.decifrar(token, participanteId, receiverCifrado.get())
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
