package com.amigosecreto.v1.infrastructure.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tb_sorteio_membro")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SorteioMembroEntity {

    @Id
    @Column(name = "participante_id")
    private Long participanteId;
}
