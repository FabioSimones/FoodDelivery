package br.com.totem.backend.usuario.service;

import br.com.totem.backend.restaurante.entity.Restaurante;
import br.com.totem.backend.restaurante.repository.RestauranteRepository;
import br.com.totem.backend.shared.exception.ConflitoException;
import br.com.totem.backend.shared.exception.RecursoNaoEncontradoException;
import br.com.totem.backend.shared.exception.RegraNegocioException;
import br.com.totem.backend.usuario.dto.UsuarioCriacaoRequest;
import br.com.totem.backend.usuario.dto.UsuarioResponse;
import br.com.totem.backend.usuario.entity.Usuario;
import br.com.totem.backend.usuario.enums.PerfilUsuario;
import br.com.totem.backend.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RestauranteRepository restauranteRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UsuarioResponse criar(UsuarioCriacaoRequest request) {
        String emailNormalizado = normalizarEmail(request.email());

        if (usuarioRepository.existsByEmailIgnoreCase(emailNormalizado)) {
            throw new ConflitoException(
                    "EMAIL_DUPLICADO",
                    "Já existe um usuário cadastrado com este e-mail."
            );
        }

        Restaurante restaurante = definirRestaurante(request);

        Usuario usuario = Usuario.builder()
                .restaurante(restaurante)
                .nome(normalizarTextoObrigatorio(request.nome(), "O nome do usuário é obrigatório."))
                .email(emailNormalizado)
                .senhaHash(passwordEncoder.encode(request.senha()))
                .perfil(request.perfil())
                .ativo(true)
                .build();

        Usuario usuarioSalvo = usuarioRepository.save(usuario);

        return toResponse(usuarioSalvo);
    }

    @Transactional
    public UsuarioResponse criarSuperAdminBootstrap(UsuarioCriacaoRequest request) {
        if (usuarioRepository.count() > 0) {
            throw new ConflitoException(
                    "BOOTSTRAP_INDISPONIVEL",
                    "O usuário inicial já foi criado."
            );
        }

        UsuarioCriacaoRequest requestSuperAdmin = new UsuarioCriacaoRequest(
                null,
                request.nome(),
                request.email(),
                request.senha(),
                PerfilUsuario.SUPER_ADMIN
        );

        return criar(requestSuperAdmin);
    }

    private Restaurante definirRestaurante(UsuarioCriacaoRequest request) {
        if (request.perfil() == PerfilUsuario.SUPER_ADMIN) {
            return null;
        }

        if (request.restauranteId() == null) {
            throw new RegraNegocioException(
                    "RESTAURANTE_OBRIGATORIO",
                    "Usuários deste perfil devem estar vinculados a um restaurante."
            );
        }

        return restauranteRepository.findById(request.restauranteId())
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "RESTAURANTE_NAO_ENCONTRADO",
                        "Restaurante não encontrado."
                ));
    }

    private String normalizarEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new RegraNegocioException(
                    "EMAIL_INVALIDO",
                    "O e-mail do usuário é obrigatório."
            );
        }

        return email.trim().toLowerCase();
    }

    private String normalizarTextoObrigatorio(String texto, String mensagemErro) {
        if (texto == null || texto.isBlank()) {
            throw new RegraNegocioException(
                    "TEXTO_OBRIGATORIO",
                    mensagemErro
            );
        }

        return texto.trim();
    }

    private UsuarioResponse toResponse(Usuario usuario) {
        Restaurante restaurante = usuario.getRestaurante();

        return new UsuarioResponse(
                usuario.getId(),
                restaurante != null ? restaurante.getId() : null,
                restaurante != null ? restaurante.getNome() : null,
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getPerfil(),
                usuario.getAtivo(),
                usuario.getCriadoEm(),
                usuario.getAtualizadoEm()
        );
    }
}
