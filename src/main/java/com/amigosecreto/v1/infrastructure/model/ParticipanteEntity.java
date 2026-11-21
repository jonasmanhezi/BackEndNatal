package com.amigosecreto.v1.infrastructure.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Entity
@Table(name = "tb_participante")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParticipanteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(name = "foto", columnDefinition = "TEXT")
    private String foto;

    @Column(name = "calcado", nullable = false)
    private String calcado;

    @Column(name = "calca", nullable = false)
    private String calca;

    @Column(name = "camisa", nullable = false)
    private String camisa;

    @Column(name = "gostos", nullable = false)
    private String gostos;

    @Column(name = "lista_desejos", nullable = false)
    private String listaDesejos;

    @Column(name = "evitar", nullable = false)
    private String evitar;

    @Column(name = "manual", nullable = false)
    private boolean manual;

    @Column(name = "token_hash", nullable = false, unique = true)
    private String tokenHash;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}
