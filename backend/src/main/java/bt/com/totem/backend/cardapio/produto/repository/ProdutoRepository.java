package bt.com.totem.backend.cardapio.produto.repository;

import bt.com.totem.backend.cardapio.produto.entity.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface ProdutoRepository extends JpaRepository<Produto, UUID> {

    @Query("""
            SELECT p FROM Produto p
            JOIN FETCH p.restaurante r
            JOIN FETCH p.categoria c
            ORDER BY r.nome ASC, c.ordemExibicao ASC, p.ordemExibicao ASC, p.nome ASC
            """)
    List<Produto> listarTodosOrdenados();

    @Query("""
            SELECT p FROM Produto p
            JOIN FETCH p.restaurante r
            JOIN FETCH p.categoria c
            WHERE r.id = :restauranteId
            ORDER BY c.ordemExibicao ASC, p.ordemExibicao ASC, p.nome ASC
            """)
    List<Produto> listarPorRestauranteOrdenados(UUID restauranteId);

    @Query("""
            SELECT p FROM Produto p
            JOIN FETCH p.restaurante r
            JOIN FETCH p.categoria c
            WHERE c.id = :categoriaId
            ORDER BY p.ordemExibicao ASC, p.nome ASC
            """)
    List<Produto> listarPorCategoriaOrdenados(UUID categoriaId);

    @Query("""
        SELECT p FROM Produto p
        JOIN FETCH p.restaurante r
        JOIN FETCH p.categoria c
        WHERE r.id = :restauranteId
        AND c.ativa = true
        AND p.disponivel = true
        ORDER BY c.ordemExibicao ASC, p.ordemExibicao ASC, p.nome ASC
        """)
    List<Produto> listarDisponiveisParaTotem(UUID restauranteId);

    boolean existsByRestaurante_IdAndNomeIgnoreCase(UUID restauranteId, String nome);

    boolean existsByRestaurante_IdAndNomeIgnoreCaseAndIdNot(
            UUID restauranteId,
            String nome,
            UUID id
    );
}