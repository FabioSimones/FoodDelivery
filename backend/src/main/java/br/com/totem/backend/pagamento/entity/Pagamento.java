package br.com.totem.backend.pagamento.entity;

import br.com.totem.backend.pagamento.enums.FormaPagamento;
import br.com.totem.backend.pagamento.enums.StatusPagamento;
import br.com.totem.backend.pedido.entity.Pedido;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "pagamentos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pagamento {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;

    @Enumerated(EnumType.STRING)
    @Column(name = "forma_pagamento", nullable = false, length = 40)
    private FormaPagamento formaPagamento;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_pagamento", nullable = false, length = 40)
    private StatusPagamento statusPagamento;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    @Column(name = "payment_provider", nullable = false, length = 80)
    private String paymentProvider;

    @Column(name = "external_payment_id", length = 120)
    private String externalPaymentId;

    @Column(name = "qr_code_pix", columnDefinition = "TEXT")
    private String qrCodePix;

    @Column(name = "expira_em")
    private LocalDateTime expiraEm;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm;

    @Column(name = "pago_em")
    private LocalDateTime pagoEm;

    @Column(name = "cancelado_em")
    private LocalDateTime canceladoEm;

    @Column(length = 255)
    private String observacao;

    @Column(name = "motivo_cancelamento", length = 255)
    private String motivoCancelamento;

    @PrePersist
    public void prePersist() {
        this.id = UUID.randomUUID();

        if (this.criadoEm == null) {
            this.criadoEm = LocalDateTime.now();
        }
    }
}