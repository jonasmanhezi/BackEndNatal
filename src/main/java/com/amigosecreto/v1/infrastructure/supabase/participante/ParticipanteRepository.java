package com.amigosecreto.v1.infrastructure.supabase.participante;

import com.amigosecreto.v1.infrastructure.model.ParticipanteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParticipanteRepository extends JpaRepository<ParticipanteEntity, Long> {

    List<ParticipanteEntity> findAllByOrderByCreatedAtAscIdAsc();

    @Query("SELECT p.id FROM ParticipanteEntity p ORDER BY p.id")
    List<Long> findAllIds();

    @Query("SELECT p.id FROM ParticipanteEntity p WHERE p.tokenHash = :tokenHash")
    Optional<Long> findIdByTokenHash(@Param("tokenHash") String tokenHash);
}
