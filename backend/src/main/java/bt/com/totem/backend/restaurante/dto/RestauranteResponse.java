package bt.com.totem.backend.restaurante.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record RestauranteResponse(
        UUID id,
        String nome,
        String cnpj,
        String endereco,
        Boolean ativo,
        LocalDateTime criadoEm,
        LocalDateTime atualizadoEm
) {
}
