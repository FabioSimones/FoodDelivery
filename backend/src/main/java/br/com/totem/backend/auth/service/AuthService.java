package br.com.totem.backend.auth.service;

import br.com.totem.backend.auth.dto.LoginRequest;
import br.com.totem.backend.auth.dto.LoginResponse;
import br.com.totem.backend.restaurante.entity.Restaurante;
import br.com.totem.backend.shared.exception.RegraNegocioException;
import br.com.totem.backend.shared.security.JwtService;
import br.com.totem.backend.shared.security.UsuarioAutenticado;
import br.com.totem.backend.shared.security.UsuarioDetailsService;
import br.com.totem.backend.usuario.entity.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioDetailsService usuarioDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        UsuarioAutenticado usuarioAutenticado =
                (UsuarioAutenticado) usuarioDetailsService.loadUserByUsername(request.email());

        Usuario usuario = usuarioAutenticado.getUsuario();

        if (!Boolean.TRUE.equals(usuario.getAtivo())) {
            throw new RegraNegocioException(
                    "USUARIO_INATIVO",
                    "Este usuário está inativo."
            );
        }

        if (!passwordEncoder.matches(request.senha(), usuario.getSenhaHash())) {
            throw new RegraNegocioException(
                    "CREDENCIAIS_INVALIDAS",
                    "E-mail ou senha inválidos."
            );
        }

        String token = jwtService.gerarToken(usuario);
        Restaurante restaurante = usuario.getRestaurante();

        return new LoginResponse(
                token,
                "Bearer",
                jwtService.getExpirationSeconds(),
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getPerfil(),
                restaurante != null ? restaurante.getId() : null,
                restaurante != null ? restaurante.getNome() : null
        );
    }
}
