package com.amigosecreto.v1.core.port.in.dto.request.configuracao;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfiguracaoUpdateRequestDto {

    @JsonProperty("title")
    @Size(max = 50, message = "O nome do grupo pode ter até 50 letras.")
    private String titulo;

    /** AAAA-MM-DD ou vazio. */
    @JsonProperty("drawDate")
    private String dataSorteio;

    /** AAAA-MM-DD ou vazio. */
    @JsonProperty("exchangeDate")
    private String dataTroca;

    @JsonProperty("budget")
    @Min(value = 0, message = "O valor não pode ser negativo.")
    private Integer valor;
}
