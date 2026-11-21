package com.amigosecreto.v1.core.port.in.dto.response.participante;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Resposta do cadastro. O token aparece uma única vez. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParticipanteTokenResponseDto {

    @JsonProperty("participant")
    private ParticipanteResponseDto participante;

    @JsonProperty("token")
    private String token;
}
