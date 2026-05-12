package br.com.totem.backend.pedido.repository;

import br.com.totem.backend.pedido.entity.Pedido;
import br.com.totem.backend.pedido.enums.StatusPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PedidoRepository extends JpaRepository<Pedido, UUID> {

    @Query("""
            SELECT DISTINCT p FROM Pedido p
            JOIN FETCH p.restaurante r
            LEFT JOIN FETCH p.itens i
            WHERE p.id = :id
            """)
    Optional<Pedido> buscarComItensPorId(UUID id);

    @Query("""
            SELECT DISTINCT p FROM Pedido p
            JOIN FETCH p.restaurante r
            LEFT JOIN FETCH p.itens i
            LEFT JOIN FETCH i.produto pr
            WHERE p.statusPedido IN :status
            ORDER BY p.criadoEm ASC
            """)
    List<Pedido> listarFilaCozinha(
            @Param("status") Collection<StatusPedido> status
    );

    @Query("""
            SELECT DISTINCT p FROM Pedido p
            JOIN FETCH p.restaurante r
            LEFT JOIN FETCH p.itens i
            LEFT JOIN FETCH i.produto pr
            WHERE r.id = :restauranteId
            AND p.statusPedido IN :status
            ORDER BY p.criadoEm ASC
            """)
    List<Pedido> listarFilaCozinhaPorRestaurante(
            @Param("restauranteId") UUID restauranteId,
            @Param("status") Collection<StatusPedido> status
    );
}