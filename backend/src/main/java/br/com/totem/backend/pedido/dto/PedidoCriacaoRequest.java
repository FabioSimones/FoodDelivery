package br.com.totem.backend.pedido.dto;

import br.com.totem.backend.pedido.enums.TipoConsumo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

public record PedidoCriacaoRequest(

        @NotNull(message = "O restaurante é obrigatório.")
        UUID restauranteId,

        @Size(max = 100, message = "O nome do cliente deve ter no máximo 100 caracteres.")
        String clienteNome,

        @NotNull(message = "O tipo de consumo é obrigatório.")
        TipoConsumo tipoConsumo,

        @NotEmpty(message = "O pedido deve possuir pelo menos um item.")
        List<@Valid ItemPedidoCriacaoRequest> itens
) {
}