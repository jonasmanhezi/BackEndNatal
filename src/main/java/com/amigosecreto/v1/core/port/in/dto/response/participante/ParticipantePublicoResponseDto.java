package com.amigosecreto.v1.core.port.in.dto.response.participante;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** O que todo mundo vê na aba Pessoas. Sem tamanhos. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParticipantePublicoResponseDto {

    @JsonProperty("id")
    private String id;

    @JsonProperty("name")
    private String nome;

    @JsonProperty("photo")
    private String foto;

    @JsonProperty("complete")
    private boolean completo;
}
