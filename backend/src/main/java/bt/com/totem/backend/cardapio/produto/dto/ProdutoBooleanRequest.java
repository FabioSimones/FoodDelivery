package bt.com.totem.backend.cardapio.produto.dto;

import jakarta.validation.constraints.NotNull;

public record ProdutoBooleanRequest(

        @NotNull(message = "O valor deve ser informado.")
        Boolean valor
) {
}
