package br.com.totem.backend.pedido.repository;

import br.com.totem.backend.pedido.entity.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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
}