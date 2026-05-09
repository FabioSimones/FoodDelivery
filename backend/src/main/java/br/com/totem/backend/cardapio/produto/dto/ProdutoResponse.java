package br.com.totem.backend.cardapio.produto.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ProdutoResponse(
        UUID id,
        UUID restauranteId,
        String restauranteNome,
        UUID categoriaId,
        String categoriaNome,
        String nome,
        String descricao,
        BigDecimal preco,
        String imagemUrl,
        Boolean disponivel,
        Boolean destaque,
        Boolean recomendado,
        Integer ordemExibicao,
        LocalDateTime criadoEm,
        LocalDateTime atualizadoEm
) {
}