package br.com.totem.backend.pedido.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record ItemPedidoCriacaoRequest(

        @NotNull(message = "O produto é obrigatório.")
        UUID produtoId,

        @NotNull(message = "A quantidade é obrigatória.")
        @Min(value = 1, message = "A quantidade deve ser maior que zero.")
        Integer quantidade,

        @Size(max = 255, message = "A observação deve ter no máximo 255 caracteres.")
        String observacao
) {
}
