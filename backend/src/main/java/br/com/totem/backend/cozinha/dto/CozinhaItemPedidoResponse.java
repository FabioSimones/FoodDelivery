package br.com.totem.backend.cozinha.dto;

import java.util.UUID;

public record CozinhaItemPedidoResponse(
        UUID produtoId,
        String nomeProduto,
        Integer quantidade,
        String observacao
) {
}
