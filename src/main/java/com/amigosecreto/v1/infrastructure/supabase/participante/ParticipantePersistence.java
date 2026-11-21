package com.amigosecreto.v1.infrastructure.supabase.participante;

import com.amigosecreto.v1.core.domain.entity.participante.Participante;
import com.amigosecreto.v1.core.port.out.participante.ParticipanteRepositoryPortOut;
import com.amigosecreto.v1.infrastructure.model.ParticipanteEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ParticipantePersistence implements ParticipanteRepositoryPortOut {

    private final ParticipanteRepository participanteRepository;

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
                .criadoEm(entity.getCreatedAt() == null ? null : entity.getCreatedAt().toInstant())
                .atualizadoEm(entity.getUpdatedAt() == null ? null : entity.getUpdatedAt().toInstant())
                .build();
    }

    private static OffsetDateTime toOffset(Instant instant) {
        return instant == null ? OffsetDateTime.now() : instant.atOffset(ZoneOffset.UTC);
    }
}
