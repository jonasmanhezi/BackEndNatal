package com.amigosecreto.v1.infrastructure.supabase.sorteio;

import com.amigosecreto.v1.infrastructure.model.AtribuicaoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AtribuicaoRepository extends JpaRepository<AtribuicaoEntity, Long> {
}
