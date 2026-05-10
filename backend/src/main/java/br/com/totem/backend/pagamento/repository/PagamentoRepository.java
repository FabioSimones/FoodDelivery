package br.com.totem.backend.pagamento.repository;

import br.com.totem.backend.pagamento.entity.Pagamento;
import br.com.totem.backend.pagamento.enums.FormaPagamento;
import br.com.totem.backend.pagamento.enums.StatusPagamento;
import br.com.totem.backend.pedido.enums.StatusPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PagamentoRepository extends JpaRepository<Pagamento, UUID> {

    boolean existsByPedido_IdAndStatusPagamentoIn(
            UUID pedidoId,
            Collection<StatusPagamento> status
    );

    Optional<Pagamento> findTopByPedido_IdOrderByCriadoEmDesc(UUID pedidoId);

    Optional<Pagamento> findTopByPedido_IdAndFormaPagamentoAndStatusPagamentoOrderByCriadoEmDesc(
            UUID pedidoId,
            FormaPagamento formaPagamento,
            StatusPagamento statusPagamento
    );

    @Query("""
            SELECT pagamento FROM Pagamento pagamento
            JOIN FETCH pagamento.pedido pedido
            JOIN FETCH pedido.restaurante restaurante
            WHERE pagamento.formaPagamento = :formaPagamento
            AND pagamento.statusPagamento = :statusPagamento
            AND pedido.statusPedido = :statusPedido
            ORDER BY pedido.criadoEm ASC
            """)
    List<Pagamento> listarPagamentosPendentesCaixa(
            @Param("formaPagamento") FormaPagamento formaPagamento,
            @Param("statusPagamento") StatusPagamento statusPagamento,
            @Param("statusPedido") StatusPedido statusPedido
    );
}