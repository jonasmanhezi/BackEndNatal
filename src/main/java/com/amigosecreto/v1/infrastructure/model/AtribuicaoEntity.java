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

    /** Id de quem foi tirado, cifrado com o token pessoal de quem deu. */
    @Column(name = "receiver_cifrado", nullable = false, columnDefinition = "TEXT")
    private String receiverCifrado;
}
