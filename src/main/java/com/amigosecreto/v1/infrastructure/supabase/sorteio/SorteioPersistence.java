package com.amigosecreto.v1.infrastructure.supabase.sorteio;

import com.amigosecreto.v1.core.port.out.sorteio.SorteioRepositoryPortOut;
import com.amigosecreto.v1.infrastructure.model.AtribuicaoEntity;
import com.amigosecreto.v1.infrastructure.model.SorteioMembroEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SorteioPersistence implements SorteioRepositoryPortOut {

    private final AtribuicaoRepository atribuicaoRepository;
    private final SorteioMembroRepository sorteioMembroRepository;

    @Override
    public void substituirSorteio(Map<Long, String> pares) {
        apagarSorteio();

        List<AtribuicaoEntity> atribuicoes = pares.entrySet().stream()
                .map(par -> AtribuicaoEntity.builder()
                        .giverId(par.getKey())
                        .receiverCifrado(par.getValue())
                        .build())
                .collect(Collectors.toList());

        List<SorteioMembroEntity> membros = pares.keySet().stream()
                .map(id -> SorteioMembroEntity.builder().participanteId(id).build())
                .collect(Collectors.toList());

        atribuicaoRepository.saveAll(atribuicoes);
        sorteioMembroRepository.saveAll(membros);
    }

    @Override
    public void apagarSorteio() {
        atribuicaoRepository.deleteAllInBatch();
        sorteioMembroRepository.deleteAllInBatch();
    }

    @Override
    public boolean isMembro(Long participanteId) {
        return sorteioMembroRepository.existsById(participanteId);
    }

    @Override
    public Set<Long> findMembrosIds() {
        return new HashSet<>(sorteioMembroRepository.findAllIds());
    }

    @Override
    public long countMembros() {
        return sorteioMembroRepository.count();
    }

    @Override
    public Optional<String> findReceiverCifrado(Long giverId) {
        return atribuicaoRepository.findById(giverId).map(AtribuicaoEntity::getReceiverCifrado);
    }

    @Override
    public void atualizarReceiverCifrado(Long giverId, String receiverCifrado) {
        atribuicaoRepository.save(AtribuicaoEntity.builder()
                .giverId(giverId)
                .receiverCifrado(receiverCifrado)
                .build());
    }
}
