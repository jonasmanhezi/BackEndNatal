package com.amigosecreto.v1.core.port.in.dto.request.participante;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParticipanteRequestDto {

    @JsonProperty("name")
    @NotBlank(message = "Coloque um nome.")
    @Size(max = 40, message = "O nome pode ter até 40 letras.")
    private String nome;

    @JsonProperty("photo")
    private String foto;

    @JsonProperty("shoe")
    @Size(max = 16, message = "O calçado pode ter até 16 caracteres.")
    private String calcado;

    @JsonProperty("pants")
    @Size(max = 16, message = "A calça pode ter até 16 caracteres.")
    private String calca;

    @JsonProperty("shirt")
    @Size(max = 16, message = "A camisa pode ter até 16 caracteres.")
    private String camisa;

    @JsonProperty("likes")
    @Size(max = 300, message = "Os gostos podem ter até 300 caracteres.")
    private String gostos;

    @JsonProperty("wishlist")
    @Size(max = 500, message = "A lista de desejos pode ter até 500 caracteres.")
    private String listaDesejos;

    @JsonProperty("avoid")
    @Size(max = 160, message = "O campo \"prefere não ganhar\" pode ter até 160 caracteres.")
    private String evitar;
}
