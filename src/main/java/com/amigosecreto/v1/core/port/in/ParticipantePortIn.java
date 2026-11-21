package com.amigosecreto.v1.core.port.in;

import com.amigosecreto.v1.core.domain.entity.participante.Participante;
import com.amigosecreto.v1.core.port.in.dto.request.participante.ParticipanteRequestDto;

import java.util.List;
import java.util.Optional;

public interface ParticipantePortIn {

    List<Participante> listar();

    Participante buscarPorId(Long id);

    Optional<Long> buscarIdPorToken(String token);

    /** Cadastro próprio. Devolve o participante e o token em texto (mostrado uma vez). */
    CadastroResultado cadastrar(ParticipanteRequestDto request);

    /** Cadastro feito pelo organizador (manual = true). Permitido mesmo depois do sorteio. */
    CadastroResultado cadastrarPeloOrganizador(ParticipanteRequestDto request);

    Participante atualizar(Long id, ParticipanteRequestDto request);

    /** A própria pessoa saindo. Bloqueado depois do sorteio. */
    void sair(Long id);

    /** O organizador removendo alguém. Permitido a qualquer momento. */
    void remover(Long id);

    String gerarNovoLink(Long id);

    record CadastroResultado(Participante participante, String token) {
    }
}
