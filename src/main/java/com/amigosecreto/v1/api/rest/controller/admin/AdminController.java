package com.amigosecreto.v1.api.rest.controller.admin;

import com.amigosecreto.v1.api.rest.config.OpenApiConfig;
import com.amigosecreto.v1.core.port.in.dto.request.configuracao.ConfiguracaoUpdateRequestDto;
import com.amigosecreto.v1.core.port.in.dto.request.participante.ParticipanteRequestDto;
import com.amigosecreto.v1.core.port.in.dto.response.admin.AdminCheckResponseDto;
import com.amigosecreto.v1.core.port.in.dto.response.admin.ParticipanteAdminResponseDto;
import com.amigosecreto.v1.core.port.in.dto.response.admin.TokenResponseDto;
import com.amigosecreto.v1.core.port.in.dto.response.configuracao.ConfiguracaoResponseDto;
import com.amigosecreto.v1.core.port.in.dto.response.participante.ParticipanteResponseDto;
import com.amigosecreto.v1.core.port.in.dto.response.participante.ParticipanteTokenResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Tag(name = "Organizador", description = "Ajustes, cadastros e sorteio. Nunca devolve os pares")
@SecurityRequirement(name = OpenApiConfig.ADMIN_KEY)
@RequestMapping("/api/admin")
public interface AdminController {

    @Operation(summary = "Confere a chave de organizador")
    @GetMapping("/check")
    ResponseEntity<AdminCheckResponseDto> conferirChave();

    @Operation(summary = "Atualizar nome do grupo, datas e valor")
    @PutMapping("/config")
    ResponseEntity<ConfiguracaoResponseDto> atualizarConfiguracao(@RequestBody @Valid ConfiguracaoUpdateRequestDto request);

    @Operation(summary = "Todos os cadastros completos")
    @GetMapping("/participants")
    ResponseEntity<List<ParticipanteAdminResponseDto>> listarParticipantes();

    @Operation(summary = "Cadastrar alguém (manual = true). Devolve o token para mandar pelo WhatsApp")
    @PostMapping("/participants")
    ResponseEntity<ParticipanteTokenResponseDto> cadastrarParticipante(@RequestBody @Valid ParticipanteRequestDto request);

    @Operation(summary = "Editar um cadastro")
    @PutMapping("/participants/{id}")
    ResponseEntity<ParticipanteResponseDto> atualizarParticipante(
            @PathVariable("id") String id,
            @RequestBody @Valid ParticipanteRequestDto request
    );

    @Operation(summary = "Remover um cadastro")
    @DeleteMapping("/participants/{id}")
    ResponseEntity<Void> removerParticipante(@PathVariable("id") String id);

    @Operation(summary = "Gerar novo link pessoal (o anterior para de funcionar)")
    @PostMapping("/participants/{id}/link")
    ResponseEntity<TokenResponseDto> gerarNovoLink(@PathVariable("id") String id);

    @Operation(summary = "Fazer (ou refazer) o sorteio. Mínimo de 3 pessoas")
    @PostMapping("/draw")
    ResponseEntity<ConfiguracaoResponseDto> sortear();

    @Operation(summary = "Apagar os pares e reabrir as inscrições")
    @PostMapping("/reopen")
    ResponseEntity<ConfiguracaoResponseDto> reabrir();
}
