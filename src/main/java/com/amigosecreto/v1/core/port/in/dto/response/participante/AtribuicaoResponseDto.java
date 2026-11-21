package com.amigosecreto.v1.core.port.in.dto.response.participante;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AtribuicaoResponseDto {

    @JsonProperty("receiver")
    private AmigoSecretoResponseDto amigoSecreto;

    /** null, "not_drawn", "joined_after_draw" ou "receiver_left". */
    @JsonProperty("reason")
    private String motivo;
}
