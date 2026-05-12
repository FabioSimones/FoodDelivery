package br.com.totem.backend.pedido.historico.entity;

import br.com.totem.backend.pedido.entity.Pedido;
import br.com.totem.backend.pedido.enums.StatusPedido;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "historico_status_pedido")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistoricoStatusPedido {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_anterior", length = 50)
    private StatusPedido statusAnterior;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_novo", nullable = false, length = 50)
    private StatusPedido statusNovo;

    @Column(name = "data_alteracao", nullable = false)
    private LocalDateTime dataAlteracao;

    @Column(name = "alterado_por_usuario_id")
    private UUID alteradoPorUsuarioId;

    @Column(name = "alterado_por_dispositivo_id")
    private UUID alteradoPorDispositivoId;

    @Column(length = 50)
    private String origem;

    @Column(length = 255)
    private String observacao;

    @PrePersist
    public void prePersist() {
        this.id = UUID.randomUUID();

        if (this.dataAlteracao == null) {
            this.dataAlteracao = LocalDateTime.now();
        }
    }
}
