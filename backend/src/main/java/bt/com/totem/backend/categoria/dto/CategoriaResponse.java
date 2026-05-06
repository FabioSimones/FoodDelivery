package bt.com.totem.backend.categoria.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record CategoriaResponse(
        UUID id,
        UUID restauranteId,
        String restauranteNome,
        String nome,
        String descricao,
        Integer ordemExibicao,
        Boolean ativa,
        LocalDateTime criadoEm,
        LocalDateTime atualizadoEm
) {
}
