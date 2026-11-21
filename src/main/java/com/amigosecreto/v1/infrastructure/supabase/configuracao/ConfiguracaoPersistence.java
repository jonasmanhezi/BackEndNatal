package com.amigosecreto.v1.infrastructure.supabase.configuracao;

import com.amigosecreto.v1.core.domain.entity.configuracao.Configuracao;
import com.amigosecreto.v1.core.port.out.configuracao.ConfiguracaoRepositoryPortOut;
import com.amigosecreto.v1.infrastructure.model.ConfiguracaoEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Component
@RequiredArgsConstructor
public class ConfiguracaoPersistence implements ConfiguracaoRepositoryPortOut {

    private final ConfiguracaoRepository configuracaoRepository;

    @Override
    public Configuracao buscar() {
        return configuracaoRepository.findById(ConfiguracaoEntity.ID_UNICO)
                .map(this::toDomain)
                .orElseThrow(() -> new IllegalStateException("Configuração não encontrada. Rode as migrations."));
    }

    @Override
    public Configuracao buscarComBloqueio() {
        return configuracaoRepository.findByIdComBloqueio(ConfiguracaoEntity.ID_UNICO)
                .map(this::toDomain)
                .orElseThrow(() -> new IllegalStateException("Configuração não encontrada. Rode as migrations."));
    }

    @Override
    public Configuracao salvar(Configuracao configuracao) {
        ConfiguracaoEntity entity = ConfiguracaoEntity.builder()
                .id(ConfiguracaoEntity.ID_UNICO)
                .titulo(configuracao.getTitulo())
                .dataSorteio(configuracao.getDataSorteio())
                .dataTroca(configuracao.getDataTroca())
                .valor(configuracao.getValor())
                .status(configuracao.getStatus().name())
                .sorteadoEm(configuracao.getSorteadoEm() == null ? null
                        : configuracao.getSorteadoEm().atOffset(ZoneOffset.UTC))
                .updatedAt(OffsetDateTime.now())
                .build();

        return toDomain(configuracaoRepository.save(entity));
    }

    private Configuracao toDomain(ConfiguracaoEntity entity) {
        return Configuracao.builder()
                .titulo(entity.getTitulo())
                .dataSorteio(entity.getDataSorteio())
                .dataTroca(entity.getDataTroca())
                .valor(entity.getValor())
                .status(Configuracao.StatusSorteio.valueOf(entity.getStatus()))
                .sorteadoEm(entity.getSorteadoEm() == null ? null : entity.getSorteadoEm().toInstant())
                .build();
    }
}
