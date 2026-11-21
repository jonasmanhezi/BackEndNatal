package com.amigosecreto.v1.core.application.service.sorteio;

import com.amigosecreto.v1.core.domain.entity.configuracao.Configuracao;
import com.amigosecreto.v1.core.domain.entity.participante.Participante;
import com.amigosecreto.v1.core.port.in.SorteioPortIn.MeuAmigoSecreto;
import com.amigosecreto.v1.core.port.out.configuracao.ConfiguracaoRepositoryPortOut;
import com.amigosecreto.v1.core.port.out.participante.ParticipanteRepositoryPortOut;
import com.amigosecreto.v1.core.port.out.sorteio.SorteioRepositoryPortOut;
import com.amigosecreto.v1.shared.cripto.ParSecreto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SorteioServiceTest {

    private ConfiguracaoRepositoryPortOut configuracaoRepository;
    private ParticipanteRepositoryPortOut participanteRepository;
    private SorteioRepositoryPortOut sorteioRepository;
    private SorteioService service;

    @BeforeEach
    void setUp() {
        configuracaoRepository = mock(ConfiguracaoRepositoryPortOut.class);
        participanteRepository = mock(ParticipanteRepositoryPortOut.class);
        sorteioRepository = mock(SorteioRepositoryPortOut.class);
        service = new SorteioService(configuracaoRepository, participanteRepository, sorteioRepository);
        when(configuracaoRepository.salvar(any())).thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    void sortear_gravaParesCifradosQueSoOProprioTokenAbre() {
        List<Participante> pessoas = List.of(pessoa(1L), pessoa(2L), pessoa(3L), pessoa(4L));
        when(configuracaoRepository.buscarComBloqueio()).thenReturn(configuracao(Configuracao.StatusSorteio.OPEN));
        when(participanteRepository.findAll()).thenReturn(pessoas);

        Configuracao resultado = service.sortear();

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<Long, String>> pares = ArgumentCaptor.forClass(Map.class);
        verify(sorteioRepository).substituirSorteio(pares.capture());
        assertEquals(4, pares.getValue().size());
        pares.getValue().forEach((giverId, cifrado) -> {
            Optional<Long> receiver = ParSecreto.decifrar(token(giverId), giverId, cifrado);
            assertTrue(receiver.isPresent());
            assertTrue(!receiver.get().equals(giverId));
            assertTrue(ParSecreto.decifrar(token(giverId + 1), giverId, cifrado).isEmpty());
        });
        assertEquals(Configuracao.StatusSorteio.DRAWN, resultado.getStatus());
        assertNotNull(resultado.getSorteadoEm());
    }

    @Test
    void sortear_alguemSemLinkSalvo_pedeLinkNovo() {
        Participante semLink = pessoa(3L);
        semLink.setToken(null);
        semLink.setNome("Tio Beto");
        when(configuracaoRepository.buscarComBloqueio()).thenReturn(configuracao(Configuracao.StatusSorteio.OPEN));
        when(participanteRepository.findAll()).thenReturn(List.of(pessoa(1L), pessoa(2L), semLink));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> service.sortear());
        assertTrue(ex.getMessage().contains("Tio Beto"));
        verify(sorteioRepository, never()).substituirSorteio(any());
    }

    @Test
    void sortear_comDuasPessoas_naoGravaNada() {
        when(configuracaoRepository.buscarComBloqueio()).thenReturn(configuracao(Configuracao.StatusSorteio.OPEN));
        when(participanteRepository.findAll()).thenReturn(List.of(pessoa(1L), pessoa(2L)));

        assertThrows(IllegalStateException.class, () -> service.sortear());
        verify(sorteioRepository, never()).substituirSorteio(any());
        verify(configuracaoRepository, never()).salvar(any());
    }

    @Test
    void reabrir_apagaParesEVoltaParaOpen() {
        Configuracao sorteada = configuracao(Configuracao.StatusSorteio.DRAWN);
        sorteada.setSorteadoEm(Instant.now());
        when(configuracaoRepository.buscarComBloqueio()).thenReturn(sorteada);

        Configuracao resultado = service.reabrir();

        verify(sorteioRepository).apagarSorteio();
        assertEquals(Configuracao.StatusSorteio.OPEN, resultado.getStatus());
        assertNull(resultado.getSorteadoEm());
    }

    @Test
    void meuAmigoSecreto_antesDoSorteio_notDrawn() {
        when(configuracaoRepository.buscar()).thenReturn(configuracao(Configuracao.StatusSorteio.OPEN));

        MeuAmigoSecreto resultado = service.buscarMeuAmigoSecreto(1L, token(1L));

        assertNull(resultado.amigoSecreto());
        assertEquals(MeuAmigoSecreto.NAO_SORTEADO, resultado.motivo());
    }

    @Test
    void meuAmigoSecreto_entrouDepois_joinedAfterDraw() {
        when(configuracaoRepository.buscar()).thenReturn(configuracao(Configuracao.StatusSorteio.DRAWN));
        when(sorteioRepository.isMembro(9L)).thenReturn(false);
        when(sorteioRepository.findReceiverCifrado(9L)).thenReturn(Optional.empty());

        assertEquals(MeuAmigoSecreto.ENTROU_DEPOIS, service.buscarMeuAmigoSecreto(9L, token(9L)).motivo());
    }

    @Test
    void meuAmigoSecreto_amigoSaiu_receiverLeft() {
        when(configuracaoRepository.buscar()).thenReturn(configuracao(Configuracao.StatusSorteio.DRAWN));
        when(sorteioRepository.isMembro(1L)).thenReturn(true);
        when(sorteioRepository.findReceiverCifrado(1L)).thenReturn(Optional.of(ParSecreto.cifrar(token(1L), 1L, 2L)));
        when(participanteRepository.findById(2L)).thenReturn(Optional.empty());

        assertEquals(MeuAmigoSecreto.AMIGO_SAIU, service.buscarMeuAmigoSecreto(1L, token(1L)).motivo());
    }

    @Test
    void meuAmigoSecreto_devolveQuemTirou() {
        Participante marina = Participante.builder().id(2L).nome("Marina").build();
        when(configuracaoRepository.buscar()).thenReturn(configuracao(Configuracao.StatusSorteio.DRAWN));
        when(sorteioRepository.isMembro(1L)).thenReturn(true);
        when(sorteioRepository.findReceiverCifrado(1L)).thenReturn(Optional.of(ParSecreto.cifrar(token(1L), 1L, 2L)));
        when(participanteRepository.findById(2L)).thenReturn(Optional.of(marina));

        MeuAmigoSecreto resultado = service.buscarMeuAmigoSecreto(1L, token(1L));

        assertEquals("Marina", resultado.amigoSecreto().getNome());
        assertNull(resultado.motivo());
    }

    private Participante pessoa(Long id) {
        return Participante.builder().id(id).nome("Pessoa " + id).token(token(id)).build();
    }

    private String token(Long id) {
        return "token-de-teste-da-pessoa-" + id;
    }

    private Configuracao configuracao(Configuracao.StatusSorteio status) {
        return Configuracao.builder().titulo("Natal 2026").status(status).build();
    }
}
