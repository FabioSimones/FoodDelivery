package br.com.totem.backend.auth.dto;

import br.com.totem.backend.usuario.enums.PerfilUsuario;

import java.util.UUID;

public record LoginResponse(
        String accessToken,
        String tokenType,
        Long expiresInSeconds,
        UUID usuarioId,
        String nome,
        String email,
        PerfilUsuario perfil,
        UUID restauranteId,
        String restauranteNome
) {
}
