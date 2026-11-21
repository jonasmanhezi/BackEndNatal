package com.amigosecreto.v1.api.rest.controller.configuracao;

import com.amigosecreto.v1.core.port.in.dto.response.configuracao.ConfiguracaoResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Público", description = "Configuração e estado do sorteio")
@RequestMapping("/api")
public interface ConfiguracaoController {

    @Operation(summary = "Nome do grupo, datas, valor e estado do sorteio")
    @GetMapping("/config")
    ResponseEntity<ConfiguracaoResponseDto> buscar();
}
