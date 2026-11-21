package com.amigosecreto.v1.core.application.service.participante;

import com.amigosecreto.v1.core.domain.entity.participante.Participante;
import com.amigosecreto.v1.core.port.in.ParticipantePortIn;
import com.amigosecreto.v1.core.port.in.dto.request.participante.ParticipanteRequestDto;
import com.amigosecreto.v1.core.port.out.configuracao.ConfiguracaoRepositoryPortOut;
import com.amigosecreto.v1.core.port.out.participante.ParticipanteRepositoryPortOut;
import com.amigosecreto.v1.core.port.out.sorteio.SorteioRepositoryPortOut;
import com.amigosecreto.v1.shared.constant.Commons;
import com.amigosecreto.v1.shared.cripto.ParSecreto;
import com.amigosecreto.v1.shared.exception.ArquivoGrandeDemaisException;
import com.amigosecreto.v1.shared.exception.NaoEncontradoException;
import com.amigosecreto.v1.shared.participante.NomeUtil;
import com.amigosecreto.v1.shared.token.TokenUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class ParticipanteService implements ParticipantePortIn {

    private static final Pattern FOTO_DATA_URL =
            Pattern.compile("^data:image/(jpeg|png|webp);base64,[A-Za-z0-9+/=]+$");

    private final ParticipanteRepositoryPortOut participanteRepositoryPortOut;
    private final ConfiguracaoRepositoryPortOut configuracaoRepositoryPortOut;
    private final SorteioRepositoryPortOut sorteioRepositoryPortOut;

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
        if (configuracaoRepositoryPortOut.buscarComBloqueio().isSorteado()) {
            throw new IllegalStateException(Commons.MSG_SORTEIO_JA_FEITO);
        }
        return criar(request, false);
    }

    @Override
    @Transactional
    public CadastroResultado cadastrarPeloOrganizador(ParticipanteRequestDto request) {
        configuracaoRepositoryPortOut.buscarComBloqueio();
        return criar(request, true);
    }

    @Override
    @Transactional
    public Participante atualizar(Long id, ParticipanteRequestDto request) {
        configuracaoRepositoryPortOut.buscarComBloqueio();
        Participante participante = buscarPorId(id);
        preencher(participante, request);
        validarNomeUnico(participante.getNome(), id);
        participante.setAtualizadoEm(Instant.now());
        return participanteRepositoryPortOut.save(participante);
    }

    @Override
    @Transactional
    public void sair(Long id) {
        if (configuracaoRepositoryPortOut.buscarComBloqueio().isSorteado()) {
            throw new IllegalStateException(Commons.MSG_SAIR_DEPOIS_DO_SORTEIO);
        }
        participanteRepositoryPortOut.deleteById(id);
    }

    @Override
    @Transactional
    public void remover(Long id) {
        configuracaoRepositoryPortOut.buscarComBloqueio();
        if (participanteRepositoryPortOut.existsById(id)) {
            participanteRepositoryPortOut.deleteById(id);
        }
    }

    @Override
    @Transactional
    public String gerarNovoLink(Long id) {
        // Espera um sorteio em andamento terminar: senão o par novo poderia ser sobrescrito pelo antigo.
        configuracaoRepositoryPortOut.buscarComBloqueio();
        Participante participante = buscarPorId(id);
        String tokenAntigo = participante.getToken();
        String token = TokenUtil.gerarToken();

        // O par foi cifrado com o link antigo: abre com ele e cifra de novo com o novo.
        sorteioRepositoryPortOut.findReceiverCifrado(id).ifPresent(cifrado ->
                ParSecreto.decifrar(tokenAntigo, id, cifrado).ifPresentOrElse(
                        receiverId -> sorteioRepositoryPortOut.atualizarReceiverCifrado(
                                id, ParSecreto.cifrar(token, id, receiverId)),
                        () -> log.warn("Link trocado sem o antigo salvo: o par da pessoa {} se perdeu. Refaça o sorteio.", id)));

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
        validarNomeUnico(participante.getNome(), null);

        return new CadastroResultado(participanteRepositoryPortOut.save(participante), token);
    }

    /** A mesma pessoa cadastrada duas vezes viraria duas pessoas no sorteio (e poderia tirar a si mesma). */
    private void validarNomeUnico(String nome, Long idAtual) {
        String normalizado = NomeUtil.normalizar(nome);
        boolean repetido = participanteRepositoryPortOut.findAll().stream()
                .anyMatch(p -> !p.getId().equals(idAtual) && NomeUtil.normalizar(p.getNome()).equals(normalizado));
        if (repetido) {
            throw new IllegalStateException(String.format(Commons.MSG_NOME_REPETIDO, nome));
        }
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
