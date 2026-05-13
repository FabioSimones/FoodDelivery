package br.com.totem.backend.pedido.historico.dto;

import br.com.totem.backend.pedido.enums.StatusPedido;

import java.time.LocalDateTime;
import java.util.UUID;

public record HistoricoStatusPedidoResponse(
        UUID id,
        UUID pedidoId,
        String numeroPedido,
        StatusPedido statusAnterior,
        StatusPedido statusNovo,
        String origem,
        String observacao,
        LocalDateTime dataAlteracao
) {
}