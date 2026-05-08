package bt.com.totem.backend.cardapio.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ProdutoCardapioTotemResponse(
        UUID id,
        String nome,
        String descricao,
        BigDecimal preco,
        String imagemUrl,
        Boolean destaque,
        Boolean recomendado,
        Integer ordemExibicao
) {
}