package com.amigosecreto.v1.core.port.in.dto.response.participante;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Quem eu tirei: mesmos campos do cadastro, sem metadados. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AmigoSecretoResponseDto {

    @JsonProperty("id")
    private String id;

    @JsonProperty("name")
    private String nome;

    @JsonProperty("photo")
    private String foto;

    @JsonProperty("shoe")
    private String calcado;

    @JsonProperty("pants")
    private String calca;

    @JsonProperty("shirt")
    private String camisa;

    @JsonProperty("likes")
    private String gostos;

    @JsonProperty("wishlist")
    private String listaDesejos;

    @JsonProperty("avoid")
    private String evitar;
}
