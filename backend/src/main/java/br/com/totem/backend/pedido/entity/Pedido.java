package br.com.totem.backend.pedido.entity;

import br.com.totem.backend.pedido.enums.StatusPedido;
import br.com.totem.backend.pedido.enums.TipoConsumo;
import br.com.totem.backend.restaurante.entity.Restaurante;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "pedidos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pedido {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "restaurante_id", nullable = false)
    private Restaurante restaurante;

    @Column(name = "numero_pedido", nullable = false, length = 30)
    private String numeroPedido;

    @Column(name = "cliente_nome", length = 100)
    private String clienteNome;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_consumo", nullable = false, length = 30)
    private TipoConsumo tipoConsumo;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_pedido", nullable = false, length = 50)
    private StatusPedido statusPedido;

    @Column(name = "valor_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorTotal;

    @Column(name = "dispositivo_origem_id")
    private UUID dispositivoOrigemId;

    @Builder.Default
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemPedido> itens = new ArrayList<>();

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm;

    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    @PrePersist
    public void prePersist() {
        this.id = UUID.randomUUID();

        if (this.statusPedido == null) {
            this.statusPedido = StatusPedido.CRIADO;
        }

        if (this.valorTotal == null) {
            this.valorTotal = BigDecimal.ZERO;
        }

        this.criadoEm = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.atualizadoEm = LocalDateTime.now();
    }

    public void adicionarItem(ItemPedido item) {
        item.setPedido(this);
        this.itens.add(item);
    }
}