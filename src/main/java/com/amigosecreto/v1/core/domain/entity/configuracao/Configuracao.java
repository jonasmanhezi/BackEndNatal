package com.amigosecreto.v1.core.domain.entity.configuracao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Configuracao {

    private String titulo;
    private LocalDate dataSorteio;
    private LocalDate dataTroca;
    private Integer valor;
    private StatusSorteio status;
    private Instant sorteadoEm;

    public enum StatusSorteio {
        OPEN,
        DRAWN
    }

    public boolean isSorteado() {
        return status == StatusSorteio.DRAWN;
    }
}
