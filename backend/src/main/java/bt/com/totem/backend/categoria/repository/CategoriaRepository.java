package bt.com.totem.backend.categoria.repository;

import bt.com.totem.backend.categoria.entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;


public interface CategoriaRepository extends JpaRepository<Categoria, UUID> {

    @Query("""
            SELECT c FROM Categoria c
            JOIN FETCH c.restaurante r
            ORDER BY r.nome ASC, c.ordemExibicao ASC, c.nome ASC
            """)
    List<Categoria> listarTodasOrdenadas();

    @Query("""
            SELECT c FROM Categoria c
            JOIN FETCH c.restaurante r
            WHERE r.id = :restauranteId
            ORDER BY c.ordemExibicao ASC, c.nome ASC
            """)
    List<Categoria> listarPorRestauranteOrdenadas(UUID restauranteId);

    @Query("""
        SELECT c FROM Categoria c
        JOIN FETCH c.restaurante r
        WHERE r.id = :restauranteId
        AND c.ativa = true
        ORDER BY c.ordemExibicao ASC, c.nome ASC
        """)
    List<Categoria> listarAtivasPorRestauranteOrdenadas(UUID restauranteId);

    boolean existsByRestaurante_IdAndNomeIgnoreCase(UUID restauranteId, String nome);

    boolean existsByRestaurante_IdAndNomeIgnoreCaseAndIdNot(
            UUID restauranteId,
            String nome,
            UUID id
    );
}