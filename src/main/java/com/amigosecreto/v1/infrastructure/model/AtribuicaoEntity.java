package com.amigosecreto.v1.infrastructure.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tb_atribuicao")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AtribuicaoEntity {

    @Id
    @Column(name = "giver_id")
    private Long giverId;

    /** Fica nulo se quem foi tirado sair depois do sorteio. */
    @Column(name = "receiver_id")
    private Long receiverId;
}
