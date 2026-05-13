package br.com.totem.backend.usuario.dto;

import br.com.totem.backend.usuario.enums.PerfilUsuario;
import jakarta.validation.constraints.*;

import java.util.UUID;

public record UsuarioCriacaoRequest(

        UUID restauranteId,

        @NotBlank(message = "O nome do usuário é obrigatório.")
        @Size(max = 150, message = "O nome deve ter no máximo 150 caracteres.")
        String nome,

        @NotBlank(message = "O e-mail é obrigatório.")
        @Email(message = "O e-mail informado é inválido.")
        @Size(max = 160, message = "O e-mail deve ter no máximo 160 caracteres.")
        String email,

        @NotBlank(message = "A senha é obrigatória.")
        @Size(min = 8, max = 80, message = "A senha deve ter entre 8 e 80 caracteres.")
        String senha,

        @NotNull(message = "O perfil do usuário é obrigatório.")
        PerfilUsuario perfil
) {
}
