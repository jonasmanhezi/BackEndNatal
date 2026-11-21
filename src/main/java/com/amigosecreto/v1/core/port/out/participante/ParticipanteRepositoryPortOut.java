package com.amigosecreto.v1.core.port.out.participante;

import com.amigosecreto.v1.core.domain.entity.participante.Participante;

import java.util.List;
import java.util.Optional;

public interface ParticipanteRepositoryPortOut {

    List<Participante> findAll();

    List<Long> findAllIds();

    Optional<Participante> findById(Long id);

    Optional<Long> findIdByTokenHash(String tokenHash);

    Participante save(Participante participante);

    boolean existsById(Long id);

    void deleteById(Long id);
}
