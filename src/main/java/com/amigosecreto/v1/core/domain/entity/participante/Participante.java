package com.amigosecreto.v1.core.domain.entity.participante;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Participante {

    private Long id;
    private String nome;
    private String foto;
    private String calcado;
    private String calca;
    private String camisa;
    private String gostos;
    private String listaDesejos;
    private String evitar;
    private boolean manual;
    private String tokenHash;
    private Instant criadoEm;
    private Instant atualizadoEm;

    /** Tem calçado, calça e camisa preenchidos. */
    public boolean isCompleto() {
        return preenchido(nome) && preenchido(calcado) && preenchido(calca) && preenchido(camisa);
    }

    private static boolean preenchido(String valor) {
        return valor != null && !valor.isBlank();
    }
}
