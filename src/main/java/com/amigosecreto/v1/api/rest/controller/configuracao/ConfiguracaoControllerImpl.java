package com.amigosecreto.v1.api.rest.controller.configuracao;

import com.amigosecreto.v1.api.rest.mapper.RespostaMapper;
import com.amigosecreto.v1.core.port.in.ConfiguracaoPortIn;
import com.amigosecreto.v1.core.port.in.dto.response.configuracao.ConfiguracaoResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ConfiguracaoControllerImpl implements ConfiguracaoController {

    private final ConfiguracaoPortIn configuracaoService;

    @Override
    public ResponseEntity<ConfiguracaoResponseDto> buscar() {
        return ResponseEntity.ok(RespostaMapper.toDto(
                configuracaoService.buscar(), configuracaoService.quantidadeNoSorteio()));
    }
}
