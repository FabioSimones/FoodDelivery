package br.com.totem.backend.usuario.dto;

import br.com.totem.backend.usuario.enums.PerfilUsuario;

import java.time.LocalDateTime;
import java.util.UUID;

public record UsuarioResponse(
        UUID id,
        UUID restauranteId,
        String restauranteNome,
        String nome,
        String email,
        PerfilUsuario perfil,
        Boolean ativo,
        LocalDateTime criadoEm,
        LocalDateTime atualizadoEm
) {
}
