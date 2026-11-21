package com.amigosecreto.v1.core.application.service.participante;

import com.amigosecreto.v1.core.domain.entity.configuracao.Configuracao;
import com.amigosecreto.v1.core.domain.entity.participante.Participante;
import com.amigosecreto.v1.core.port.in.ParticipantePortIn.CadastroResultado;
import com.amigosecreto.v1.core.port.in.dto.request.participante.ParticipanteRequestDto;
import com.amigosecreto.v1.core.port.out.configuracao.ConfiguracaoRepositoryPortOut;
import com.amigosecreto.v1.core.port.out.participante.ParticipanteRepositoryPortOut;
import com.amigosecreto.v1.core.port.out.sorteio.SorteioRepositoryPortOut;
import com.amigosecreto.v1.shared.cripto.ParSecreto;
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
    private SorteioRepositoryPortOut sorteioRepository;
    private ParticipanteService service;

    @BeforeEach
    void setUp() {
        participanteRepository = mock(ParticipanteRepositoryPortOut.class);
        configuracaoRepository = mock(ConfiguracaoRepositoryPortOut.class);
        sorteioRepository = mock(SorteioRepositoryPortOut.class);
        when(sorteioRepository.findReceiverCifrado(any())).thenReturn(java.util.Optional.empty());
        service = new ParticipanteService(participanteRepository, configuracaoRepository, sorteioRepository);
        when(participanteRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(configuracaoRepository.buscarComBloqueio()).thenReturn(configuracao(Configuracao.StatusSorteio.OPEN));
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
        when(configuracaoRepository.buscarComBloqueio()).thenReturn(configuracao(Configuracao.StatusSorteio.DRAWN));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> service.cadastrar(request("Tio Beto")));
        assertEquals(Commons.MSG_SORTEIO_JA_FEITO, ex.getMessage());
    }

    @Test
    void cadastrarPeloOrganizador_depoisDoSorteio_permitido() {
        when(configuracaoRepository.buscarComBloqueio()).thenReturn(configuracao(Configuracao.StatusSorteio.DRAWN));

        assertTrue(service.cadastrarPeloOrganizador(request("Tio Beto")).participante().isManual());
    }

    @Test
    void sair_depoisDoSorteio_conflito() {
        when(configuracaoRepository.buscarComBloqueio()).thenReturn(configuracao(Configuracao.StatusSorteio.DRAWN));

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
    void gerarNovoLink_depoisDoSorteio_recifraOParComONovoLink() {
        Participante celia = service.cadastrarPeloOrganizador(request("Dona Célia")).participante();
        String tokenAntigo = celia.getToken();
        celia.setId(7L);
        when(participanteRepository.findById(7L)).thenReturn(java.util.Optional.of(celia));
        when(sorteioRepository.findReceiverCifrado(7L))
                .thenReturn(java.util.Optional.of(ParSecreto.cifrar(tokenAntigo, 7L, 3L)));

        String novo = service.gerarNovoLink(7L);

        org.mockito.ArgumentCaptor<String> recifrado = org.mockito.ArgumentCaptor.forClass(String.class);
        verify(sorteioRepository).atualizarReceiverCifrado(org.mockito.ArgumentMatchers.eq(7L), recifrado.capture());
        assertEquals(java.util.Optional.of(3L), ParSecreto.decifrar(novo, 7L, recifrado.getValue()));
        assertTrue(ParSecreto.decifrar(tokenAntigo, 7L, recifrado.getValue()).isEmpty());
    }

    @Test
    void cadastrar_nomeRepetido_mesmoComAcentoEMaiusculas_conflito() {
        when(participanteRepository.findAll()).thenReturn(java.util.List.of(
                Participante.builder().id(1L).nome("Dona Célia").build()));

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> service.cadastrar(request("  dona   CELIA ")));
        assertTrue(ex.getMessage().contains("Já tem alguém chamado"));
        verify(participanteRepository, never()).save(any());
    }

    @Test
    void cadastrarPeloOrganizador_nomeRepetido_conflito() {
        when(participanteRepository.findAll()).thenReturn(java.util.List.of(
                Participante.builder().id(1L).nome("Jonas").build()));

        assertThrows(IllegalStateException.class, () -> service.cadastrarPeloOrganizador(request("jonas")));
    }

    @Test
    void atualizar_mantendoOProprioNome_permitido_mudandoParaNomeDeOutro_conflito() {
        Participante jonas = Participante.builder().id(1L).nome("Jonas").build();
        Participante marina = Participante.builder().id(2L).nome("Marina").build();
        when(participanteRepository.findAll()).thenReturn(java.util.List.of(jonas, marina));
        when(participanteRepository.findById(1L)).thenReturn(java.util.Optional.of(jonas));

        assertEquals("Jonas", service.atualizar(1L, request("Jonas")).getNome());
        assertThrows(IllegalStateException.class, () -> service.atualizar(1L, request("MARINA")));
    }

    @Test
    void gerarNovoLink_esperaOSorteioTerminar() {
        Participante celia = Participante.builder().id(7L).nome("Dona Célia").token("t").build();
        when(participanteRepository.findById(7L)).thenReturn(java.util.Optional.of(celia));

        service.gerarNovoLink(7L);

        org.mockito.InOrder ordem = org.mockito.Mockito.inOrder(configuracaoRepository, sorteioRepository);
        ordem.verify(configuracaoRepository).buscarComBloqueio();
        ordem.verify(sorteioRepository).findReceiverCifrado(7L);
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
