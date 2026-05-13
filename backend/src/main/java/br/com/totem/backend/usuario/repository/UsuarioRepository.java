package br.com.totem.backend.usuario.repository;

import br.com.totem.backend.usuario.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {

    boolean existsByEmailIgnoreCase(String email);

    @Query("""
            SELECT u FROM Usuario u
            LEFT JOIN FETCH u.restaurante r
            WHERE LOWER(u.email) = LOWER(:email)
            """)
    Optional<Usuario> buscarPorEmailComRestaurante(String email);
}