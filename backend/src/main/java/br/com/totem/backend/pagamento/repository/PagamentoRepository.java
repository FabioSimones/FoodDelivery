package br.com.totem.backend.pagamento.repository;

import br.com.totem.backend.pagamento.entity.Pagamento;
import br.com.totem.backend.pagamento.enums.StatusPagamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface PagamentoRepository extends JpaRepository<Pagamento, UUID> {

    boolean existsByPedido_IdAndStatusPagamentoIn(
            UUID pedidoId,
            Collection<StatusPagamento> status
    );

    Optional<Pagamento> findTopByPedido_IdOrderByCriadoEmDesc(UUID pedidoId);

    @Query("""
        SELECT p FROM Pagamento p
        JOIN FETCH p.pedido pe
        WHERE pe.id = :pedidoId
        ORDER BY p.criadoEm DESC
        LIMIT 1
        """)
    Optional<Pagamento> buscarUltimoPagamentoComPedido(UUID pedidoId);
}