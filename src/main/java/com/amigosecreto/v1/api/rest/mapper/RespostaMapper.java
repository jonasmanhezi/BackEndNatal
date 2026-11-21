package com.amigosecreto.v1.api.rest.mapper;

import com.amigosecreto.v1.core.domain.entity.configuracao.Configuracao;
import com.amigosecreto.v1.core.domain.entity.participante.Participante;
import com.amigosecreto.v1.core.port.in.dto.response.admin.ParticipanteAdminResponseDto;
import com.amigosecreto.v1.core.port.in.dto.response.configuracao.ConfiguracaoResponseDto;
import com.amigosecreto.v1.core.port.in.dto.response.participante.AmigoSecretoResponseDto;
import com.amigosecreto.v1.core.port.in.dto.response.participante.ParticipantePublicoResponseDto;
import com.amigosecreto.v1.core.port.in.dto.response.participante.ParticipanteResponseDto;

import java.time.Instant;
import java.time.LocalDate;

/** Domínio -> DTO. Nada daqui expõe token_hash nem os pares do sorteio. */
public final class RespostaMapper {

    private RespostaMapper() {
    }

    public static ConfiguracaoResponseDto toDto(Configuracao configuracao, long quantidadeNoSorteio) {
        return ConfiguracaoResponseDto.builder()
                .titulo(configuracao.getTitulo())
                .dataSorteio(data(configuracao.getDataSorteio()))
                .dataTroca(data(configuracao.getDataTroca()))
                .valor(configuracao.getValor())
                .status(configuracao.getStatus().name().toLowerCase())
                .sorteadoEm(millis(configuracao.getSorteadoEm()))
                .quantidadeNoSorteio(quantidadeNoSorteio)
                .recursos(ConfiguracaoResponseDto.FeaturesDto.builder().ideiasComIa(false).build())
                .build();
    }

    public static ParticipanteResponseDto toDto(Participante participante) {
        return ParticipanteResponseDto.builder()
                .id(String.valueOf(participante.getId()))
                .nome(participante.getNome())
                .foto(participante.getFoto())
                .calcado(participante.getCalcado())
                .calca(participante.getCalca())
                .camisa(participante.getCamisa())
                .gostos(participante.getGostos())
                .listaDesejos(participante.getListaDesejos())
                .evitar(participante.getEvitar())
                .manual(participante.isManual())
                .criadoEm(millis(participante.getCriadoEm()))
                .atualizadoEm(millis(participante.getAtualizadoEm()))
                .build();
    }

    public static ParticipantePublicoResponseDto toPublicoDto(Participante participante) {
        return ParticipantePublicoResponseDto.builder()
                .id(String.valueOf(participante.getId()))
                .nome(participante.getNome())
                .foto(participante.getFoto())
                .completo(participante.isCompleto())
                .build();
    }

    public static ParticipanteAdminResponseDto toAdminDto(Participante participante, boolean noSorteio) {
        return ParticipanteAdminResponseDto.builder()
                .participante(toDto(participante))
                .completo(participante.isCompleto())
                .noSorteio(noSorteio)
                .build();
    }

    public static AmigoSecretoResponseDto toAmigoSecretoDto(Participante participante) {
        return AmigoSecretoResponseDto.builder()
                .id(String.valueOf(participante.getId()))
                .nome(participante.getNome())
                .foto(participante.getFoto())
                .calcado(participante.getCalcado())
                .calca(participante.getCalca())
                .camisa(participante.getCamisa())
                .gostos(participante.getGostos())
                .listaDesejos(participante.getListaDesejos())
                .evitar(participante.getEvitar())
                .build();
    }

    private static String data(LocalDate data) {
        return data == null ? "" : data.toString();
    }

    private static Long millis(Instant instant) {
        return instant == null ? null : instant.toEpochMilli();
    }
}
