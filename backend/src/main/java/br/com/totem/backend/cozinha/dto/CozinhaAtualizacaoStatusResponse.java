package br.com.totem.backend.cozinha.dto;

import br.com.totem.backend.pedido.enums.StatusPedido;

import java.time.LocalDateTime;
import java.util.UUID;

public record CozinhaAtualizacaoStatusResponse(
        UUID pedidoId,
        String numeroPedido,
        StatusPedido statusAnterior,
        StatusPedido statusNovo,
        String observacao,
        LocalDateTime atualizadoEm
) {
}