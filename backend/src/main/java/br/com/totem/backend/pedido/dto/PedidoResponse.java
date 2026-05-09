package br.com.totem.backend.pedido.dto;

import br.com.totem.backend.pedido.enums.StatusPedido;
import br.com.totem.backend.pedido.enums.TipoConsumo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record PedidoResponse(
        UUID id,
        UUID restauranteId,
        String restauranteNome,
        String numeroPedido,
        String clienteNome,
        TipoConsumo tipoConsumo,
        StatusPedido statusPedido,
        BigDecimal valorTotal,
        List<ItemPedidoResponse> itens,
        LocalDateTime criadoEm,
        LocalDateTime atualizadoEm
) {
}