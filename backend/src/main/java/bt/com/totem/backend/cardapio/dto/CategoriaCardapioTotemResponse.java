package bt.com.totem.backend.cardapio.dto;

import java.util.List;
import java.util.UUID;

public record CategoriaCardapioTotemResponse(
        UUID id,
        String nome,
        String descricao,
        Integer ordemExibicao,
        List<ProdutoCardapioTotemResponse> produtos
) {
}