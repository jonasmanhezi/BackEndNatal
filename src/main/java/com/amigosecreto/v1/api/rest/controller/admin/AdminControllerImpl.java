package com.amigosecreto.v1.api.rest.controller.admin;

import com.amigosecreto.v1.api.rest.mapper.IdParser;
import com.amigosecreto.v1.api.rest.mapper.RespostaMapper;
import com.amigosecreto.v1.core.domain.entity.configuracao.Configuracao;
import com.amigosecreto.v1.core.port.in.ConfiguracaoPortIn;
import com.amigosecreto.v1.core.port.in.ParticipantePortIn;
import com.amigosecreto.v1.core.port.in.SorteioPortIn;
import com.amigosecreto.v1.core.port.in.dto.request.configuracao.ConfiguracaoUpdateRequestDto;
import com.amigosecreto.v1.core.port.in.dto.request.participante.ParticipanteRequestDto;
import com.amigosecreto.v1.core.port.in.dto.response.admin.AdminCheckResponseDto;
import com.amigosecreto.v1.core.port.in.dto.response.admin.ParticipanteAdminResponseDto;
import com.amigosecreto.v1.core.port.in.dto.response.admin.TokenResponseDto;
import com.amigosecreto.v1.core.port.in.dto.response.configuracao.ConfiguracaoResponseDto;
import com.amigosecreto.v1.core.port.in.dto.response.participante.ParticipanteResponseDto;
import com.amigosecreto.v1.core.port.in.dto.response.participante.ParticipanteTokenResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class AdminControllerImpl implements AdminController {

    private final ConfiguracaoPortIn configuracaoService;
    private final ParticipantePortIn participanteService;
    private final SorteioPortIn sorteioService;

    @Override
    public ResponseEntity<AdminCheckResponseDto> conferirChave() {
        return ResponseEntity.ok(AdminCheckResponseDto.builder().ok(true).build());
    }

    @Override
    public ResponseEntity<ConfiguracaoResponseDto> atualizarConfiguracao(ConfiguracaoUpdateRequestDto request) {
        return ResponseEntity.ok(toDto(configuracaoService.atualizar(request)));
    }

    @Override
    public ResponseEntity<List<ParticipanteAdminResponseDto>> listarParticipantes() {
        Set<Long> noSorteio = sorteioService.idsNoSorteio();
        List<ParticipanteAdminResponseDto> participantes = participanteService.listar().stream()
                .map(p -> RespostaMapper.toAdminDto(p, noSorteio.contains(p.getId())))
                .collect(Collectors.toList());
        return ResponseEntity.ok(participantes);
    }

    @Override
    public ResponseEntity<ParticipanteTokenResponseDto> cadastrarParticipante(ParticipanteRequestDto request) {
        ParticipantePortIn.CadastroResultado resultado = participanteService.cadastrarPeloOrganizador(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ParticipanteTokenResponseDto.builder()
                .participante(RespostaMapper.toDto(resultado.participante()))
                .token(resultado.token())
                .build());
    }

    @Override
    public ResponseEntity<ParticipanteResponseDto> atualizarParticipante(String id, ParticipanteRequestDto request) {
        return ResponseEntity.ok(RespostaMapper.toDto(participanteService.atualizar(IdParser.parse(id), request)));
    }

    @Override
    public ResponseEntity<Void> removerParticipante(String id) {
        participanteService.remover(IdParser.parse(id));
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<TokenResponseDto> gerarNovoLink(String id) {
        String token = participanteService.gerarNovoLink(IdParser.parse(id));
        return ResponseEntity.ok(TokenResponseDto.builder().token(token).build());
    }

    @Override
    public ResponseEntity<ConfiguracaoResponseDto> sortear() {
        return ResponseEntity.ok(toDto(sorteioService.sortear()));
    }

    @Override
    public ResponseEntity<ConfiguracaoResponseDto> reabrir() {
        return ResponseEntity.ok(toDto(sorteioService.reabrir()));
    }

    private ConfiguracaoResponseDto toDto(Configuracao configuracao) {
        return RespostaMapper.toDto(configuracao, configuracaoService.quantidadeNoSorteio());
    }
}
