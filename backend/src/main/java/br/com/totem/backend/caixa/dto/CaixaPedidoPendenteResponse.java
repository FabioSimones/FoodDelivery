package br.com.totem.backend.caixa.dto;

import br.com.totem.backend.pagamento.enums.FormaPagamento;
import br.com.totem.backend.pagamento.enums.StatusPagamento;
import br.com.totem.backend.pedido.enums.StatusPedido;
import br.com.totem.backend.pedido.enums.TipoConsumo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record CaixaPedidoPendenteResponse(
        UUID pedidoId,
        String numeroPedido,
        String clienteNome,
        TipoConsumo tipoConsumo,
        BigDecimal valorTotal,
        FormaPagamento formaPagamento,
        StatusPagamento statusPagamento,
        StatusPedido statusPedido,
        LocalDateTime criadoEm
) {
}