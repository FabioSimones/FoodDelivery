package bt.com.totem.backend.restaurante.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RestauranteCriacaoRequest (
        @NotBlank(message = "O nome do restaurante é obrigatório.")
        @Size(max = 150, message = "O nome deve ter no máximo 150 caracteres.")
        String nome,

        @Size(max = 20, message = "O CNPJ deve ter no máximo 20 caracteres.")
        String cnpj,

        @Size(max = 255, message = "O endereço deve ter no máximo 255 caracteres.")
        String endereco
) {
}
