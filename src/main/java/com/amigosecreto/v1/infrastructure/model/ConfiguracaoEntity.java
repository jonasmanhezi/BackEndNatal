package com.amigosecreto.v1.infrastructure.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "tb_configuracao")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfiguracaoEntity {

    public static final Short ID_UNICO = 1;

    @Id
    private Short id;

    @Column(name = "titulo", nullable = false)
    private String titulo;

    @Column(name = "data_sorteio")
    private LocalDate dataSorteio;

    @Column(name = "data_troca")
    private LocalDate dataTroca;

    @Column(name = "valor")
    private Integer valor;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "sorteado_em")
    private OffsetDateTime sorteadoEm;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}
