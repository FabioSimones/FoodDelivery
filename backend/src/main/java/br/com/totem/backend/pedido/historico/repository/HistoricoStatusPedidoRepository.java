package br.com.totem.backend.pedido.historico.repository;

import br.com.totem.backend.pedido.historico.entity.HistoricoStatusPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface HistoricoStatusPedidoRepository extends JpaRepository<HistoricoStatusPedido, UUID> {

    @Query("""
            SELECT h FROM HistoricoStatusPedido h
            JOIN FETCH h.pedido p
            WHERE p.id = :pedidoId
            ORDER BY h.dataAlteracao ASC
            """)
    List<HistoricoStatusPedido> listarPorPedidoOrdenado(UUID pedidoId);
}
