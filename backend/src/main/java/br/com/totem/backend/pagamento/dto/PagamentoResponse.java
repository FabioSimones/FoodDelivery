package br.com.totem.backend.pagamento.dto;


import br.com.totem.backend.pagamento.enums.FormaPagamento;
import br.com.totem.backend.pagamento.enums.StatusPagamento;
import br.com.totem.backend.pedido.enums.StatusPedido;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PagamentoResponse(
        UUID id,
        UUID pedidoId,
        String numeroPedido,
        FormaPagamento formaPagamento,
        StatusPagamento statusPagamento,
        BigDecimal valor,
        String paymentProvider,
        String externalPaymentId,
        String qrCodePix,
        LocalDateTime expiraEm,
        StatusPedido statusPedido,
        LocalDateTime criadoEm,
        LocalDateTime pagoEm,
        LocalDateTime canceladoEm
) {
}
