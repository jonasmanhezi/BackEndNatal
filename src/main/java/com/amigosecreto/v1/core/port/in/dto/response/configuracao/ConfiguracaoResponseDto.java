package com.amigosecreto.v1.core.port.in.dto.response.configuracao;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfiguracaoResponseDto {

    @JsonProperty("title")
    private String titulo;

    @JsonProperty("drawDate")
    private String dataSorteio;

    @JsonProperty("exchangeDate")
    private String dataTroca;

    @JsonProperty("budget")
    private Integer valor;

    /** "open" ou "drawn". */
    @JsonProperty("status")
    private String status;

    /** Epoch em milissegundos. */
    @JsonProperty("drawnAt")
    private Long sorteadoEm;

    @JsonProperty("drawCount")
    private long quantidadeNoSorteio;

    @JsonProperty("features")
    private FeaturesDto recursos;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FeaturesDto {

        @JsonProperty("aiIdeas")
        private boolean ideiasComIa;
    }
}
