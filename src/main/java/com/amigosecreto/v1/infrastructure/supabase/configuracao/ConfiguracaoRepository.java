package com.amigosecreto.v1.infrastructure.supabase.configuracao;

import com.amigosecreto.v1.infrastructure.model.ConfiguracaoEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConfiguracaoRepository extends JpaRepository<ConfiguracaoEntity, Short> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM ConfiguracaoEntity c WHERE c.id = :id")
    Optional<ConfiguracaoEntity> findByIdComBloqueio(@Param("id") Short id);
}
