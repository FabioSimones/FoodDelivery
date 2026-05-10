package br.com.totem.backend.caixa.dto;

import br.com.totem.backend.pagamento.enums.FormaPagamento;
import br.com.totem.backend.pagamento.enums.StatusPagamento;
import br.com.totem.backend.pedido.enums.StatusPedido;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record CaixaOperacaoPedidoResponse(
        UUID pedidoId,
        String numeroPedido,
        UUID pagamentoId,
        FormaPagamento formaPagamento,
        StatusPagamento statusPagamento,
        StatusPedido statusPedido,
        BigDecimal valorTotal,
        BigDecimal valorRecebido,
        BigDecimal troco,
        String observacao,
        String motivoCancelamento,
        LocalDateTime pagoEm,
        LocalDateTime canceladoEm
) {
}