package com.amigosecreto.v1.infrastructure.supabase.sorteio;

import com.amigosecreto.v1.infrastructure.model.SorteioMembroEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SorteioMembroRepository extends JpaRepository<SorteioMembroEntity, Long> {

    @Query("SELECT m.participanteId FROM SorteioMembroEntity m")
    List<Long> findAllIds();
}
