package com.amigosecreto.v1.api.rest.controller.participante;

import com.amigosecreto.v1.api.rest.mapper.RespostaMapper;
import com.amigosecreto.v1.core.port.in.ParticipantePortIn;
import com.amigosecreto.v1.core.port.in.SorteioPortIn;
import com.amigosecreto.v1.core.port.in.dto.request.participante.ParticipanteRequestDto;
import com.amigosecreto.v1.core.port.in.dto.response.participante.AtribuicaoResponseDto;
import com.amigosecreto.v1.core.port.in.dto.response.participante.ParticipantePublicoResponseDto;
import com.amigosecreto.v1.core.port.in.dto.response.participante.ParticipanteResponseDto;
import com.amigosecreto.v1.core.port.in.dto.response.participante.ParticipanteTokenResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class ParticipanteControllerImpl implements ParticipanteController {

    private final ParticipantePortIn participanteService;
    private final SorteioPortIn sorteioService;

    @Override
    public ResponseEntity<List<ParticipantePublicoResponseDto>> listar() {
        List<ParticipantePublicoResponseDto> participantes = participanteService.listar().stream()
                .map(RespostaMapper::toPublicoDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(participantes);
    }

    @Override
    public ResponseEntity<ParticipanteTokenResponseDto> cadastrar(ParticipanteRequestDto request) {
        ParticipantePortIn.CadastroResultado resultado = participanteService.cadastrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ParticipanteTokenResponseDto.builder()
                .participante(RespostaMapper.toDto(resultado.participante()))
                .token(resultado.token())
                .build());
    }

    @Override
    public ResponseEntity<ParticipanteResponseDto> buscarMeuCadastro(Long participanteId) {
        return ResponseEntity.ok(RespostaMapper.toDto(participanteService.buscarPorId(participanteId)));
    }

    @Override
    public ResponseEntity<ParticipanteResponseDto> atualizarMeuCadastro(Long participanteId, ParticipanteRequestDto request) {
        return ResponseEntity.ok(RespostaMapper.toDto(participanteService.atualizar(participanteId, request)));
    }

    @Override
    public ResponseEntity<Void> sair(Long participanteId) {
        participanteService.sair(participanteId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<AtribuicaoResponseDto> buscarMeuAmigoSecreto(Long participanteId) {
        SorteioPortIn.MeuAmigoSecreto resultado = sorteioService.buscarMeuAmigoSecreto(participanteId);
        return ResponseEntity.ok(AtribuicaoResponseDto.builder()
                .amigoSecreto(resultado.amigoSecreto() == null ? null
                        : RespostaMapper.toAmigoSecretoDto(resultado.amigoSecreto()))
                .motivo(resultado.motivo())
                .build());
    }
}
