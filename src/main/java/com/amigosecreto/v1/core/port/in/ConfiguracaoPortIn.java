package com.amigosecreto.v1.core.port.in;

import com.amigosecreto.v1.core.domain.entity.configuracao.Configuracao;
import com.amigosecreto.v1.core.port.in.dto.request.configuracao.ConfiguracaoUpdateRequestDto;

public interface ConfiguracaoPortIn {

    Configuracao buscar();

    Configuracao atualizar(ConfiguracaoUpdateRequestDto request);

    long quantidadeNoSorteio();
}
