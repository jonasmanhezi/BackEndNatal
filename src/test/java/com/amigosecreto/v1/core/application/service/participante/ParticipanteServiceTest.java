package com.amigosecreto.v1.core.application.service.participante;

import com.amigosecreto.v1.core.domain.entity.configuracao.Configuracao;
import com.amigosecreto.v1.core.domain.entity.participante.Participante;
import com.amigosecreto.v1.core.port.in.ParticipantePortIn.CadastroResultado;
import com.amigosecreto.v1.core.port.in.dto.request.participante.ParticipanteRequestDto;
import com.amigosecreto.v1.core.port.out.configuracao.ConfiguracaoRepositoryPortOut;
import com.amigosecreto.v1.core.port.out.participante.ParticipanteRepositoryPortOut;
import com.amigosecreto.v1.shared.constant.Commons;
import com.amigosecreto.v1.shared.exception.ArquivoGrandeDemaisException;
import com.amigosecreto.v1.shared.token.TokenUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ParticipanteServiceTest {

    private ParticipanteRepositoryPortOut participanteRepository;
    private ConfiguracaoRepositoryPortOut configuracaoRepository;
    private ParticipanteService service;

    @BeforeEach
    void setUp() {
        participanteRepository = mock(ParticipanteRepositoryPortOut.class);
        configuracaoRepository = mock(ConfiguracaoRepositoryPortOut.class);
        service = new ParticipanteService(participanteRepository, configuracaoRepository);
        when(participanteRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(configuracaoRepository.buscar()).thenReturn(configuracao(Configuracao.StatusSorteio.OPEN));
    }

    @Test
    void cadastrar_guardaSoOHashDoToken() {
        CadastroResultado resultado = service.cadastrar(request("  Dona Célia  "));

        Participante salvo = resultado.participante();
        assertEquals("Dona Célia", salvo.getNome());
        assertEquals(TokenUtil.hash(resultado.token()), salvo.getTokenHash());
        assertEquals(resultado.token(), salvo.getToken());
        assertFalse(salvo.isManual());
        assertEquals("", salvo.getCalcado());
    }

    @Test
    void cadastrar_depoisDoSorteio_conflito() {
        when(configuracaoRepository.buscar()).thenReturn(configuracao(Configuracao.StatusSorteio.DRAWN));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> service.cadastrar(request("Tio Beto")));
        assertEquals(Commons.MSG_SORTEIO_JA_FEITO, ex.getMessage());
    }

    @Test
    void cadastrarPeloOrganizador_depoisDoSorteio_permitido() {
        when(configuracaoRepository.buscar()).thenReturn(configuracao(Configuracao.StatusSorteio.DRAWN));

        assertTrue(service.cadastrarPeloOrganizador(request("Tio Beto")).participante().isManual());
    }

    @Test
    void sair_depoisDoSorteio_conflito() {
        when(configuracaoRepository.buscar()).thenReturn(configuracao(Configuracao.StatusSorteio.DRAWN));

        assertThrows(IllegalStateException.class, () -> service.sair(1L));
        verify(participanteRepository, never()).deleteById(any());
    }

    @Test
    void gerarNovoLink_trocaTokenEHash() {
        Participante antigo = service.cadastrarPeloOrganizador(request("Dona Célia")).participante();
        String tokenAntigo = antigo.getToken();
        antigo.setId(7L);
        when(participanteRepository.findById(7L)).thenReturn(java.util.Optional.of(antigo));

        String novo = service.gerarNovoLink(7L);

        assertFalse(novo.equals(tokenAntigo));
        assertEquals(novo, antigo.getToken());
        assertEquals(TokenUtil.hash(novo), antigo.getTokenHash());
    }

    @Test
    void validarFoto_aceitaJpegBase64() {
        String foto = "data:image/jpeg;base64,/9j/4AAQSkZJRg==";
        assertEquals(foto, ParticipanteService.validarFoto(foto));
        assertNull(ParticipanteService.validarFoto("  "));
    }

    @Test
    void validarFoto_recusaOutroFormato() {
        assertThrows(IllegalArgumentException.class,
                () -> ParticipanteService.validarFoto("data:image/svg+xml;base64,PHN2Zz4="));
        assertThrows(IllegalArgumentException.class,
                () -> ParticipanteService.validarFoto("https://exemplo.com/foto.jpg"));
    }

    @Test
    void validarFoto_grandeDemais() {
        String foto = "data:image/jpeg;base64," + "A".repeat(Commons.FOTO_MAX_CARACTERES);
        assertThrows(ArquivoGrandeDemaisException.class, () -> ParticipanteService.validarFoto(foto));
    }

    private ParticipanteRequestDto request(String nome) {
        return ParticipanteRequestDto.builder().nome(nome).build();
    }

    private Configuracao configuracao(Configuracao.StatusSorteio status) {
        return Configuracao.builder().titulo("Natal 2026").status(status).build();
    }
}
