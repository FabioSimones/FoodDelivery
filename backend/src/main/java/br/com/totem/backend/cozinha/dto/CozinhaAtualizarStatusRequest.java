package br.com.totem.backend.cozinha.dto;

import br.com.totem.backend.pedido.enums.StatusPedido;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CozinhaAtualizarStatusRequest(

        @NotNull(message = "O novo status do pedido é obrigatório.")
        StatusPedido statusPedido,

        @Size(max = 255, message = "A observação deve ter no máximo 255 caracteres.")
        String observacao
) {
}