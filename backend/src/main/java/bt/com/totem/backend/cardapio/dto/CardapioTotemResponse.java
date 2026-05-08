package bt.com.totem.backend.cardapio.dto;

import java.util.List;
import java.util.UUID;

public record CardapioTotemResponse(
        UUID restauranteId,
        String restauranteNome,
        List<CategoriaCardapioTotemResponse> categorias,
        List<ProdutoCardapioTotemResponse> destaques,
        List<ProdutoCardapioTotemResponse> recomendados
) {
}