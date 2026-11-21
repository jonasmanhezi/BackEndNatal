package com.amigosecreto.v1.core.port.in.dto.response.admin;

import com.amigosecreto.v1.core.port.in.dto.response.participante.ParticipanteResponseDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Cadastro completo + estado no sorteio + link pessoal. Nunca inclui o par. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParticipanteAdminResponseDto {

    @JsonUnwrapped
    private ParticipanteResponseDto participante;

    @JsonProperty("complete")
    private boolean completo;

    @JsonProperty("inDraw")
    private boolean noSorteio;

    /** Link pessoal para o organizador mandar pelo WhatsApp. Nulo em cadastros antigos. */
    @JsonProperty("token")
    private String token;
}
