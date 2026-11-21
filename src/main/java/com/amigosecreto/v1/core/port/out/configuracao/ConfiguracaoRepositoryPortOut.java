package com.amigosecreto.v1.core.port.out.configuracao;

import com.amigosecreto.v1.core.domain.entity.configuracao.Configuracao;

public interface ConfiguracaoRepositoryPortOut {

    Configuracao buscar();

    /** Mesma configuração, mas trava a linha até o fim da transação (evita dois sorteios ao mesmo tempo). */
    Configuracao buscarComBloqueio();

    Configuracao salvar(Configuracao configuracao);
}
