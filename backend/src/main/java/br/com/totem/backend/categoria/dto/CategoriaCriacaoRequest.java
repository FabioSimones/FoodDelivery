package br.com.totem.backend.categoria.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CategoriaCriacaoRequest(
        @NotNull(message = "O restaurante é obrigatório.")
        UUID restauranteId,

        @NotBlank(message = "O nome da categoria é obrigatório.")
        @Size(max = 120, message = "O nome deve ter no máximo 120 caracteres.")
        String nome,

        @Size(max = 255, message = "A descrição deve ter no máximo 255 caracteres.")
        String descricao,

        @Min(value = 0, message = "A ordem de exibição não pode ser negativa.")
        Integer ordemExibicao
) {
}
