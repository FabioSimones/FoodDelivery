package br.com.totem.backend.pedido.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ItemPedidoResponse(
        UUID id,
        UUID produtoId,
        String nomeProduto,
        Integer quantidade,
        BigDecimal precoUnitario,
        BigDecimal subtotal,
        String observacao
) {
}