package br.com.totem.backend.cardapio.produto.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.UUID;

public record ProdutoCriacaoRequest(

        @NotNull(message = "O restaurante é obrigatório.")
        UUID restauranteId,

        @NotNull(message = "A categoria é obrigatória.")
        UUID categoriaId,

        @NotBlank(message = "O nome do produto é obrigatório.")
        @Size(max = 150, message = "O nome deve ter no máximo 150 caracteres.")
        String nome,

        @Size(max = 500, message = "A descrição deve ter no máximo 500 caracteres.")
        String descricao,

        @NotNull(message = "O preço é obrigatório.")
        @DecimalMin(value = "0.01", message = "O preço deve ser maior que zero.")
        BigDecimal preco,

        @Size(max = 500, message = "A URL da imagem deve ter no máximo 500 caracteres.")
        String imagemUrl,

        Boolean disponivel,

        Boolean destaque,

        Boolean recomendado,

        @Min(value = 0, message = "A ordem de exibição não pode ser negativa.")
        Integer ordemExibicao
) {
}
