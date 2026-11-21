package com.amigosecreto.v1.core.application.service.configuracao;

import com.amigosecreto.v1.core.domain.entity.configuracao.Configuracao;
import com.amigosecreto.v1.core.port.in.ConfiguracaoPortIn;
import com.amigosecreto.v1.core.port.in.dto.request.configuracao.ConfiguracaoUpdateRequestDto;
import com.amigosecreto.v1.core.port.out.configuracao.ConfiguracaoRepositoryPortOut;
import com.amigosecreto.v1.core.port.out.sorteio.SorteioRepositoryPortOut;
import com.amigosecreto.v1.shared.constant.Commons;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@Service
@RequiredArgsConstructor
public class ConfiguracaoService implements ConfiguracaoPortIn {

    static final String TITULO_PADRAO = "Natal 2026";

    private final ConfiguracaoRepositoryPortOut configuracaoRepositoryPortOut;
    private final SorteioRepositoryPortOut sorteioRepositoryPortOut;

    @Override
    @Transactional(readOnly = true)
    public Configuracao buscar() {
        return configuracaoRepositoryPortOut.buscar();
    }

    @Override
    @Transactional
    public Configuracao atualizar(ConfiguracaoUpdateRequestDto request) {
        Configuracao configuracao = configuracaoRepositoryPortOut.buscarComBloqueio();

        String titulo = request.getTitulo() == null ? "" : request.getTitulo().trim();
        configuracao.setTitulo(titulo.isEmpty() ? TITULO_PADRAO : titulo);
        configuracao.setDataSorteio(parseData(request.getDataSorteio()));
        configuracao.setDataTroca(parseData(request.getDataTroca()));
        configuracao.setValor(request.getValor());

        return configuracaoRepositoryPortOut.salvar(configuracao);
    }

    @Override
    @Transactional(readOnly = true)
    public long quantidadeNoSorteio() {
        return sorteioRepositoryPortOut.countMembros();
    }

    static LocalDate parseData(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(valor.trim());
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(Commons.MSG_DATA_INVALIDA);
        }
    }
}
