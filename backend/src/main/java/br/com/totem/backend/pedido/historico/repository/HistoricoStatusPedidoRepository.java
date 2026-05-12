package br.com.totem.backend.pedido.historico.repository;

import br.com.totem.backend.pedido.historico.entity.HistoricoStatusPedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface HistoricoStatusPedidoRepository extends JpaRepository<HistoricoStatusPedido, UUID> {

    List<HistoricoStatusPedido> findAllByPedido_IdOrderByDataAlteracaoAsc(UUID pedidoId);
}
