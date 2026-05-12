package br.com.totem.backend.cozinha.dto;

import br.com.totem.backend.pedido.enums.StatusPedido;
import br.com.totem.backend.pedido.enums.TipoConsumo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record CozinhaPedidoResponse(
        UUID pedidoId,
        String numeroPedido,
        String clienteNome,
        TipoConsumo tipoConsumo,
        StatusPedido statusPedido,
        BigDecimal valorTotal,
        LocalDateTime criadoEm,
        List<CozinhaItemPedidoResponse> itens
) {
}