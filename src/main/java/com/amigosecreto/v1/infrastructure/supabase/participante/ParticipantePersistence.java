package com.amigosecreto.v1.infrastructure.supabase.participante;

import com.amigosecreto.v1.core.domain.entity.participante.Participante;
import com.amigosecreto.v1.core.port.out.participante.ParticipanteRepositoryPortOut;
import com.amigosecreto.v1.infrastructure.model.ParticipanteEntity;
import com.amigosecreto.v1.shared.cripto.Cifra;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ParticipantePersistence implements ParticipanteRepositoryPortOut {

    private final ParticipanteRepository participanteRepository;
    private final String adminKey;

    public ParticipantePersistence(ParticipanteRepository participanteRepository,
                                   @Value("${app.admin-key:}") String adminKey) {
        this.participanteRepository = participanteRepository;
        this.adminKey = adminKey == null ? "" : adminKey.trim();
    }

    @Override
    public List<Participante> findAll() {
        return participanteRepository.findAllByOrderByCreatedAtAscIdAsc().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Long> findAllIds() {
        return participanteRepository.findAllIds();
    }

    @Override
    public Optional<Participante> findById(Long id) {
        return participanteRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Long> findIdByTokenHash(String tokenHash) {
        return participanteRepository.findIdByTokenHash(tokenHash);
    }

    @Override
    public Participante save(Participante participante) {
        return toDomain(participanteRepository.save(toEntity(participante)));
    }

    @Override
    public boolean existsById(Long id) {
        return participanteRepository.existsById(id);
    }

    @Override
    public void deleteById(Long id) {
        participanteRepository.deleteById(id);
    }

    private ParticipanteEntity toEntity(Participante participante) {
        return ParticipanteEntity.builder()
                .id(participante.getId())
                .nome(participante.getNome())
                .foto(participante.getFoto())
                .calcado(participante.getCalcado())
                .calca(participante.getCalca())
                .camisa(participante.getCamisa())
                .gostos(participante.getGostos())
                .listaDesejos(participante.getListaDesejos())
                .evitar(participante.getEvitar())
                .manual(participante.isManual())
                .tokenHash(participante.getTokenHash())
                .tokenCifrado(cifrarToken(participante.getToken(), participante.getTokenHash()))
                .createdAt(toOffset(participante.getCriadoEm()))
                .updatedAt(toOffset(participante.getAtualizadoEm()))
                .build();
    }

    private Participante toDomain(ParticipanteEntity entity) {
        return Participante.builder()
                .id(entity.getId())
                .nome(entity.getNome())
                .foto(entity.getFoto())
                .calcado(entity.getCalcado())
                .calca(entity.getCalca())
                .camisa(entity.getCamisa())
                .gostos(entity.getGostos())
                .listaDesejos(entity.getListaDesejos())
                .evitar(entity.getEvitar())
                .manual(entity.isManual())
                .tokenHash(entity.getTokenHash())
                .token(decifrarToken(entity.getTokenCifrado(), entity.getTokenHash()))
                .criadoEm(entity.getCreatedAt() == null ? null : entity.getCreatedAt().toInstant())
                .atualizadoEm(entity.getUpdatedAt() == null ? null : entity.getUpdatedAt().toInstant())
                .build();
    }

    /** Sem ADMIN_KEY não há como guardar o token: o organizador só consegue gerar links novos. */
    private String cifrarToken(String token, String tokenHash) {
        if (token == null || adminKey.isEmpty()) {
            return null;
        }
        return Cifra.cifrar(adminKey, Cifra.PROPOSITO_TOKEN, token, tokenHash);
    }

    private String decifrarToken(String tokenCifrado, String tokenHash) {
        if (tokenCifrado == null) {
            return null;
        }
        return Cifra.decifrar(adminKey, Cifra.PROPOSITO_TOKEN, tokenCifrado, tokenHash).orElseGet(() -> {
            log.warn("Não deu para decifrar um link salvo (a ADMIN_KEY mudou?). Gere um link novo para essa pessoa.");
            return null;
        });
    }

    private static OffsetDateTime toOffset(Instant instant) {
        return instant == null ? OffsetDateTime.now() : instant.atOffset(ZoneOffset.UTC);
    }
}
