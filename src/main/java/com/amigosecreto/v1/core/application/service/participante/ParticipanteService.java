package com.amigosecreto.v1.core.application.service.participante;

import com.amigosecreto.v1.core.domain.entity.participante.Participante;
import com.amigosecreto.v1.core.port.in.ParticipantePortIn;
import com.amigosecreto.v1.core.port.in.dto.request.participante.ParticipanteRequestDto;
import com.amigosecreto.v1.core.port.out.configuracao.ConfiguracaoRepositoryPortOut;
import com.amigosecreto.v1.core.port.out.participante.ParticipanteRepositoryPortOut;
import com.amigosecreto.v1.shared.constant.Commons;
import com.amigosecreto.v1.shared.exception.ArquivoGrandeDemaisException;
import com.amigosecreto.v1.shared.exception.NaoEncontradoException;
import com.amigosecreto.v1.shared.token.TokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class ParticipanteService implements ParticipantePortIn {

    private static final Pattern FOTO_DATA_URL =
            Pattern.compile("^data:image/(jpeg|png|webp);base64,[A-Za-z0-9+/=]+$");

    private final ParticipanteRepositoryPortOut participanteRepositoryPortOut;
    private final ConfiguracaoRepositoryPortOut configuracaoRepositoryPortOut;

    @Override
    @Transactional(readOnly = true)
    public List<Participante> listar() {
        return participanteRepositoryPortOut.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Participante buscarPorId(Long id) {
        return participanteRepositoryPortOut.findById(id)
                .orElseThrow(() -> new NaoEncontradoException(Commons.MSG_PESSOA_NAO_ENCONTRADA));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Long> buscarIdPorToken(String token) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }
        return participanteRepositoryPortOut.findIdByTokenHash(TokenUtil.hash(token.trim()));
    }

    @Override
    @Transactional
    public CadastroResultado cadastrar(ParticipanteRequestDto request) {
        if (configuracaoRepositoryPortOut.buscar().isSorteado()) {
            throw new IllegalStateException(Commons.MSG_SORTEIO_JA_FEITO);
        }
        return criar(request, false);
    }

    @Override
    @Transactional
    public CadastroResultado cadastrarPeloOrganizador(ParticipanteRequestDto request) {
        return criar(request, true);
    }

    @Override
    @Transactional
    public Participante atualizar(Long id, ParticipanteRequestDto request) {
        Participante participante = buscarPorId(id);
        preencher(participante, request);
        participante.setAtualizadoEm(Instant.now());
        return participanteRepositoryPortOut.save(participante);
    }

    @Override
    @Transactional
    public void sair(Long id) {
        if (configuracaoRepositoryPortOut.buscar().isSorteado()) {
            throw new IllegalStateException(Commons.MSG_SAIR_DEPOIS_DO_SORTEIO);
        }
        participanteRepositoryPortOut.deleteById(id);
    }

    @Override
    @Transactional
    public void remover(Long id) {
        if (participanteRepositoryPortOut.existsById(id)) {
            participanteRepositoryPortOut.deleteById(id);
        }
    }

    @Override
    @Transactional
    public String gerarNovoLink(Long id) {
        Participante participante = buscarPorId(id);
        String token = TokenUtil.gerarToken();
        participante.setTokenHash(TokenUtil.hash(token));
        participante.setToken(token);
        participante.setAtualizadoEm(Instant.now());
        participanteRepositoryPortOut.save(participante);
        return token;
    }

    private CadastroResultado criar(ParticipanteRequestDto request, boolean manual) {
        String token = TokenUtil.gerarToken();
        Instant agora = Instant.now();

        Participante participante = Participante.builder()
                .manual(manual)
                .tokenHash(TokenUtil.hash(token))
                .token(token)
                .criadoEm(agora)
                .atualizadoEm(agora)
                .build();
        preencher(participante, request);

        return new CadastroResultado(participanteRepositoryPortOut.save(participante), token);
    }

    private void preencher(Participante participante, ParticipanteRequestDto request) {
        String nome = texto(request.getNome());
        if (nome.isEmpty()) {
            throw new IllegalArgumentException("Coloque um nome.");
        }
        participante.setNome(nome);
        participante.setFoto(validarFoto(request.getFoto()));
        participante.setCalcado(texto(request.getCalcado()));
        participante.setCalca(texto(request.getCalca()));
        participante.setCamisa(texto(request.getCamisa()));
        participante.setGostos(texto(request.getGostos()));
        participante.setListaDesejos(texto(request.getListaDesejos()));
        participante.setEvitar(texto(request.getEvitar()));
    }

    static String validarFoto(String foto) {
        if (foto == null || foto.isBlank()) {
            return null;
        }
        if (foto.length() > Commons.FOTO_MAX_CARACTERES) {
            throw new ArquivoGrandeDemaisException(Commons.MSG_FOTO_GRANDE);
        }
        if (!FOTO_DATA_URL.matcher(foto).matches()) {
            throw new IllegalArgumentException(Commons.MSG_FOTO_INVALIDA);
        }
        return foto;
    }

    private static String texto(String valor) {
        return valor == null ? "" : valor.trim();
    }
}
