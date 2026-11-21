package com.amigosecreto.v1.api.rest.controller.participante;

import com.amigosecreto.v1.api.rest.config.OpenApiConfig;
import com.amigosecreto.v1.core.port.in.dto.request.participante.ParticipanteRequestDto;
import com.amigosecreto.v1.core.port.in.dto.response.participante.AtribuicaoResponseDto;
import com.amigosecreto.v1.core.port.in.dto.response.participante.ParticipantePublicoResponseDto;
import com.amigosecreto.v1.core.port.in.dto.response.participante.ParticipanteResponseDto;
import com.amigosecreto.v1.core.port.in.dto.response.participante.ParticipanteTokenResponseDto;
import com.amigosecreto.v1.security.AuthenticatedParticipanteId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Tag(name = "Participante", description = "Cadastro e quem eu tirei")
@RequestMapping("/api")
public interface ParticipanteController {

    @Operation(summary = "Lista pública de participantes (sem tamanhos)")
    @GetMapping("/participants")
    ResponseEntity<List<ParticipantePublicoResponseDto>> listar();

    @Operation(summary = "Cadastro pelo próprio celular. Devolve o token pessoal uma única vez")
    @PostMapping("/participants")
    ResponseEntity<ParticipanteTokenResponseDto> cadastrar(@RequestBody @Valid ParticipanteRequestDto request);

    @Operation(summary = "Meu cadastro", security = @SecurityRequirement(name = OpenApiConfig.PARTICIPANT_TOKEN))
    @GetMapping("/me")
    ResponseEntity<ParticipanteResponseDto> buscarMeuCadastro(
            @Parameter(hidden = true) @AuthenticatedParticipanteId Long participanteId
    );

    @Operation(summary = "Atualizar meu cadastro (substitui todos os campos)",
            security = @SecurityRequirement(name = OpenApiConfig.PARTICIPANT_TOKEN))
    @PutMapping("/me")
    ResponseEntity<ParticipanteResponseDto> atualizarMeuCadastro(
            @Parameter(hidden = true) @AuthenticatedParticipanteId Long participanteId,
            @RequestBody @Valid ParticipanteRequestDto request
    );

    @Operation(summary = "Sair do sorteio (bloqueado depois do sorteio)",
            security = @SecurityRequirement(name = OpenApiConfig.PARTICIPANT_TOKEN))
    @DeleteMapping("/me")
    ResponseEntity<Void> sair(@Parameter(hidden = true) @AuthenticatedParticipanteId Long participanteId);

    @Operation(summary = "Quem eu tirei", security = @SecurityRequirement(name = OpenApiConfig.PARTICIPANT_TOKEN))
    @GetMapping("/me/assignment")
    ResponseEntity<AtribuicaoResponseDto> buscarMeuAmigoSecreto(
            @Parameter(hidden = true) @AuthenticatedParticipanteId Long participanteId
    );
}
